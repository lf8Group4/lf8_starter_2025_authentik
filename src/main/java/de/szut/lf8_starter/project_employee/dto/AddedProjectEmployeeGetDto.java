package de.szut.lf8_starter.project_employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddedProjectEmployeeGetDto {
    private Long projectId;
    private String projectName;
    private Long employeeNumber;
    private String employeeName;
    private String assignedQualification;
}