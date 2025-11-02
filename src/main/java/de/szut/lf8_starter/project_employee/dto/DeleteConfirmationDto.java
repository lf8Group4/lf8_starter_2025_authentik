package de.szut.lf8_starter.project_employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteConfirmationDto {
    private String status;
    private String message;
}