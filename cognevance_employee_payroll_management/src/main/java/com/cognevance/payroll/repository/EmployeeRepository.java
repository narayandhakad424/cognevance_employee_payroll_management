package com.cognevance.payroll.repository;

import com.cognevance.payroll.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmployeeCodeIgnoreCase(String code);

    Optional<Employee> findByEmailIgnoreCase(String email);

    List<Employee> findByDepartmentIgnoreCase(String department);

    List<Employee> findByActive(boolean active);

}
