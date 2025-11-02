package de.szut.lf8_starter.project;

import de.szut.lf8_starter.project.dto.CreateProjectDto;
import de.szut.lf8_starter.project.dto.GetProjectDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public GetProjectDto create(CreateProjectDto dto) {
        ProjectEntity entity = toEntity(dto);
        ProjectEntity saved = projectRepository.save(entity);
        return toDto(saved);
    }



    private ProjectEntity toEntity(CreateProjectDto dto) {
        ProjectEntity e = new ProjectEntity();
        e.setDesignation(dto.getDesignation());
        e.setResponsibleEmployeeId(dto.getResponsibleEmployeeId());
        e.setCustomerId(dto.getCustomerId());
        e.setCustomerContactPerson(dto.getCustomerContactPerson());
        e.setComment(dto.getComment());
        e.setStartDate(dto.getStartDate());
        e.setPlannedEndDate(dto.getPlannedEndDate());
        e.setEndDate(dto.getEndDate());
        return e;
    }

    private GetProjectDto toDto(ProjectEntity e) {
        GetProjectDto out = new GetProjectDto();
        out.setId(e.getId());
        out.setDesignation(e.getDesignation());
        out.setResponsibleEmployeeId(e.getResponsibleEmployeeId());
        out.setCustomerId(e.getCustomerId());
        out.setCustomerContactPerson(e.getCustomerContactPerson());
        out.setComment(e.getComment());
        out.setStartDate(e.getStartDate());
        out.setPlannedEndDate(e.getPlannedEndDate());
        out.setEndDate(e.getEndDate());
        return out;
    }
}
