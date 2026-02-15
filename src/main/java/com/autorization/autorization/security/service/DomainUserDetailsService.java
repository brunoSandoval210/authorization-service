package com.autorization.autorization.security.service;

import com.autorization.autorization.auth.application.dto.out.UserSecurityResponse;
import com.autorization.autorization.auth.domain.port.in.GetUserForAuthUseCase;
import com.autorization.autorization.auth.domain.model.user.UserDomain;
import com.autorization.autorization.auth.domain.model.user.vo.UserEmail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DomainUserDetailsService implements UserDetailsService {
    private final GetUserForAuthUseCase getUserForAuthUseCase;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String email = username == null ? "" : username.trim().toLowerCase();

        UserSecurityResponse user = getUserForAuthUseCase.execute(email);

        Collection<GrantedAuthority> authorities = user.authorities().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        log.debug("Usuario {} cargado con {} autoridades", email, authorities.size());

        return org.springframework.security.core.userdetails.User.withUsername(user.email())
                .password(user.password())
                .authorities(authorities)
                .disabled(!user.enabled())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();
    }
}