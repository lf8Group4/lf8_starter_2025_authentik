package de.szut.lf8_starter.project;

import de.szut.lf8_starter.project.dto.CreateProjectDto;
import de.szut.lf8_starter.project.dto.GetProjectDto;
import de.szut.lf8_starter.project.dto.ProjectEmployeesGetDto;
import de.szut.lf8_starter.project_employee.dto.AddProjectEmployeeDto;
import de.szut.lf8_starter.project_employee.dto.AddedProjectEmployeeGetDto;
import de.szut.lf8_starter.project_employee.dto.DeleteConfirmationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;


import java.util.List;

public interface ProjectControllerOpenAPI {

    @Operation(summary = "Creates a new project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Project created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetProjectDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid JSON or validation error (z.B. verantwortlicher Mitarbeiter/Kunde nicht gefunden)",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized",
                    content = @Content)
    })
    ResponseEntity<GetProjectDto> createProject(@RequestBody(description = "Project details to create") CreateProjectDto dto);


    @Operation(summary = "Updates an existing project by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetProjectDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid JSON or validation error (z.B. verantwortlicher Mitarbeiter/Kunde nicht gefunden)",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content)
    })
    ResponseEntity<GetProjectDto> updateProject(@PathVariable Long id, @RequestBody(description = "Updated project details") CreateProjectDto dto);



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

    @Operation(summary = "Adds an employee to a project with a required qualification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee successfully added to project",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AddedProjectEmployeeGetDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input data or qualification check failed (Mitarbeiter hat Qualifikation nicht)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Project or Employee not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Time conflict: Employee is already assigned to another project in the specified period", content = @Content)
    })
    ResponseEntity<AddedProjectEmployeeGetDto> addEmployeeToProject(@PathVariable Long id, @RequestBody AddProjectEmployeeDto dto);

    @Operation(summary = "Removes an employee from a project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee successfully removed from project",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeleteConfirmationDto.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Project not found or employee not assigned to project", content = @Content)
    })
    ResponseEntity<DeleteConfirmationDto> removeEmployeeFromProject(@PathVariable Long projectId, @PathVariable Long employeeId);

    @Operation(summary = "Retrieves all employees assigned to a specific project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of employees retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProjectEmployeesGetDto.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
    })
    ResponseEntity<ProjectEmployeesGetDto> getProjectEmployees(@PathVariable Long id);
}