package com.autorization.autorization.audit.adapter.out.jpa.repository;

import com.autorization.autorization.audit.adapter.out.jpa.entity.ActivityLog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface AuditLogJPARepository extends JpaRepository<ActivityLog, UUID> {
    Page<ActivityLog> findAllByModule(String module, Pageable pageable);

    Page<ActivityLog> findAllByTimestampBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<ActivityLog> findAllByModuleAndTimestampBetween(String module, LocalDateTime start, LocalDateTime end,
            Pageable pageable);
}
