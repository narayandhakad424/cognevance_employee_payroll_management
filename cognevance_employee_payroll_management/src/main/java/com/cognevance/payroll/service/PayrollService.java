package com.cognevance.payroll.service;

import com.cognevance.payroll.dto.PayrollRequest;
import com.cognevance.payroll.exception.*;
import com.cognevance.payroll.model.*;
import com.cognevance.payroll.repository.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;

    public PayrollService(PayrollRepository p, EmployeeRepository e,
                          AttendanceRepository a) {
        payrollRepository = p;
        employeeRepository = e;
        attendanceRepository = a;
    }

    @Transactional
    public Payroll generateForEmployee(Long employeeId, PayrollRequest request) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found: " + employeeId));

        if (!employee.isActive())
            throw new BadRequestException("Payroll cannot be generated for an inactive employee.");

        YearMonth period = YearMonth.parse(request.period());

        if (payrollRepository.existsByEmployeeIdAndPayPeriod(employeeId, period.toString()))
            throw new BadRequestException("Payroll already generated for this employee and period.");

        LocalDate start = period.atDay(1), end = period.atEndOfMonth();

        List<Attendance> records = attendanceRepository
                .findByEmployeeIdAndWorkDateBetweenOrderByWorkDate(employeeId, start, end);

        long present = records.stream()
                .filter(a -> a.getStatus().equals("PRESENT")).count();

        long halfDays = records.stream()
                .filter(a -> a.getStatus().equals("HALF_DAY")).count();

        long leave = records.stream()
                .filter(a -> a.getStatus().equals("LEAVE")).count();

        long absent = records.stream()
                .filter(a -> a.getStatus().equals("ABSENT")).count();

        int paidDays = (int)(present + halfDays * 0.5 + leave);
        int absentDays = (int) absent;
        BigDecimal base = employee.getBaseSalary();

        // Simple documented policy: salary is monthly base + allowance - deduction.
        // Attendance is included in the report; it does not prorate salary in this demo policy.
        BigDecimal allowances = request.allowance()
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal deductions = request.deduction()
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal gross = base.add(allowances)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal net = gross.subtract(deductions).max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        return payrollRepository.save(new Payroll(employee, period, base, allowances
                , deductions, gross, net, paidDays, absentDays));
    }

    @Async
    public CompletableFuture<List<Payroll>> generateForAllActive(PayrollRequest request) {

        List<Employee> employees = employeeRepository.findByActive(true);

        List<Payroll> results = new ArrayList<>();

        for (Employee employee : employees) {
            try {
                results.add(generateForEmployee(employee.getId(), request));
            }
            catch (BadRequestException ex) {
                if (!ex.getMessage().startsWith("Payroll already generated")) throw ex;
            }
        }
        return CompletableFuture.completedFuture(results);
    }

    @Transactional(readOnly=true)
    public List<Payroll> byPeriod(String period) {

        YearMonth.parse(period);
        return payrollRepository.findByPayPeriodOrderByEmployeeFullName(period);
    }

    @Transactional(readOnly=true)
    public List<Payroll> byEmployee(Long employeeId) {

        if (!employeeRepository.existsById(employeeId))
            throw new ResourceNotFoundException("Employee not found: " + employeeId);

        return payrollRepository.findByEmployeeIdOrderByPayPeriodDesc(employeeId);
    }

    @Transactional
    public Payroll markPaid(Long payrollId) {

        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payroll not found: " + payrollId));

        if (payroll.getStatus().equals("PAID"))
            throw new BadRequestException("Payroll is already marked paid.");

        payroll.markPaid();
        return payrollRepository.save(payroll);
    }

    @Transactional(readOnly=true)
    public Map<String, Object> summary(String period) {

        List<Payroll> rows = byPeriod(period);

        BigDecimal gross = rows.stream()
                .map(Payroll::getGrossSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal net = rows.stream()
                .map(Payroll::getNetSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long paid = rows.stream()
                .filter(p -> p.getStatus().equals("PAID")).count();

        return Map.of("period", period, "payrollCount", rows.size(),
                "paidCount", paid, "pendingCount", rows.size() - paid,
                "totalGross", gross, "totalNet", net);
    }
}
