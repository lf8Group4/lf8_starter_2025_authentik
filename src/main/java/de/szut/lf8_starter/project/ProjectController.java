package de.szut.lf8_starter.project;

import de.szut.lf8_starter.project.dto.CreateProjectDto;
import de.szut.lf8_starter.project.dto.GetProjectDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<GetProjectDto> create(@Valid @RequestBody CreateProjectDto dto) {
        GetProjectDto created = projectService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created); // HTTP 201 + JSON
    }
}
