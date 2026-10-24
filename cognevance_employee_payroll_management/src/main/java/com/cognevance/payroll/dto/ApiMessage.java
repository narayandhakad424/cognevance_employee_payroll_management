package com.cognevance.payroll.dto;

import java.time.Instant;

public record ApiMessage(String message, Instant timestamp) {

    public static ApiMessage of(String message) {
        return new ApiMessage(message, Instant.now());
    }
}
