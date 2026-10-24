package com.cognevance.payroll.controller;

import com.cognevance.payroll.dto.*;
import com.cognevance.payroll.model.Payroll;
import com.cognevance.payroll.service.PayrollService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/payroll")
public class PayrollController {

    private final PayrollService service;

    public PayrollController(PayrollService service) {
        this.service = service;
    }

    @PostMapping("/employee/{employeeId}")
    public ResponseEntity<Payroll> generate(@PathVariable Long employeeId,
            @Valid @RequestBody PayrollRequest r) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.generateForEmployee(employeeId, r));
    }

    @PostMapping("/generate-all")
    public CompletableFuture<ResponseEntity<List<Payroll>>> generateAll(@Valid @RequestBody PayrollRequest r) {
        return service.generateForAllActive(r)
                .thenApply(results -> ResponseEntity.status(HttpStatus.CREATED).body(results));
    }

    @GetMapping
    public List<Payroll> byPeriod(@RequestParam String period) {
        return service.byPeriod(period);
    }

    @GetMapping("/employee/{employeeId}")
    public List<Payroll> byEmployee(@PathVariable Long employeeId) {
        return service.byEmployee(employeeId);
    }

    @PatchMapping("/{payrollId}/paid")
    public Payroll paid(@PathVariable Long payrollId) {
        return service.markPaid(payrollId);
    }

    @GetMapping("/summary")
    public Map<String,Object> summary(@RequestParam String period) {
        return service.summary(period);
    }
}
