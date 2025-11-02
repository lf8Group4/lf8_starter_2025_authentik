package de.szut.lf8_starter.employee;

import de.szut.lf8_starter.project_employee.dto.EmployeeProjectsGetDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface EmployeeControllerOpenAPI {

    @Operation(summary = "Retrieves all projects an employee is assigned to.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of projects retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmployeeProjectsGetDto.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content)
    })
    ResponseEntity<EmployeeProjectsGetDto> getProjectsByEmployee(@PathVariable Long employeeId);
}