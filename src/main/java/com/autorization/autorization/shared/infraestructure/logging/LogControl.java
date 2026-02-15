package com.autorization.autorization.shared.infraestructure.logging;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_control", uniqueConstraints = @UniqueConstraint(columnNames = "log_date"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogControl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "created_at", nullable = false, columnDefinition = "timestamp with time zone")
    private LocalDateTime createdAt;
}

