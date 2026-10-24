package com.cognevance.payroll.dto;

import com.cognevance.payroll.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendenceResponse {

    private Long id;
    private Long employeeId;
    private  String employeeCode;
    private   LocalDate workDate;
    private AttendanceStatus status;
    private   LocalDateTime checkIn;
    private   LocalDateTime checkOut;

}
