package com.cognevance.payroll.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record PayrollRequest(

        @NotBlank @Pattern(regexp="\\d{4}-(0[1-9]|1[0-2])",
                message="period must be YYYY-MM")
        String period,

        @NotNull @DecimalMin("0.00")
        BigDecimal allowance,

        @NotNull @DecimalMin("0.00")
        BigDecimal deduction
) {}
