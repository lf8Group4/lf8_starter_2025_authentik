package de.szut.lf8_starter.project.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GetProjectDto {

    private Long id;
    private String designation;
    private Long responsibleEmployeeId;
    private Long customerId;
    private String customerContactPerson;
    private String comment;
    private LocalDate startDate;
    private LocalDate plannedEndDate;
    private LocalDate endDate;
}
