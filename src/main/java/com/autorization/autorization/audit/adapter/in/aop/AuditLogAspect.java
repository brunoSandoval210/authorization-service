package com.autorization.autorization.audit.adapter.in.aop;

import com.autorization.autorization.audit.domain.model.ActivityLogDomain;
import com.autorization.autorization.audit.domain.port.in.AuditUseCasePort;
import com.autorization.autorization.shared.annotation.AuditLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogAspect {

    private final AuditUseCasePort auditUseCasePort;
    ObjectMapper objectMapper = new ObjectMapper();

    @AfterReturning(pointcut = "@annotation(auditLog)", returning = "result")
    public void logActivitySuccess(JoinPoint joinPoint, AuditLog auditLog, Object result) {
        saveLog(joinPoint, auditLog, "SUCCESS", null);
    }

    @AfterThrowing(pointcut = "@annotation(auditLog)", throwing = "exception")
    public void logActivityFailure(JoinPoint joinPoint, AuditLog auditLog, Exception exception) {
        saveLog(joinPoint, auditLog, "FAILURE", exception.getMessage());
    }

    @Async
    public void saveLog(JoinPoint joinPoint, AuditLog auditLog, String status, String errorDetails) {
        try {
            String userId = getCurrentUser();
            String details = getMethodArgs(joinPoint);
            String ipAddress = getClientIp();

            if (errorDetails != null) {
                details += " | Error: " + errorDetails;
            }

            ActivityLogDomain log = ActivityLogDomain.builder()
                    .userId(userId)
                    .module(auditLog.module())
                    .action(auditLog.action())
                    .details(details)
                    .ipAddress(ipAddress)
                    .status(status)
                    .timestamp(LocalDateTime.now())
                    .build();

            auditUseCasePort.logActivity(log);

        } catch (Exception e) {
            log.error("Error saving audit log", e);
        }
    }

    private String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "ANONYMOUS";
    }

    private String getMethodArgs(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                // Filter out non-serializable objects if necessary, for now just try to
                // serialize the first arg which is usually the request body
                return objectMapper.writeValueAsString(args[0]);
            }
        } catch (Exception e) {
            log.warn("Could not serialize method arguments", e);
        }
        return "{}";
    }

    private String getClientIp() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("Proxy-Client-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("WL-Proxy-Client-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                return ip;
            }
        } catch (Exception e) {
            log.warn("Could not get client IP", e);
        }
        return "UNKNOWN";
    }
}
