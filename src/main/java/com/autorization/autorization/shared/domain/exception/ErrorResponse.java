package com.autorization.autorization.shared.domain.exception;

import java.time.Instant;
import org.slf4j.MDC;
import com.autorization.autorization.shared.infraestructure.web.RequestIdFilter;

public record ErrorResponse(
        Instant timestamp,
        String requestId,
        String message,
        String detail
) {
    public static ErrorResponse of(String message, String detail) {
        // usar la constante centralizada en RequestIdFilter para consistencia
        String reqId = MDC.get(RequestIdFilter.MDC_KEY);
        return new ErrorResponse(Instant.now(), reqId, message, detail);
    }
}
