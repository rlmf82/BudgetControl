package rlmf.java.budgetcontrol.adapter.in.web.dto;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String message,
        Map<String, String> fieldErrors
) {
    public ErrorResponse(int status, String message) {
        this(Instant.now(), status, message, Map.of());
    }

    public ErrorResponse(int status, String message, Map<String, String> fieldErrors) {
        this(Instant.now(), status, message, fieldErrors);
    }
}
