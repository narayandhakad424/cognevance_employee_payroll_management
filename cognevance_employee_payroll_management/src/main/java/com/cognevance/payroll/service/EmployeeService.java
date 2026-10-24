package com.cognevance.payroll.service;

import com.cognevance.payroll.dto.EmployeeRequest;
import com.cognevance.payroll.exception.*;
import com.cognevance.payroll.model.Employee;
import com.cognevance.payroll.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public Employee create(EmployeeRequest r) {

        if (repository.existsByEmployeeCodeIgnoreCase(r.employeeCode().trim()))
            throw new BadRequestException("Employee code already exists.");

        if (repository.existsByEmailIgnoreCase(r.email().trim()))
            throw new BadRequestException("Email already exists.");

        return repository.save(new Employee(r.employeeCode().trim(), r.fullName().trim(), r.email().trim().toLowerCase(),
                r.phone(), r.department().trim(), r.designation().trim(), r.baseSalary(), r.joiningDate(),
                r.active() == null || r.active()));
    }

    @Transactional(readOnly=true)
    public List<Employee> all() {
        return repository.findAll();
    }

    @Transactional(readOnly=true)
    public Employee get(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
    }

    @Transactional(readOnly=true)
    public List<Employee> byDepartment(String department) {
        return repository.findByDepartmentIgnoreCase(department);
    }

    @Transactional(readOnly=true)
    public List<Employee> byStatus(boolean active) {
        return repository.findByActive(active);
    }

    public Employee update(Long id, EmployeeRequest r) {

        Employee e = get(id);

        repository.findByEmailIgnoreCase(r.email()
                        .trim())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new BadRequestException("Email already belongs to another employee."); });

        if (!e.getEmployeeCode().equalsIgnoreCase(r.employeeCode().trim()) &&
                repository.existsByEmployeeCodeIgnoreCase(r.employeeCode().trim()))
            throw new BadRequestException("Employee code already exists.");

        e.setEmployeeCode(r.employeeCode().trim());
        e.setFullName(r.fullName().trim());
        e.setEmail(r.email().trim().toLowerCase());
        e.setPhone(r.phone());
        e.setDepartment(r.department().trim());
        e.setDesignation(r.designation().trim());
        e.setBaseSalary(r.baseSalary());
        e.setJoiningDate(r.joiningDate());
        e.setActive(r.active() == null || r.active());

        return repository.save(e);
    }

    public void delete(Long id) {
        Employee e = get(id);
        e.setActive(false); // Soft delete preserves attendance and payroll history.
        repository.save(e);
    }
}
