package de.szut.lf8_starter.project_employee.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeProjectsGetDto {
    private Long employeeNumber;
    private List<EmployeeProjectDto> projects;
}