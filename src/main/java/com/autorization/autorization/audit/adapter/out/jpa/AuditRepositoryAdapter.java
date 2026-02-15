package com.autorization.autorization.audit.adapter.out.jpa;

import com.autorization.autorization.audit.adapter.out.jpa.entity.ActivityLog;
import com.autorization.autorization.audit.adapter.out.jpa.mapper.AuditMapper;
import com.autorization.autorization.audit.adapter.out.jpa.repository.AuditLogJPARepository;
import com.autorization.autorization.audit.domain.model.ActivityLogDomain;
import com.autorization.autorization.audit.domain.port.out.AuditRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuditRepositoryAdapter implements AuditRepositoryPort {

    private final AuditLogJPARepository repository;
    private final AuditMapper mapper;

    @Override
    public void save(ActivityLogDomain activityLog) {
        ActivityLog entity = mapper.toEntity(activityLog);
        repository.save(entity);
    }

    @Override
    public Page<ActivityLogDomain> findLogs(String module, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        Page<ActivityLog> entityPage;
        if (module != null && !module.isEmpty()) {
            entityPage = repository.findAllByModuleAndTimestampBetween(module, start, end, pageable);
        } else {
            entityPage = repository.findAllByTimestampBetween(start, end, pageable);
        }
        return entityPage.map(mapper::toDomain);
    }
}
