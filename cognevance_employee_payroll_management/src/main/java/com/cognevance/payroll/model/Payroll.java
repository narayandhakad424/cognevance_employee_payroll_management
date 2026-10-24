package com.cognevance.payroll.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Table(name = "payrolls", uniqueConstraints =
        @UniqueConstraint(name = "uk_payroll_employee_period", columnNames = {"employee_id", "pay_period"}))
public class Payroll {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "pay_period", nullable = false, length = 7)
    private String payPeriod;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal baseSalary;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal allowances;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal deductions;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal grossSalary;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal netSalary;

    @Column(nullable = false)
    private int paidDays;

    @Column(nullable = false)
    private int absentDays;

    @Column(nullable = false)
    private LocalDate generatedOn;

    @Column(nullable = false, length = 20)
    private String status;

    protected Payroll() {}
    public Payroll(Employee employee, YearMonth period, BigDecimal baseSalary, BigDecimal allowances,
                   BigDecimal deductions, BigDecimal grossSalary, BigDecimal netSalary, int paidDays, int absentDays) {
        this.employee = employee; this.payPeriod = period.toString(); this.baseSalary = baseSalary;
        this.allowances = allowances; this.deductions = deductions; this.grossSalary = grossSalary;
        this.netSalary = netSalary; this.paidDays = paidDays; this.absentDays = absentDays;
        this.generatedOn = LocalDate.now(); this.status = "GENERATED";
    }
    public Long getId() { return id; }
    public Employee getEmployee() { return employee; }
    public String getPayPeriod() { return payPeriod; }
    public BigDecimal getBaseSalary() { return baseSalary; }
    public BigDecimal getAllowances() { return allowances; }
    public BigDecimal getDeductions() { return deductions; }
    public BigDecimal getGrossSalary() { return grossSalary; }
    public BigDecimal getNetSalary() { return netSalary; }
    public int getPaidDays() { return paidDays; }
    public int getAbsentDays() { return absentDays; }
    public LocalDate getGeneratedOn() { return generatedOn; }
    public String getStatus() { return status; }
    public void markPaid() { status = "PAID"; }
}
