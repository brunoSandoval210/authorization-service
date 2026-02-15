package com.autorization.autorization.audit.domain.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityLogDomain {

    private UUID id;
    private String userId;
    private String module;
    private String action;
    private String details;
    private String ipAddress;
    private String status;
    private LocalDateTime timestamp;
}
