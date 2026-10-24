package com.cognevance.payroll.dto;

import com.cognevance.payroll.enums.AttendanceStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceRequest(

        @NotNull
        Long employeeId,

        @NotNull
        @PastOrPresent
        LocalDate workDate,

        @NotBlank
        @Pattern(regexp="PRESENT|ABSENT|LEAVE|HALF_DAY")
        AttendanceStatus status,
        LocalDateTime checkIn,
        LocalDateTime checkOut,
        @Size(max=500) String notes
) {}
