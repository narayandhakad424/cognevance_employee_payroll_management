package com.cognevance.payroll.controller;

import com.cognevance.payroll.dto.*;
import com.cognevance.payroll.model.Employee;
import com.cognevance.payroll.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<Employee> create(@Valid @RequestBody EmployeeRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));
     }

    @GetMapping
    public List<Employee> all(@RequestParam(required=false) String department,
                              @RequestParam(required=false) Boolean active) {
        if (department != null && !department.isBlank()) {
            return service.byDepartment(department);
        };

        if (active != null) return service.byStatus(active);
        return service.all();
    }

    @GetMapping("/{id}")
    public Employee get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public Employee update(@PathVariable Long id,
                           @Valid @RequestBody EmployeeRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    public ApiMessage delete(@PathVariable Long id) {
        service.delete(id);
        return ApiMessage.of("Employee deactivated; historical records preserved."); }
}
