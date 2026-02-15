package com.autorization.autorization.shared.infraestructure.logging;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface LogControlRepository extends JpaRepository<LogControl, Long> {
    Optional<LogControl> findByLogDate(LocalDate logDate);
}

