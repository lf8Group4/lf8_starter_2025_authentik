package de.szut.lf8_starter.project;


import de.szut.lf8_starter.mapping.MappingService;
import de.szut.lf8_starter.project.dto.GetProjectDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService service;
    private final MappingService mappingService;

    public ProjectController(ProjectService service, MappingService mappingService) {
        this.service = service;
        this.mappingService = mappingService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetProjectDto> getProjectById(@PathVariable Long id) {
        ProjectEntity project = service.getById(id);
        GetProjectDto dto = mappingService.mapProjectEntityToGetProjectDto(project);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteProject(@PathVariable Long id) {
        service.delete(id);
    }
}