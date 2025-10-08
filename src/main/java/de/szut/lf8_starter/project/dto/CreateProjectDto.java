package de.szut.lf8_starter.project.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateProjectDto {

    @NotNull
    private String designation;

    @NotNull
    private Long responsibleEmployeeId;

    @NotNull
    private Long customerId;

    private String customerContactPerson;

    @NotNull
    private String comment;

    private LocalDate startDate;

    private LocalDate plannedEndDate;

    private LocalDate endDate;
}
