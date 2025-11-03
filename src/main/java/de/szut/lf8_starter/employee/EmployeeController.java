package de.szut.lf8_starter.employee;

import de.szut.lf8_starter.project_employee.ProjectEmployeeService;
import de.szut.lf8_starter.project_employee.dto.EmployeeProjectsGetDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employees")
public class EmployeeController implements EmployeeControllerOpenAPI {

    private final ProjectEmployeeService projectEmployeeService;

    public EmployeeController(ProjectEmployeeService projectEmployeeService) {
        this.projectEmployeeService = projectEmployeeService;
    }

    @Override
    @GetMapping("/{employeeId}/projects")
    public ResponseEntity<EmployeeProjectsGetDto> getProjectsByEmployee(@PathVariable Long employeeId) {
        EmployeeProjectsGetDto projects = projectEmployeeService.getProjectsByEmployee(employeeId);
        return ResponseEntity.ok(projects);
    }
}