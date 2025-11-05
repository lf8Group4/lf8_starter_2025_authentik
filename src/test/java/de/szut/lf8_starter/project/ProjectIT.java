package de.szut.lf8_starter.project;

import de.szut.lf8_starter.employee.EmployeeServiceClient;
import de.szut.lf8_starter.project_employee.ProjectEmployeeRepository;
import de.szut.lf8_starter.project.dto.EmployeeDto;
import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ProjectIT extends AbstractIntegrationTest {

    private final String ENDPOINT = "/projects";
    // IDs zur Simulation von existierenden/nicht existierenden Mitarbeitern
    private final Long EMPLOYEE_ID_VALID = 100L;
    private final Long EMPLOYEE_ID_INVALID = 999L;
    private final Long CUSTOMER_ID = 5L;
    private final String VALID_QUALIFICATION = "Java-Entwickler";
    private final String INVALID_QUALIFICATION = "Datenbank-Admin"; // Wird für 400-Test verwendet

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectEmployeeRepository projectEmployeeRepository;

    @MockBean
    private EmployeeServiceClient employeeServiceClient;

    private EmployeeDto createMockEmployee(Long id, String qualification) {
        return new EmployeeDto(id, "Max Mustermann", List.of(qualification));
    }

    private String createProjectJson(Long responsibleEmployeeId, LocalDate startDate, LocalDate plannedEndDate) {
        return String.format("""
                {
                    "designation": "Testprojekt",
                    "responsibleEmployeeId": %d,
                    "customerId": %d,
                    "customerContactPerson": "Max Mustermann",
                    "comment": "Integrations-Testlauf",
                    "startDate": "%s",
                    "plannedEndDate": "%s"
                }
                """, responsibleEmployeeId, CUSTOMER_ID, startDate, plannedEndDate);
    }

    private String createAssignmentJson(Long employeeId, String qualification) {
        return String.format("""
                {
                    "employeeId": %d,
                    "requiredQualification": "%s"
                }
                """, employeeId, qualification);
    }

    @BeforeEach
    void setupProjectIT() {
        projectEmployeeRepository.deleteAll();
        projectRepository.deleteAll();

        // --- MOCKING SETUP ---

        when(employeeServiceClient.employeeExists(EMPLOYEE_ID_VALID)).thenReturn(true);
        when(employeeServiceClient.employeeExists(EMPLOYEE_ID_INVALID)).thenReturn(false);

        when(employeeServiceClient.getEmployeeDetails(EMPLOYEE_ID_VALID))
                .thenReturn(createMockEmployee(EMPLOYEE_ID_VALID, VALID_QUALIFICATION));

        when(employeeServiceClient.getEmployeeDetails(EMPLOYEE_ID_INVALID))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        when(employeeServiceClient.getEmployeeDetails(200L))
                .thenReturn(createMockEmployee(200L, INVALID_QUALIFICATION));

        when(employeeServiceClient.employeeExists(anyLong())).thenReturn(true);
        when(employeeServiceClient.employeeExists(EMPLOYEE_ID_INVALID)).thenReturn(false);
    }

    // Erstellt ein Projekt für nachfolgende Tests
    private Long createTestProject(Long responsibleId, LocalDate start, LocalDate end) throws Exception {
        String projectJson = createProjectJson(responsibleId, start, end);

        String response = this.mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectJson)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return new JSONObject(response).getLong("id");
    }

    // CRUD HAPPY PATH TESTS

    @Test
    void authorization_on_create() throws Exception {
        final String content = createProjectJson(EMPLOYEE_ID_VALID, LocalDate.now(), LocalDate.now().plusMonths(1));
        this.mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "user")
    void createAndFindProject_happyPath() throws Exception {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusMonths(3);

        final String projectJson = createProjectJson(EMPLOYEE_ID_VALID, start, end);

        final var response = this.mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectJson)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("designation", is("Testprojekt")))
                .andExpect(jsonPath("responsibleEmployeeId", is(EMPLOYEE_ID_VALID.intValue())))
                .andReturn().getResponse().getContentAsString();

        final var projectId = new JSONObject(response).getLong("id");

        this.mockMvc.perform(get(ENDPOINT + "/" + projectId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("designation", is("Testprojekt")));
    }

    @Test
    @WithMockUser(roles = "user")
    void deleteProject_happyPath() throws Exception {
        Long projectId = createTestProject(EMPLOYEE_ID_VALID, LocalDate.now(), LocalDate.now().plusMonths(3));

        this.mockMvc.perform(delete(ENDPOINT + "/" + projectId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertThat(projectRepository.findById(projectId)).isNotPresent();
    }

    // VALIDATION TESTS (404, 400, 409)

    @Test
    @WithMockUser(roles = "user")
    void createProject_throws_404_if_responsibleEmployee_notExists() throws Exception {
        final String invalidJson = createProjectJson(EMPLOYEE_ID_INVALID, LocalDate.now(), LocalDate.now().plusMonths(1));

        this.mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Responsible Employee with ID " + EMPLOYEE_ID_INVALID + " does not exist.")));
    }

    @Test
    @WithMockUser(roles = "user")
    void addEmployee_throws_400_if_qualificationMissing() throws Exception {
        Long projectId = createTestProject(EMPLOYEE_ID_VALID, LocalDate.now(), LocalDate.now().plusMonths(3));
        final Long maIdWithoutQualification = 200L;

        final String assignmentJson = createAssignmentJson(maIdWithoutQualification, VALID_QUALIFICATION);

        this.mockMvc.perform(post(ENDPOINT + "/" + projectId + "/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assignmentJson)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Mitarbeiter hat die Qualifikation " + VALID_QUALIFICATION + " nicht.")));
    }

    @Test
    @WithMockUser(roles = "user")
    void addEmployee_throws_409_on_timeConflict() throws Exception {
        LocalDate start1 = LocalDate.of(2026, 1, 1);
        LocalDate end1 = LocalDate.of(2026, 3, 31);

        Long projectId1 = createTestProject(EMPLOYEE_ID_VALID, start1, end1);
        String assignJson1 = createAssignmentJson(EMPLOYEE_ID_VALID, VALID_QUALIFICATION);
        this.mockMvc.perform(post(ENDPOINT + "/" + projectId1 + "/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(assignJson1)
                .with(csrf()));

        LocalDate start2_overlap = LocalDate.of(2026, 3, 15);
        LocalDate end2_overlap = LocalDate.of(2026, 5, 15);
        Long projectId2 = createTestProject(EMPLOYEE_ID_VALID, start2_overlap, end2_overlap);

        String assignJson2 = createAssignmentJson(EMPLOYEE_ID_VALID, VALID_QUALIFICATION);
        this.mockMvc.perform(post(ENDPOINT + "/" + projectId2 + "/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assignJson2)
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("Mitarbeiter ist im Zeitraum 2026-01-01 bis 2026-03-31 schon verplant.")));
    }

    // UPDATE TESTS

    @Test
    @WithMockUser(roles = "user")
    void updateProject_happyPath_and_validation() throws Exception {
        LocalDate start = LocalDate.of(2028, 1, 1);
        LocalDate end = LocalDate.of(2028, 3, 31);

        Long projectId = createTestProject(EMPLOYEE_ID_VALID, start, end);

        final String updateJson = String.format("""
                {
                    "designation": "Projektbezeichnung NEU",
                    "responsibleEmployeeId": %d,
                    "customerId": %d,
                    "customerContactPerson": "Kontakt Neu",
                    "comment": "Aktualisierter Kommentar",
                    "startDate": "2028-01-01",
                    "plannedEndDate": "2028-04-30" 
                }
                """, EMPLOYEE_ID_VALID, CUSTOMER_ID); 

        this.mockMvc.perform(put(ENDPOINT + "/" + projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(projectId.intValue())))
                .andExpect(jsonPath("designation", is("Projektbezeichnung NEU")))
                .andExpect(jsonPath("plannedEndDate", is("2028-04-30")));

        final String validationJson = String.format("""
                {
                    "designation": "Projektbezeichnung FEHLER",
                    "responsibleEmployeeId": %d, 
                    "customerId": %d,
                    "customerContactPerson": "Kontakt Neu",
                    "comment": "Aktualisierter Kommentar",
                    "startDate": "2028-01-01",
                    "plannedEndDate": "2028-04-30"
                }
                """, EMPLOYEE_ID_INVALID, CUSTOMER_ID); 

        this.mockMvc.perform(put(ENDPOINT + "/" + projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validationJson)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Responsible Employee with ID " + EMPLOYEE_ID_INVALID + " does not exist.")));
    }



    // EMPLOYEE ASSIGNMENT & REMOVAL TESTS

    @Test
    @WithMockUser(roles = "user")
    void addEmployeeAndRemove_happyPath() throws Exception {
        Long projectId = createTestProject(EMPLOYEE_ID_VALID, LocalDate.now(), LocalDate.now().plusMonths(3));
        String assignJson = createAssignmentJson(EMPLOYEE_ID_VALID, VALID_QUALIFICATION);

        this.mockMvc.perform(post(ENDPOINT + "/" + projectId + "/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assignJson)
                        .with(csrf()))
                .andExpect(status().isOk());

        this.mockMvc.perform(delete(ENDPOINT + "/" + projectId + "/employees/" + EMPLOYEE_ID_VALID)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is("SUCCESS")));

        assertThat(projectEmployeeRepository.findAllByEmployeeId(EMPLOYEE_ID_VALID)).isEmpty();
    }

    @Test
    @WithMockUser(roles = "user")
    void removeEmployee_throws_404_if_notAssigned() throws Exception {
        Long projectId = createTestProject(EMPLOYEE_ID_VALID, LocalDate.now(), LocalDate.now().plusMonths(3));

        final Long UNASSIGNED_MA = 500L;

        this.mockMvc.perform(delete(ENDPOINT + "/" + projectId + "/employees/" + UNASSIGNED_MA)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("arbeitet im Projekt mit der ID " + projectId + " nicht.")));
    }

    // READ EMPLOYEES AND PROJECTS

    @Test
    @WithMockUser(roles = "user")
    void getEmployeesInProject_happyPath() throws Exception {
        Long projectId = createTestProject(EMPLOYEE_ID_VALID, LocalDate.now(), LocalDate.now().plusMonths(3));
        String assignJson = createAssignmentJson(EMPLOYEE_ID_VALID, VALID_QUALIFICATION);
        this.mockMvc.perform(post(ENDPOINT + "/" + projectId + "/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(assignJson)
                .with(csrf()));

        this.mockMvc.perform(get(ENDPOINT + "/" + projectId + "/employees")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("projectId", is(projectId.intValue())))
                .andExpect(jsonPath("employees", hasSize(1)))
                .andExpect(jsonPath("employees[0].employeeNumber", is(EMPLOYEE_ID_VALID.intValue())))
                .andExpect(jsonPath("employees[0].qualifications[0]", is(VALID_QUALIFICATION)));
    }

    @Test
    @WithMockUser(roles = "user")
    void getProjectsByEmployee_happyPath() throws Exception {
        LocalDate start1 = LocalDate.of(2027, 1, 1);
        LocalDate end1 = LocalDate.of(2027, 3, 31);
        LocalDate start2 = LocalDate.of(2027, 4, 1);
        LocalDate end2 = LocalDate.of(2027, 6, 30);

        Long projectId1 = createTestProject(EMPLOYEE_ID_VALID, start1, end1);
        Long projectId2 = createTestProject(EMPLOYEE_ID_VALID, start2, end2);

        String assignJson = createAssignmentJson(EMPLOYEE_ID_VALID, VALID_QUALIFICATION);
        this.mockMvc.perform(post(ENDPOINT + "/" + projectId1 + "/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(assignJson)
                .with(csrf()));
        this.mockMvc.perform(post(ENDPOINT + "/" + projectId2 + "/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(assignJson)
                .with(csrf()));

        this.mockMvc.perform(get("/employees/" + EMPLOYEE_ID_VALID + "/projects")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("employeeNumber", is(EMPLOYEE_ID_VALID.intValue())))
                .andExpect(jsonPath("projects", hasSize(2)))
                .andExpect(jsonPath("projects[0].qualification", is(VALID_QUALIFICATION)));
    }

    @Test
    @WithMockUser(roles = "user")
    void getProjectsByEmployee_throws_404_if_employeeNotExists() throws Exception {
        this.mockMvc.perform(get("/employees/" + EMPLOYEE_ID_INVALID + "/projects")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Mitarbeiter mit der Mitarbeiternummer " + EMPLOYEE_ID_INVALID + " existiert nicht.")));
    }
}
