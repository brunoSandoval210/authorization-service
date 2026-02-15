package com.autorization.autorization.auth.application.services;

import com.autorization.autorization.auth.adapter.in.web.request.LoginRequest;
import com.autorization.autorization.auth.application.dto.out.AuthResponse;
import com.autorization.autorization.auth.domain.model.user.vo.UserEmail;
import com.autorization.autorization.auth.domain.port.out.UserRepositoryPort;
import com.autorization.autorization.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request) {
        String email = request.email() == null ? "" : request.email().trim().toLowerCase();
        log.debug("Intento de inicio de sesión para email={}", email);
        var userOpt = userRepositoryPort.findByEmail(new UserEmail(email));
        if (userOpt.isEmpty()) {
            log.warn("Inicio de sesión fallido: usuario no encontrado para email={}", email);
            throw new BadCredentialsException("Credenciales erróneas");
        }

        var user = userOpt.get();
        var stored = user.getPassword() == null ? null : user.getPassword().value();
        if (stored == null) {
            log.warn("Inicio de sesión fallido: el usuario {} no tiene contraseña almacenada", email);
            throw new BadCredentialsException("Credenciales erróneas");
        }

        boolean matches = passwordEncoder.matches(request.password(), stored);
        log.debug("¿Coincide la contraseña para {}? {}", email, matches);
        if (!matches) {
            // posible caso de migración: la contraseña almacenada está en texto plano
            boolean storedLooksLikeBcrypt = stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$");
            if (!storedLooksLikeBcrypt && stored.equals(request.password())) {
                // re-hash y persistir
                String rehashed = passwordEncoder.encode(stored);
                log.info("Migrando contraseña en texto plano a bcrypt para user={}", email);
                user.changePassword(new com.autorization.autorization.auth.domain.model.user.vo.UserPassword(rehashed));
                userRepositoryPort.save(user);
                matches = true;
            }
        }

        if (!matches) {
            log.warn("Inicio de sesión fallido: contraseña incorrecta para email={}", email);
            throw new BadCredentialsException("Credenciales erróneas");
        }

        // comprobar estado de la cuenta
        if (user.getStatus() != null && !user.getStatus().isEnabled()) {
            log.warn("Inicio de sesión fallido: cuenta deshabilitada para email={}", email);
            throw new DisabledException("Cuenta deshabilitada");
        }

        String token = jwtUtil.generateToken(user);
        log.info("Inicio de sesión exitoso para email={}", email);
        return new AuthResponse(token);
    }
}
