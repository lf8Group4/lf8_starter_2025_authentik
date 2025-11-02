package de.szut.lf8_starter.project_employee.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeProjectDto {
    private Long projectId;
    private String projectName;
    private LocalDate startDate;
    private LocalDate plannedEndDate;
    private String qualification;
}