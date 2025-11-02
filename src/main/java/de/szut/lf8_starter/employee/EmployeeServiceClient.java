package de.szut.lf8_starter.employee;

import de.szut.lf8_starter.project.dto.EmployeeDto;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmployeeServiceClient {

    private final RestTemplate restTemplate;
    private static final String EMPLOYEE_SERVICE_BASE_URL = "https://employee-api.szut.dev/employees/";

    public EmployeeServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Prüft, ob ein Mitarbeiter mit der gegebenen ID im externen Employee Service existiert.
     * (Wird für die POST/PUT Validierung verwendet)
     */
    public boolean employeeExists(Long employeeId) {
        try {
            restTemplate.getForObject(EMPLOYEE_SERVICE_BASE_URL + employeeId, Object.class);
            return true;
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            throw ex;
        }
    }

    /**
     * NEU: Ruft die detaillierten Mitarbeiterdaten vom externen Service ab.
     * (Wird für den GET /projects/{id}/employees Endpunkt verwendet)
     */
    public EmployeeDto getEmployeeDetails(Long employeeId) throws HttpClientErrorException {
        // Wir nehmen an, der externe Service gibt ein Objekt zurück, das wir in EmployeeApiDetailDto mappen können.
        EmployeeApiDetailDto apiDetails = restTemplate.getForObject(
                EMPLOYEE_SERVICE_BASE_URL + employeeId,
                EmployeeApiDetailDto.class
        );

        // Mapping der externen API-Struktur auf unser internes EmployeeDto
        if (apiDetails == null) {
            return null;
        }

        List<String> qualificationStrings = apiDetails.getQualifications() != null
                ? apiDetails.getQualifications().stream()
                .map(QualificationDto::getSkill)
                .collect(Collectors.toList())
                : Collections.emptyList();

        return new EmployeeDto(
                apiDetails.getId(),
                apiDetails.getFirstName() + " " + apiDetails.getLastName(),
                qualificationStrings
        );
    }

    // --- Interne DTOs zur Abbildung der externen API-Struktur (vereinfacht) ---

    @Data
    private static class EmployeeApiDetailDto {
        private Long id;
        private String firstName;
        private String lastName;
        private List<QualificationDto> qualifications;
    }

    @Data
    private static class QualificationDto {
        private String skill;
    }
}
