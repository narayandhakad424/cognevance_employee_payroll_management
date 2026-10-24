package com.cognevance.payroll.service;

import com.cognevance.payroll.dto.AttendanceRequest;
import com.cognevance.payroll.dto.AttendenceResponse;
import com.cognevance.payroll.exception.BadRequestException;
import com.cognevance.payroll.exception.ResourceNotFoundException;
import com.cognevance.payroll.model.*;
import com.cognevance.payroll.repository.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    public AttendanceService(AttendanceRepository a, EmployeeRepository e)
    {
        attendanceRepository = a;
        employeeRepository = e;
    }

    public Attendance record(AttendanceRequest r) {

        Employee employee = employeeRepository.findById(r.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + r.employeeId()));

        if (!employee.isActive())
            throw new BadRequestException("Cannot record attendance for an inactive employee.");

        if (attendanceRepository.existsByEmployeeIdAndWorkDate(employee.getId(), r.workDate()))
            throw new BadRequestException("Attendance already exists for this employee and date.");

        if (r.checkIn() != null && r.checkOut() != null && r.checkOut().isBefore(r.checkIn()))
            throw new BadRequestException("Check-out cannot be before check-in.");

        return attendanceRepository.save(new Attendance(employee, r.workDate(), r.status(), r.checkIn(), r.checkOut(), r.notes()));
    }

    @Transactional(readOnly=true)
    public List<AttendenceResponse> byEmployeeAndMonth(Long employeeId, int year, int month) {

        if (month < 1 || month > 12) {
            throw new IllegalArgumentException(
                    "Month must be between 1 and 12");
        }

        if (!employeeRepository.existsById(employeeId))
            throw new ResourceNotFoundException("Employee not found: " + employeeId);

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return attendanceRepository
                .findByEmployeeIdAndWorkDateBetweenOrderByWorkDate(
                        employeeId , start , end
                )
                .stream()
                .map(attendance -> new AttendenceResponse(
                        attendance.getId(),
                        attendance.getEmployee().getId(),
                        attendance.getEmployee().getEmployeeCode(),
                        attendance.getWorkDate(),
                        attendance.getStatus(),
                        attendance.getCheckIn(),
                        attendance.getCheckOut()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AttendenceResponse> allBetween(
            LocalDate start, LocalDate end) {

        if (end.isBefore(start)) {
            throw new BadRequestException(
                    "End date must be on or after start date."
            );
        }

        return attendanceRepository
                .findByWorkDateBetweenOrderByWorkDate(start, end)
                .stream()
                .map(a -> new AttendenceResponse(
                        a.getId(),
                        a.getEmployee().getId(),
                        a.getEmployee().getEmployeeCode(),
                        a.getWorkDate(),
                        a.getStatus(),
                        a.getCheckIn(),
                        a.getCheckOut()
                ))
                .toList();
    }
}
