package com.autorization.autorization.audit.domain.port.out;

import com.autorization.autorization.audit.domain.model.ActivityLogDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface AuditRepositoryPort {
    void save(ActivityLogDomain activityLog);

    Page<ActivityLogDomain> findLogs(String module, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
