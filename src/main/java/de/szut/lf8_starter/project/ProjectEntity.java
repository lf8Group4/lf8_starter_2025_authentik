package de.szut.lf8_starter.project;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "project")
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String designation;


    @Column(name = "responsible_employee_id", nullable = false)
    private Long responsibleEmployeeId;


    @Column(name = "customer_id",nullable = false)
    private Long customerId;


    @Column(name = "customer_contact_person")
    private String customerContactPerson;


    @Column(name = "comment",nullable = false)
    private String comment;


    @Column(name = "start_date")
    private LocalDate startDate;


    @Column(name = "planned_end_date")
    private LocalDate plannedEndDate;


    @Column(name = "end_date")
    private LocalDate endDate;

}