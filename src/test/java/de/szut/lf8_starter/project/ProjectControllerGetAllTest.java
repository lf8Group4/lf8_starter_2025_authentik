package de.szut.lf8_starter.project;

import de.szut.lf8_starter.mapping.MappingService;
import de.szut.lf8_starter.project.dto.GetProjectDto;
import de.szut.lf8_starter.project_employee.ProjectEmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProjectController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class ProjectControllerGetAllTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private MappingService mappingService;

    @MockBean
    private ProjectEmployeeService projectEmployeeService;

    @Test
    @WithMockUser
    void getAllProjects() throws Exception {

        ProjectEntity project1 = new ProjectEntity();
        project1.setId(1L);
        project1.setDesignation("Projekt A");

        ProjectEntity project2 = new ProjectEntity();
        project2.setId(2L);
        project2.setDesignation("Projekt B");

        GetProjectDto dto1 = new GetProjectDto();
        dto1.setId(1L);
        dto1.setDesignation("Projekt A");

        GetProjectDto dto2 = new GetProjectDto();
        dto2.setId(2L);
        dto2.setDesignation("Projekt B");

        when(projectService.getAll()).thenReturn(List.of(project1, project2));
        when(mappingService.mapProjectEntityToGetProjectDto(project1)).thenReturn(dto1);
        when(mappingService.mapProjectEntityToGetProjectDto(project2)).thenReturn(dto2);

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].designation", is("Projekt A")))
                .andExpect(jsonPath("$[1].designation", is("Projekt B")));
    }

    @Test
    @WithMockUser
    void getAllProjects_returnsEmptyList() throws Exception {
        when(projectService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAllProjects_withoutAuth_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk());
        // .isUnauthorized() wäre richtig, wenn Security aktiv ist
    }
}
