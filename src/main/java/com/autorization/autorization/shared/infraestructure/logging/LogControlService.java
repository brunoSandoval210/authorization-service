package com.autorization.autorization.shared.infraestructure.logging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class LogControlService {

    private final LogControlRepository repository;

    @Value("${app.logs.dir:logs}")
    private String logsDir;

    public LogControlService(LogControlRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public LogControl ensureForToday(String filePath) {
        LocalDate today = LocalDate.now();
        Optional<LogControl> existing = repository.findByLogDate(today);
        if (existing.isEmpty()) {
            LogControl lc = LogControl.builder()
                    .logDate(today)
                    .filePath(filePath)
                    .createdAt(LocalDateTime.now())
                    .build();
            return repository.save(lc);
        } else {
            return existing.get();
        }
    }

    // Método helper que devuelve la ruta por defecto del archivo de log del día
    public String defaultTodayLogPath() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return String.format("%s/application-%s.log", logsDir, date);
    }

    public String getLogsDir() {
        return logsDir;
    }
}

