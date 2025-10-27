package de.szut.lf8_starter.project;


import de.szut.lf8_starter.mapping.MappingService;
import org.springframework.http.HttpStatus;
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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteProject(@PathVariable Long id) {
        service.delete(id);
    }
}