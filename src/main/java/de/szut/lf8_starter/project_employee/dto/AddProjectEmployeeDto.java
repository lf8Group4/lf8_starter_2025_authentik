package de.szut.lf8_starter.project_employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddProjectEmployeeDto {

    @NotNull
    private Long employeeId;

    @NotBlank
    private String requiredQualification;
}