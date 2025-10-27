package de.szut.lf8_starter.mapping;

import de.szut.lf8_starter.project.ProjectEntity;
import de.szut.lf8_starter.project.dto.CreateProjectDto;
import de.szut.lf8_starter.project.dto.GetProjectDto;
import org.springframework.stereotype.Service;

@Service
public class MappingService {

    public ProjectEntity mapCreateProjectDtoToProject(CreateProjectDto dto) {
        ProjectEntity newProject = new ProjectEntity();
        newProject.setDesignation(dto.getDesignation());
        newProject.setResponsibleEmployeeId(dto.getResponsibleEmployeeId());
        newProject.setCustomerId(dto.getCustomerId());
        newProject.setCustomerContactPerson(dto.getCustomerContactPerson());
        newProject.setComment(dto.getComment());
        newProject.setStartDate(dto.getStartDate());
        newProject.setPlannedEndDate(dto.getPlannedEndDate());
        newProject.setEndDate(dto.getEndDate());
        return newProject;
    }

    public GetProjectDto mapProjectEntityToGetProjectDto(ProjectEntity project) {
        GetProjectDto dto = new GetProjectDto();
        dto.setId(project.getId());
        dto.setDesignation(project.getDesignation());
        dto.setResponsibleEmployeeId(project.getResponsibleEmployeeId());
        dto.setCustomerId(project.getCustomerId());
        dto.setCustomerContactPerson(project.getCustomerContactPerson());
        dto.setComment(project.getComment());
        dto.setStartDate(project.getStartDate());
        dto.setPlannedEndDate(project.getPlannedEndDate());
        dto.setEndDate(project.getEndDate());
        return dto;
    }
}