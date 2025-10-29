package de.szut.lf8_starter.project;

import de.szut.lf8_starter.project.dto.GetProjectDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

public interface ProjectControllerOpenAPI {

    @Operation(summary = "Retrieve a list of all projects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of projects retrieved",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = GetProjectDto.class)))}),
            @ApiResponse(responseCode = "401", description = "not authorized",
                    content = @Content)
    })
    ResponseEntity<List<GetProjectDto>> getAllProjects();

    @Operation(summary = "Retrieve a single project by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projekt found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetProjectDto.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
    })
    ResponseEntity<GetProjectDto> getProjectById(@PathVariable Long id);

    @Operation(summary = "deletes a Project by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Project deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteProject(@PathVariable Long id);
}
