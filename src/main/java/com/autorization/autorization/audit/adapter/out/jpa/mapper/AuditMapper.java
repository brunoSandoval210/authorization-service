package com.autorization.autorization.audit.adapter.out.jpa.mapper;

import com.autorization.autorization.audit.adapter.out.jpa.entity.ActivityLog;
import com.autorization.autorization.audit.domain.model.ActivityLogDomain;

import org.springframework.stereotype.Component;

@Component
public class AuditMapper {

    public ActivityLog toEntity(ActivityLogDomain domain) {
        if (domain == null)
            return null;
        return ActivityLog.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .module(domain.getModule())
                .action(domain.getAction())
                .details(domain.getDetails())
                .ipAddress(domain.getIpAddress())
                .status(domain.getStatus())
                .timestamp(domain.getTimestamp())
                .build();
    }

    public ActivityLogDomain toDomain(ActivityLog entity) {
        if (entity == null)
            return null;
        return ActivityLogDomain.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .module(entity.getModule())
                .action(entity.getAction())
                .details(entity.getDetails())
                .ipAddress(entity.getIpAddress())
                .status(entity.getStatus())
                .timestamp(entity.getTimestamp())
                .build();
    }
}
