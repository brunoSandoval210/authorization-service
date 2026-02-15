package com.autorization.autorization.audit.domain.port.in;

import com.autorization.autorization.audit.domain.model.ActivityLogDomain;
import com.autorization.autorization.shared.application.dto.PaginatedResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AuditUseCasePort {
    void logActivity(ActivityLogDomain activityLog);

    PaginatedResponse<ActivityLogDomain> retrieveLogs(String module, LocalDate date, Pageable pageable);
}
