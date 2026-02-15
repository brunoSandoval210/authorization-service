package com.autorization.autorization.audit.application.services;

import com.autorization.autorization.audit.domain.model.ActivityLogDomain;
import com.autorization.autorization.audit.domain.port.in.AuditUseCasePort;
import com.autorization.autorization.audit.domain.port.out.AuditRepositoryPort;
import com.autorization.autorization.shared.application.dto.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService implements AuditUseCasePort {

    private final AuditRepositoryPort auditRepositoryPort;

    @Override
    @Async
    public void logActivity(ActivityLogDomain activityLog) {
        auditRepositoryPort.save(activityLog);
    }

    @Override
    public PaginatedResponse<ActivityLogDomain> retrieveLogs(String module, LocalDate date, Pageable pageable) {
        if (date == null) {
            date = LocalDate.now();
        }
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        Page<ActivityLogDomain> page = auditRepositoryPort.findLogs(module, start, end, pageable);

        return new PaginatedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
