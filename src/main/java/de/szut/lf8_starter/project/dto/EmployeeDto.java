package de.szut.lf8_starter.project.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {
    private Long employeeNumber;
    private String name;
    private List<String> qualifications;
}