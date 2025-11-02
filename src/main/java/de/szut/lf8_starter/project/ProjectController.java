package de.szut.lf8_starter.project;


import de.szut.lf8_starter.mapping.MappingService;
import de.szut.lf8_starter.project.dto.CreateProjectDto;
import de.szut.lf8_starter.project.dto.GetProjectDto;
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

    public ProjectController(ProjectService service, MappingService mappingService) {
        this.service = service;
        this.mappingService = mappingService;
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
}