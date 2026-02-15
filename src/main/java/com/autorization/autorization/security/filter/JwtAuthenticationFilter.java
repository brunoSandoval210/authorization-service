package com.autorization.autorization.security.filter;

import com.autorization.autorization.security.service.AuthPrincipal;
import com.autorization.autorization.security.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7).trim();
        }

        if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // obtener username (subject o claim 'email') de forma robusta
                String username = jwtUtil.extractUsername(jwt);

                if (username == null || username.isBlank()) {
                    log.debug("Token válido pero no contiene subject ni claim 'email'");
                } else if (jwtUtil.validateToken(jwt)) {
                    // ahora extraer claims para roles/permisos
                    Claims claims = jwtUtil.extractAllClaims(jwt);

                    // Manejar listas de forma segura (convertir a String)
                    Object rolesObj = claims.get("roles");
                    List<String> roles = List.of();
                    if (rolesObj instanceof List) {
                        roles = ((List<?>) rolesObj).stream()
                                .filter(Objects::nonNull)
                                .map(Object::toString)
                                .collect(Collectors.toList());
                    }

                    Object permsObj = claims.get("permissions");
                    List<String> permissions = List.of();
                    if (permsObj instanceof List) {
                        permissions = ((List<?>) permsObj).stream()
                                .filter(Objects::nonNull)
                                .map(Object::toString)
                                .collect(Collectors.toList());
                    }

                    // normalizar y evitar duplicados
                    Set<String> normalized = roles.stream()
                            .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                            .collect(Collectors.toSet());
                    normalized.addAll(permissions.stream()
                            .map(p -> p.startsWith("PERM_") ? p : "PERM_" + p)
                            .collect(Collectors.toSet()));

                    Collection<GrantedAuthority> authorities = new ArrayList<>();
                    normalized.forEach(a -> authorities.add(new SimpleGrantedAuthority(a)));

                    // extraer userId claim si existe
                    String userIdClaim = null;
                    try {
                        Object uid = claims.get("userId");
                        userIdClaim = uid == null ? null : uid.toString();
                    } catch (Exception ignored) {}

                    // crear principal con username y userId
                    AuthPrincipal principal = new AuthPrincipal(username, userIdClaim);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            principal, null, authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                log.debug("Token inválido o error al procesar claims: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
