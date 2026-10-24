package com.cognevance.payroll;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EmployeePayrollApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmployeePayrollApplication.class, args);
    }
}
