package de.szut.lf8_starter.project_employee;

import de.szut.lf8_starter.project.ProjectEntity;
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
@Table(name = "project_employee")
public class ProjectEmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "required_qualification", nullable = false)
    private String requiredQualification;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectEmployeeEntity that)) return false;

        if (!project.equals(that.project)) return false;
        return employeeId.equals(that.employeeId);
    }

    @Override
    public int hashCode() {
        int result = project.hashCode();
        result = 31 * result + employeeId.hashCode();
        return result;
    }
}