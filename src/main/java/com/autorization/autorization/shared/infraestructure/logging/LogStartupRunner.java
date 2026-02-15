package com.autorization.autorization.shared.infraestructure.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class LogStartupRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(LogStartupRunner.class);

    private final LogControlService logControlService;

    public LogStartupRunner(LogControlService logControlService) {
        this.logControlService = logControlService;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            String logsDir = logControlService.getLogsDir();
            Path dir = Path.of(logsDir);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
                log.info("Directorio de logs creado: {}", dir.toAbsolutePath());
            } else {
                log.info("Directorio de logs existe: {}", dir.toAbsolutePath());
            }

            // Asegurar registro en BD para el archivo del día
            String todayPath = logControlService.defaultTodayLogPath();
            logControlService.ensureForToday(todayPath);
            log.info("LogControl asegurado para hoy: {}", todayPath);
        } catch (Exception ex) {
            log.warn("No se pudo inicializar LogStartupRunner: {}", ex.getMessage(), ex);
        }
    }
}

