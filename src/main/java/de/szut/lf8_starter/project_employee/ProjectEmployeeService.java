package de.szut.lf8_starter.project_employee;

import de.szut.lf8_starter.employee.EmployeeServiceClient;
import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.exceptionHandling.TimeConflictException;
import de.szut.lf8_starter.project.ProjectEntity;
import de.szut.lf8_starter.project.ProjectService;
import de.szut.lf8_starter.project.dto.EmployeeDto;
import de.szut.lf8_starter.project_employee.dto.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectEmployeeService {
    private final ProjectService projectService;
    private final ProjectEmployeeRepository employeeRepository;
    private final EmployeeServiceClient employeeServiceClient;

    public ProjectEmployeeService(ProjectService projectService, ProjectEmployeeRepository employeeRepository, EmployeeServiceClient employeeServiceClient) {
        this.projectService = projectService;
        this.employeeRepository = employeeRepository;
        this.employeeServiceClient = employeeServiceClient;
    }

    /**
     * Fügt einen Mitarbeiter zu einem Projekt hinzu, führt dabei 4 Validierungen durch.
     */
    public AddedProjectEmployeeGetDto addEmployeeToProject(Long projectId, AddProjectEmployeeDto dto) {
        // (404)
        ProjectEntity project = projectService.getById(projectId); // Wirft 404, wenn nicht gefunden

        // (404)
        EmployeeDto employeeDetails;
        try {
            employeeDetails = employeeServiceClient.getEmployeeDetails(dto.getEmployeeId());
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResourceNotFoundException("Mitarbeiter mit der Mitarbeiternummer " + dto.getEmployeeId() + " existiert nicht.");
            }
            throw ex;
        }

        // (400 Bad Request)
        if (employeeDetails.getQualifications() == null ||
                !employeeDetails.getQualifications().contains(dto.getRequiredQualification())) {

            throw new IllegalArgumentException(
                    "Mitarbeiter hat die Qualifikation " + dto.getRequiredQualification() + " nicht."
            );
        }

        // (409 Conflict)
        checkTimeConflict(project.getStartDate(), project.getPlannedEndDate(), dto.getEmployeeId(), projectId);

        ProjectEmployeeEntity projectEmployee = new ProjectEmployeeEntity();
        projectEmployee.setProject(project);
        projectEmployee.setEmployeeId(dto.getEmployeeId());
        projectEmployee.setRequiredQualification(dto.getRequiredQualification());

        employeeRepository.save(projectEmployee);

        return new AddedProjectEmployeeGetDto(
                project.getId(),
                project.getDesignation(),
                employeeDetails.getEmployeeNumber(),
                employeeDetails.getName(),
                dto.getRequiredQualification()
        );
    }

    /**
     * Entfernt einen Mitarbeiter aus einem Projekt.
     */
    public DeleteConfirmationDto removeEmployeeFromProject(Long projectId, Long employeeId) {
        projectService.getById(projectId);

        ProjectEmployeeEntity assignment = employeeRepository
                .findByProjectIdAndEmployeeId(projectId, employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Mitarbeiter mit der Mitarbeiternummer " + employeeId +
                                " arbeitet im Projekt mit der ID " + projectId + " nicht."
                ));

        employeeRepository.delete(assignment);

        return new DeleteConfirmationDto("SUCCESS",
                "Mitarbeiter " + employeeId + " wurde erfolgreich aus Projekt " + projectId + " entfernt.");
    }

    /**
     * Prüft, ob der Mitarbeiter im Projektzeitraum bereits an anderen Projekten arbeitet.
     */
    private void checkTimeConflict(LocalDate newProjectStart, LocalDate newProjectEnd, Long employeeId, Long currentProjectId) {
        List<ProjectEmployeeEntity> employeeAssignments = employeeRepository.findAllByEmployeeId(employeeId);

        for (ProjectEmployeeEntity assignment : employeeAssignments) {
            if (assignment.getProject().getId().equals(currentProjectId)) {
                continue;
            }

            ProjectEntity existingProject = assignment.getProject();
            LocalDate existingStart = existingProject.getStartDate();
            LocalDate existingEnd = existingProject.getPlannedEndDate();

            boolean overlaps =
                    (newProjectStart.isBefore(existingEnd) || newProjectStart.isEqual(existingEnd)) &&
                            (newProjectEnd.isAfter(existingStart) || newProjectEnd.isEqual(existingStart));

            if (overlaps) {
                String period = existingStart.toString() + " bis " + existingEnd.toString();
                throw new TimeConflictException(
                        "Mitarbeiter ist im Zeitraum " + period + " schon verplant."
                );
            }
        }
    }

    /**
     * Ruft alle Projekte ab, an denen ein Mitarbeiter beteiligt ist.
     */
    public EmployeeProjectsGetDto getProjectsByEmployee(Long employeeId) {
        // KORRIGIERTE LOGIK: Prüft explizit auf die Existenz des Mitarbeiters (404 Fehlerbehebung)
        if (!employeeServiceClient.employeeExists(employeeId)) {
            throw new ResourceNotFoundException("Mitarbeiter mit der Mitarbeiternummer " + employeeId + " existiert nicht.");
        }

        List<ProjectEmployeeEntity> assignments = employeeRepository.findAllByEmployeeId(employeeId);

        List<EmployeeProjectDto> employeeProjects = assignments.stream()
                .map(assignment -> {
                    ProjectEntity project = assignment.getProject();
                    return new EmployeeProjectDto(
                            project.getId(),
                            project.getDesignation(),
                            project.getStartDate(),
                            project.getPlannedEndDate(),
                            assignment.getRequiredQualification()
                    );
                })
                .collect(Collectors.toList());

        return new EmployeeProjectsGetDto(employeeId, employeeProjects);
    }
}