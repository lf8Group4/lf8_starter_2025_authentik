package de.szut.lf8_starter.employee;

import de.szut.lf8_starter.project.dto.EmployeeDto;
import lombok.Data;
import org.springframework.http.*;
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
    private static final String TOKEN ="eyJhbGciOiJSUzI1NiIsImtpZCI6IjgyYzg5YjIxNTA4YjMwODQ1NDQyNGRiOGYyMDU1ODI3IiwidHlwIjoiSldUIn0.eyJpc3MiOiJodHRwczovL2F1dGhlbnRpay5zenV0LmRldi9hcHBsaWNhdGlvbi9vL2hpdGVjLyIsInN1YiI6IjhhYjRkZTBiNmJhZGQ2ZGVhNTM1M2Y3YzExNWVmN2FiZGMyMmEzYmQyZTNmZjRjMmU5NDYzN2JlY2I2OGUzMGEiLCJhdWQiOiJoaXRlY19hcGlfY2xpZW50IiwiZXhwIjoxNzYyMzQyMTY4LCJpYXQiOjE3NjIzMzQ5NjgsImF1dGhfdGltZSI6MTc2MjMzNDk2OCwiYWNyIjoiZ29hdXRoZW50aWsuaW8vcHJvdmlkZXJzL29hdXRoMi9kZWZhdWx0IiwiYXpwIjoiaGl0ZWNfYXBpX2NsaWVudCIsInVpZCI6ImVDSDBYeE1QT2tWY3lUQTZrVFlmblE4dEdnMEt3VmVOaEVIdk5HMjkifQ.GwnwZPoodNTo90bxBTrqFPNdb0YjFMi6pIk5HdeuBGy3hILUYZP539Mmehe-zfHhGZ75ETd5WBDHap5y23W1fxpFXy9AZ2xRrgA1lVCDveN7SX3Uag5bxoGnnyg8nE8wX9gid-pETzMgnaQPR7jQBQpS5mM5d6pcnXxJjHyw9NhaYjztyPPVCR4UeafPi-UyAUo3FSw3j3fmtWsR6aoULX68XXan3cdA7_SkPRoCyUB3xZCMhAk78BQUWvCWG3jmIf0XATILbvdrszV2W3Lj21pZq6AMKqR4y3EpVbk3aOxay42jTWFOOmXvrWQnuWy02nzBb6Mo6sm6czO_wXFFhFTWuw2mlZ-zuirHUtLG_Y9ctvJ4vZAGscBSkkCmOyxdis08NXHg5CJBijImPe0i5dbucj5JwOyz8BlFMdPIokwIt9aQA5cj1Gm781lKor1pZiHK8IqG8EtfkCnwSVZGfzjoOQvtCJ7Y-hebvne4keZSQ9wl-9tQ89He7Iu58Sim7V7J9vV3O7pzWT3FcVyW6VBExWhpC88lZiqV3YJxCIP2o7kN6akSv3IqH9O9ixC6hEpzASPNznfngLETcFi6tkubxfJoZlj1Wh5KTqVN0mADoZhRpIamurL2Vlpc6W_e7rBqjuynvglt4LHlYLqd6BBgpzCL8mzIBL-jtPZj1lY";
         //   "eyJpc3MiOiJodHRwczovL2F1dGhlbnRpay5zenV0LmRldi9hcHBsaWNhdGlvbi9vL2hpdGVjLyIsInN1YiI6IjhhYjRkZTBiNmJhZGQ2ZGVhNTM1M2Y3YzExNWVmN2FiZGMyMmEzYmQyZTNmZjRjMmU5NDYzN2JlY2I2OGUzMGEiLCJhdWQiOiJoaXRlY19hcGlfY2xpZW50IiwiZXhwIjoxNzYyMzQyMDU2LCJpYXQiOjE3NjIzMzQ4NTYsImF1dGhfdGltZSI6MTc2MjMzNDg1NiwiYWNyIjoiZ29hdXRoZW50aWsuaW8vcHJvdmlkZXJzL29hdXRoMi9kZWZhdWx0IiwiYXpwIjoiaGl0ZWNfYXBpX2NsaWVudCIsInVpZCI6ImkyS28zTGZVZmhzUEZjSGh2cVcxNWJxMW95RDRYQjd1WUhWSDk1aVIifQ";

    public EmployeeServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Hilfsmethode für Authorization Header
    private HttpEntity<Void> getAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + TOKEN);
        return new HttpEntity<>(headers);
    }

    /**
     * Prüft, ob ein Mitarbeiter mit der gegebenen ID im externen Employee Service existiert.
     */
    public boolean employeeExists(Long employeeId) {
        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                    EMPLOYEE_SERVICE_BASE_URL + employeeId,
                    HttpMethod.GET,
                    getAuthEntity(),
                    Object.class
            );
            return response.getStatusCode() == HttpStatus.OK;
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            throw ex;
        }
    }

    /**
     * Ruft die detaillierten Mitarbeiterdaten vom externen Service ab.
     */
    public EmployeeDto getEmployeeDetails(Long employeeId) {
        try {
            ResponseEntity<EmployeeApiDetailDto> response = restTemplate.exchange(
                    EMPLOYEE_SERVICE_BASE_URL + employeeId,
                    HttpMethod.GET,
                    getAuthEntity(),
                    EmployeeApiDetailDto.class
            );

            EmployeeApiDetailDto apiDetails = response.getBody();
            if (apiDetails == null) return null;

            List<String> qualificationStrings = apiDetails.getSkillSet() != null
                    ? apiDetails.getSkillSet().stream()
                    .map(QualificationDto::getSkill)
                    .collect(Collectors.toList())
                    : Collections.emptyList();

            return new EmployeeDto(
                    apiDetails.getId(),
                    apiDetails.getFirstName() + " " + apiDetails.getLastName(),
                    qualificationStrings
            );
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            }
            throw ex;
        }
    }

    // --- Interne DTOs zur Abbildung der externen API-Struktur ---

    @Data
    private static class EmployeeApiDetailDto {
        private Long id;
        private String firstName;
        private String lastName;
        private List<QualificationDto> skillSet;
    }

    @Data
    private static class QualificationDto {
        private String skill;
    }
}
