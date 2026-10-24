package com.cognevance.payroll.controller;

import com.cognevance.payroll.dto.AttendanceRequest;
import com.cognevance.payroll.dto.AttendenceResponse;
import com.cognevance.payroll.model.Attendance;
import com.cognevance.payroll.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService service;
    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    @PostMapping("/record")
    public ResponseEntity<Attendance> record(@Valid @RequestBody AttendanceRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.record(r));
    }

    @GetMapping("/employee/{employeeId}")
    public List<AttendenceResponse> employeeMonth(@PathVariable Long employeeId,
                                                  @RequestParam int year, @RequestParam int month) {

        if (month < 1 || month > 12)
            throw new IllegalArgumentException("month must be from 1 to 12");
        return service.byEmployeeAndMonth(employeeId, year, month);
    }

    @GetMapping
    public List<AttendenceResponse> between(@RequestParam LocalDate start,
                                    @RequestParam LocalDate end) {
        return service.allBetween(start, end);
    }
}
