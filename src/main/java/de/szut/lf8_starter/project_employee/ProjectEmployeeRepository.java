package de.szut.lf8_starter.project_employee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectEmployeeRepository extends JpaRepository<ProjectEmployeeEntity, Long> {

    /**
     * Findet alle Projektzuordnungen für einen bestimmten Mitarbeiter.
     */
    List<ProjectEmployeeEntity> findAllByEmployeeId(Long employeeId);

    /**
     * Findet eine spezifische Zuordnung anhand von Projekt-ID und Mitarbeiter-ID.
     */
    Optional<ProjectEmployeeEntity> findByProjectIdAndEmployeeId(Long projectId, Long employeeId);
}
