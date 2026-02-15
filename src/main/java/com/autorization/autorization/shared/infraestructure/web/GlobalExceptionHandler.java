package com.autorization.autorization.shared.infraestructure.web;

import com.autorization.autorization.auth.adapter.out.jpa.mapper.exception.MappingException;
import com.autorization.autorization.shared.domain.exception.ErrorResponse;
import com.autorization.autorization.shared.domain.exception.PersistenceException;
import com.autorization.autorization.auth.domain.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.autorization.autorization.shared.infraestructure.logging.LogControlService;

import org.springframework.web.bind.MethodArgumentNotValidException;
import jakarta.validation.ConstraintViolationException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final LogControlService logControlService;

    private void ensureLogControl() {
        try {
            String path = logControlService.defaultTodayLogPath();
            logControlService.ensureForToday(path);
        } catch (Exception ex) {
            log.warn("No se pudo registrar LogControl: {}", ex.getMessage(), ex);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgNotValid(MethodArgumentNotValidException ex) {
        ensureLogControl();
        String details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("Validación de parámetros fallida: {}", details);
        ErrorResponse dto = ErrorResponse.of("Validation failed", details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        ensureLogControl();
        String details = ex.getConstraintViolations()
                .stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining("; "));
        log.warn("Violaciones de constraints: {}", details);
        ErrorResponse dto = ErrorResponse.of("Validation failed", details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ErrorResponse> handlePersistence(PersistenceException ex) {
        ensureLogControl();

        log.error("Excepción de persistencia: {}", ex.getMessage(), ex);
        ErrorResponse dto = ErrorResponse.of(ex.getMessage(), ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(dto);
    }

    @ExceptionHandler(MappingException.class)
    public ResponseEntity<ErrorResponse> handleMapping(MappingException ex) {
        ensureLogControl();

        log.warn("Error de mapeo: {}", ex.getMessage(), ex);
        ErrorResponse dto = ErrorResponse.of(ex.getMessage(), ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }

    @ExceptionHandler({UserNotFoundException.class, RoleNotFoundException.class, PermissionNotFoundException.class, ModuleNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex) {
        log.info("No encontrado: {}", ex.getMessage());
        ErrorResponse dto = ErrorResponse.of(ex.getMessage(), ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(dto);
    }

    @ExceptionHandler({UserAlreadyExistsException.class, RoleAlreadyExistsException.class, PermissionAlreadyExistsException.class})
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException ex) {
        log.info("Conflicto: {}", ex.getMessage());
        ErrorResponse dto = ErrorResponse.of(ex.getMessage(), ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(dto);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex) {
        log.warn("Autenticación fallida: {}", ex.getMessage());
        ErrorResponse dto = ErrorResponse.of(ex.getMessage(), ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(dto);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Acceso denegado: {}", ex.getMessage());
        ErrorResponse dto = ErrorResponse.of(ex.getMessage(), ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(dto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ensureLogControl();

        log.error("Excepción no controlada: {}", ex.getMessage(), ex);
        ErrorResponse dto = ErrorResponse.of(ex.getMessage(), ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(dto);
    }
}
