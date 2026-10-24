package com.cognevance.payroll.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employees", uniqueConstraints = {
        @UniqueConstraint(name = "uk_employee_email", columnNames = "email"),
        @UniqueConstraint(name = "uk_employee_code", columnNames = "employee_code")})
public class Employee {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_code", nullable = false, length = 30)
    private String employeeCode;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(nullable = false, length = 100)
    private String designation;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal baseSalary;

    @Column(nullable = false)
    private LocalDate joiningDate;

    @Column(nullable = false)
    private boolean active = true;

    protected Employee() {}

    public Employee(String employeeCode, String fullName, String email, String phone, String department,
                    String designation, BigDecimal baseSalary, LocalDate joiningDate, boolean active) {
        this.employeeCode = employeeCode; this.fullName = fullName; this.email = email; this.phone = phone;
        this.department = department; this.designation = designation; this.baseSalary = baseSalary;
        this.joiningDate = joiningDate; this.active = active;
    }
    public Long getId() { return id; }
    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String v) { employeeCode = v; }
    public String getFullName() { return fullName; }
    public void setFullName(String v) { fullName = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { email = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { phone = v; }
    public String getDepartment() { return department; }
    public void setDepartment(String v) { department = v; }
    public String getDesignation() { return designation; }
    public void setDesignation(String v) { designation = v; }
    public BigDecimal getBaseSalary() { return baseSalary; }
    public void setBaseSalary(BigDecimal v) { baseSalary = v; }
    public LocalDate getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDate v) { joiningDate = v; }
    public boolean isActive() { return active; }
    public void setActive(boolean v) { active = v; }
}
