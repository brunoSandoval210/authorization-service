package com.autorization.autorization.shared.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.EntityListeners;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public abstract class Maintenance {

    @CreatedDate
    @Column(nullable = false, columnDefinition = "timestamp with time zone")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(columnDefinition = "timestamp with time zone")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "usu_reg", nullable = false, updatable = false)
    private String usuReg;

    @LastModifiedBy
    @Column(name = "usu_mod")
    private String usuMod;

    @Enumerated(value = EnumType.STRING)
    private Status status;
}