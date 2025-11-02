package de.szut.lf8_starter.project;

import de.szut.lf8_starter.customer.CustomerServiceClient;
import de.szut.lf8_starter.employee.EmployeeServiceClient;
import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final EmployeeServiceClient employeeServiceClient;
    private final CustomerServiceClient customerServiceClient;

    public ProjectService(ProjectRepository projectRepository, EmployeeServiceClient employeeServiceClient, CustomerServiceClient customerServiceClient) {
        this.projectRepository = projectRepository;
        this.employeeServiceClient = employeeServiceClient;
        this.customerServiceClient = customerServiceClient;

    }

    /**
     * Erstellt ein neues Projekt. Führt Validierungen gegen externe Services durch.
     */
    public ProjectEntity create(ProjectEntity projectEntity) {
        if (!employeeServiceClient.employeeExists(projectEntity.getResponsibleEmployeeId())) {
            throw new ResourceNotFoundException("Responsible Employee with ID " + projectEntity.getResponsibleEmployeeId() + " does not exist.");
        }

        if (!customerServiceClient.customerExists(projectEntity.getCustomerId())) {
            throw new ResourceNotFoundException("Customer with ID " + projectEntity.getCustomerId() + " does not exist.");
        }

        return projectRepository.save(projectEntity);
    }


    public ProjectEntity getById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID " + id + " does not exist."));
    }

    public void delete(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project with ID " + id + " does not exist.");
        }
        projectRepository.deleteById(id);
    }

    public List<ProjectEntity> getAll() {
        return this.projectRepository.findAll();
    }
}