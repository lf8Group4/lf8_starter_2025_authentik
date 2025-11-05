package de.szut.lf8_starter.project;

import de.szut.lf8_starter.customer.CustomerServiceClient;
import de.szut.lf8_starter.employee.EmployeeServiceClient;
import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.project.dto.EmployeeDto;
import de.szut.lf8_starter.project.dto.ProjectEmployeesGetDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final EmployeeServiceClient employeeServiceClient;
    private final CustomerServiceClient customerServiceClient;

    public ProjectService(ProjectRepository projectRepository, EmployeeServiceClient employeeServiceClient, CustomerServiceClient customerServiceClient) {
        this.projectRepository = projectRepository;
        this.employeeServiceClient = employeeServiceClient;
        this.customerServiceClient = customerServiceClient;

    }

    /**
     * Erstellt ein neues Projekt. Führt Validierungen gegen externe Services durch.
     */
    public ProjectEntity create(ProjectEntity projectEntity) {
        if (!employeeServiceClient.employeeExists(projectEntity.getResponsibleEmployeeId())) {
            throw new ResourceNotFoundException("Responsible Employee with ID " + projectEntity.getResponsibleEmployeeId() + " does not exist.");
        }

        if (!customerServiceClient.customerExists(projectEntity.getCustomerId())) {
            throw new ResourceNotFoundException("Customer with ID " + projectEntity.getCustomerId() + " does not exist.");
        }

        return projectRepository.save(projectEntity);
    }

    /**
     * Aktualisiert ein bestehendes Projekt.
     */
    public ProjectEntity update(Long id, ProjectEntity updateEntity) {
        ProjectEntity existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID " + id + " does not exist."));

        if (!updateEntity.getResponsibleEmployeeId().equals(existingProject.getResponsibleEmployeeId()) &&
                !employeeServiceClient.employeeExists(updateEntity.getResponsibleEmployeeId())) {
            throw new ResourceNotFoundException("Responsible Employee with ID " + updateEntity.getResponsibleEmployeeId() + " does not exist.");
        }

        if (!updateEntity.getCustomerId().equals(existingProject.getCustomerId()) &&
                !customerServiceClient.customerExists(updateEntity.getCustomerId())) {
            throw new ResourceNotFoundException("Customer with ID " + updateEntity.getCustomerId() + " does not exist.");
        }

        existingProject.setDesignation(updateEntity.getDesignation());
        existingProject.setResponsibleEmployeeId(updateEntity.getResponsibleEmployeeId());
        existingProject.setCustomerId(updateEntity.getCustomerId());
        existingProject.setCustomerContactPerson(updateEntity.getCustomerContactPerson());
        existingProject.setComment(updateEntity.getComment());
        existingProject.setStartDate(updateEntity.getStartDate());
        existingProject.setPlannedEndDate(updateEntity.getPlannedEndDate());
        existingProject.setEndDate(updateEntity.getEndDate());

        return projectRepository.save(existingProject);
    }

    /**
     * Ruft ein Projekt anhand der ID ab.
     */
    public ProjectEntity getById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID " + id + " does not exist."));
    }

    /**
     * Löscht ein Projekt anhand der ID.
     */
    public void delete(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project with ID " + id + " does not exist.");
        }
        projectRepository.deleteById(id);
    }

    /**
     * Ruft alle Projekte ab.
     */
    public List<ProjectEntity> getAll() {
        return this.projectRepository.findAll();
    }

    /**
     * Ruft die Details der am Projekt beteiligten Mitarbeiter ab.
     * Nutzt die gespeicherte n:m-Beziehung.
     */
    public ProjectEmployeesGetDto getProjectEmployees(Long projectId) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Projekt mit der ID " + projectId + " existiert nicht."));

        List<EmployeeDto> employeeList = project.getEmployees().stream()
                .map(employeeAssignment -> {
                    try {
                        return employeeServiceClient.getEmployeeDetails(employeeAssignment.getEmployeeId());
                    } catch (HttpClientErrorException ex) {
                        return new EmployeeDto(
                                employeeAssignment.getEmployeeId(),
                                "Mitarbeiterdetails nicht abrufbar (ID: " + employeeAssignment.getEmployeeId() + " Fehler: " + ex.getStatusCode() + ")",
                                Collections.singletonList("Fehler")
                        );
                    }
                })
                .collect(Collectors.toList());

        if (employeeList.stream().noneMatch(e -> e.getEmployeeNumber().equals(project.getResponsibleEmployeeId()))) {
            try {
                employeeList.add(employeeServiceClient.getEmployeeDetails(project.getResponsibleEmployeeId()));
            } catch (HttpClientErrorException ex) {
            }
        }

        return new ProjectEmployeesGetDto(
                project.getId(),
                project.getDesignation(),
                employeeList
        );
    }
}