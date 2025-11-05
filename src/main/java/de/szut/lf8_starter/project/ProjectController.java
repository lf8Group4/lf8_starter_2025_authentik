package de.szut.lf8_starter.project;


import de.szut.lf8_starter.mapping.MappingService;
import de.szut.lf8_starter.project.dto.CreateProjectDto;
import de.szut.lf8_starter.project.dto.GetProjectDto;
import de.szut.lf8_starter.project.dto.ProjectEmployeesGetDto;
import de.szut.lf8_starter.project_employee.ProjectEmployeeService;
import de.szut.lf8_starter.project_employee.dto.AddProjectEmployeeDto;
import de.szut.lf8_starter.project_employee.dto.AddedProjectEmployeeGetDto;
import de.szut.lf8_starter.project_employee.dto.DeleteConfirmationDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController implements ProjectControllerOpenAPI {

    private final ProjectService service;
    private final MappingService mappingService;
    private final ProjectEmployeeService projectEmployeeService;

    public ProjectController(ProjectService service, MappingService mappingService, ProjectEmployeeService projectEmployeeService) {
        this.service = service;
        this.mappingService = mappingService;
        this.projectEmployeeService = projectEmployeeService;
    }

    @Override
    @PostMapping
    public ResponseEntity<GetProjectDto> createProject(@RequestBody @Valid CreateProjectDto createProjectDto) {
        ProjectEntity newProject = mappingService.mapCreateProjectDtoToProject(createProjectDto);
        newProject = service.create(newProject);
        GetProjectDto responseDto = mappingService.mapProjectEntityToGetProjectDto(newProject);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }


    @Override
    @PutMapping("/{id}")
    public ResponseEntity<GetProjectDto> updateProject(@PathVariable Long id, @RequestBody @Valid CreateProjectDto updateProjectDto) {
        ProjectEntity projectToUpdate = mappingService.mapCreateProjectDtoToProject(updateProjectDto);
        ProjectEntity updatedProject = service.update(id, projectToUpdate);
        GetProjectDto responseDto = mappingService.mapProjectEntityToGetProjectDto(updatedProject);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Override
    @GetMapping()
    public ResponseEntity<List<GetProjectDto>> getAllProjects() {
        List<ProjectEntity> projects = this.service.getAll();
        List<GetProjectDto> list = new LinkedList<>();
        for (ProjectEntity project : projects) {
            list.add(this.mappingService.mapProjectEntityToGetProjectDto(project));
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<GetProjectDto> getProjectById(@PathVariable Long id) {
        ProjectEntity project = service.getById(id);
        GetProjectDto dto = mappingService.mapProjectEntityToGetProjectDto(project);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable Long id) {
        service.delete(id);
    }

    // POST /projects/{id}/employees (Mitarbeiter hinzufügen)
    @Override
    @PostMapping("/{id}/employees")
    public ResponseEntity<AddedProjectEmployeeGetDto> addEmployeeToProject(
            @PathVariable("id") Long id,
            @RequestBody @Valid AddProjectEmployeeDto dto) {

        AddedProjectEmployeeGetDto response = projectEmployeeService.addEmployeeToProject(id, dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //DELETE /projects/{projectId}/employees/{employeeId} (Mitarbeiter entfernen)
    @DeleteMapping("/{projectId}/employees/{employeeId}")
    public ResponseEntity<DeleteConfirmationDto> removeEmployeeFromProject(
            @PathVariable Long projectId,
            @PathVariable Long employeeId) {

        DeleteConfirmationDto response = projectEmployeeService.removeEmployeeFromProject(projectId, employeeId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // GET /projects/{id}/employees (Zugewiesene Mitarbeiter abrufen)
    @GetMapping("/{id}/employees")
    public ResponseEntity<ProjectEmployeesGetDto> getProjectEmployees(@PathVariable Long id) {
        ProjectEmployeesGetDto dto = service.getProjectEmployees(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

}