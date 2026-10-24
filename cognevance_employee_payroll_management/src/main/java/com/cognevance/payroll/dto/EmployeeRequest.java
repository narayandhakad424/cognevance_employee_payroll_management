package com.cognevance.payroll.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeRequest(

        @NotBlank
        @Size(max=30)
        String employeeCode,

        @NotBlank
        @Size(max=100)
        String fullName,

        @NotBlank
        @Email
        @Size(max=150)
        String email,

        @Size(max=30)
        String phone,

        @NotBlank
        @Size(max=100)
        String department,

        @NotBlank
        @Size(max=100)
        String designation,

        @NotNull
        @DecimalMin(value="0.01")
        BigDecimal baseSalary,

        @NotNull
        @PastOrPresent
        LocalDate joiningDate,
        Boolean active
) {}
