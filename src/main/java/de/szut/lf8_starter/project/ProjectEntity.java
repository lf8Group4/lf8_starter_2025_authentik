package de.szut.lf8_starter.project;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "project")
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;


    private String designation;

    // TODO: verantwortlicherMitarbeiterId

    // TODO: Kunden-ID

    // TODO: Kundenzuständiger

    // TODO: Kommentar

    // TODO: Startdatum

    // TODO: geplantesEnddatum

    // TODO: Enddatum

}