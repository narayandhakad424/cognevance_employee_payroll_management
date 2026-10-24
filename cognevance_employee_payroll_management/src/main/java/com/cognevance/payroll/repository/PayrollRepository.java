package com.cognevance.payroll.repository;

import com.cognevance.payroll.model.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    boolean existsByEmployeeIdAndPayPeriod(Long employeeId, String payPeriod);

    List<Payroll> findByPayPeriodOrderByEmployeeFullName(String payPeriod);

    List<Payroll> findByEmployeeIdOrderByPayPeriodDesc(Long employeeId);

    Optional<Payroll> findByIdAndStatus(Long id, String status);
}
