package de.szut.lf8_starter.project.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectEmployeesGetDto {
    private Long projectId;
    private String projectName;
    private List<EmployeeDto> employees;
}