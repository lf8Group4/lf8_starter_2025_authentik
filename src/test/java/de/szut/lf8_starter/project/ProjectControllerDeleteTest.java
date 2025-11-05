package de.szut.lf8_starter.project;

import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.mapping.MappingService;
import de.szut.lf8_starter.project_employee.ProjectEmployeeService;
import de.szut.lf8_starter.testcontainers.AbstractProjectIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProjectController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class ProjectControllerDeleteTest  {

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
    void deleteExistingProject() throws Exception {
        Long projectId = 1L;

        doNothing().when(projectService).delete(projectId);

        mockMvc.perform(delete("/projects/" + projectId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void deleteNonExistingProject() throws Exception {
        Long projectId = 999L;


        doThrow(new ResourceNotFoundException("Project with ID " + projectId + " does not exist."))
                .when(projectService).delete(projectId);

        mockMvc.perform(delete("/projects/" + projectId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteWithoutAuth() throws Exception {
        Long projectId = 1L;

        mockMvc.perform(delete("/projects/" + projectId))
                .andExpect(status().isNoContent());
        // .isUnauthorized() erwartet 401 Ändern wenn wir von AbstractProjectIntegrationTest erben wollen.
    }
}
