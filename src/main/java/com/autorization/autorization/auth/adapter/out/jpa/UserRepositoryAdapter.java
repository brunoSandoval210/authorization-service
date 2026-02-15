package com.autorization.autorization.auth.adapter.out.jpa;

import com.autorization.autorization.auth.adapter.out.jpa.entity.User;
import com.autorization.autorization.auth.adapter.out.jpa.mapper.UserJPAMapper;
import com.autorization.autorization.auth.adapter.out.jpa.repository.RoleRepository;
import com.autorization.autorization.auth.adapter.out.jpa.repository.UserRepository;
import com.autorization.autorization.shared.domain.exception.PersistenceException;
import com.autorization.autorization.auth.domain.model.user.UserDomain;
import com.autorization.autorization.auth.domain.model.user.vo.UserEmail;
import com.autorization.autorization.auth.domain.model.user.vo.UserId;
import com.autorization.autorization.auth.domain.port.out.UserRepositoryPort;
import com.autorization.autorization.shared.domain.model.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public UserDomain save(UserDomain domain) {
        try {
            Optional<User> existingUser = userRepository.findById(domain.getUserId().id());
            User entityToSave;

            if (existingUser.isPresent()) {
                entityToSave = existingUser.get();
                entityToSave.setName(domain.getNames().name());
                entityToSave.setLastName(domain.getNames().lastName());
                entityToSave.setSecondName(domain.getNames().secondName());
                entityToSave.setEmail(domain.getEmail() == null ? null : domain.getEmail().value());

                if (domain.getPassword() != null && domain.getPassword().value() != null) {
                    entityToSave.setPassword(domain.getPassword().value());
                }

                if (domain.getStatus() != null) {
                    entityToSave.setEnabled(domain.getStatus().isEnabled());
                    entityToSave.setAccountNonExpired(domain.getStatus().accountNonExpired());
                    entityToSave.setAccountNonLocked(domain.getStatus().accountNonLocked());
                    entityToSave.setCredentialsNonExpired(domain.getStatus().credentialsNonExpired());
                    entityToSave.setStatus(domain.getStatus().status());
                }
            } else {
                entityToSave = UserJPAMapper.fromDomain(domain);
            }

            if (domain.getRoles() != null) {
                var roles = domain.getRoles().stream()
                        .map(r -> roleRepository.getReferenceById(r.getRoleId().id()))
                        .collect(Collectors.toSet());
                entityToSave.setRoles(roles);
            }

            var saved = userRepository.save(entityToSave);
            return UserJPAMapper.toDomain(saved);

        } catch (Exception e) {
            log.error("Error al persistir usuario: {}", e.getMessage(), e);
            throw new PersistenceException("Error al persistir usuario", e);
        }
    }

    @Override
    public Optional<UserDomain> findById(UserId id) {
        try {
            return userRepository.findById(id.id()).map(UserJPAMapper::toDomain);
        } catch (Exception e) {
            log.error("Error al buscar usuario por id {}: {}", id, e.getMessage(), e);
            throw new PersistenceException("Error al buscar usuario por id", e);
        }
    }

    @Override
    public Optional<UserDomain> findByEmail(UserEmail email) {
        try {
            return userRepository.findByEmail(email.value()).map(UserJPAMapper::toDomain);
        } catch (Exception e) {
            log.error("Error al buscar usuario por email {}: {}", email, e.getMessage(), e);
            throw new PersistenceException("Error al buscar usuario por email", e);
        }
    }

    @Override
    public List<UserDomain> findAll() {
        try {
            return userRepository.findAll().stream().map(UserJPAMapper::toDomain).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al obtener todos los usuarios: {}", e.getMessage(), e);
            throw new PersistenceException("Error al obtener todos los usuarios", e);
        }
    }

    @Override
    public boolean existsById(UserId id) {
        try {
            return userRepository.existsById(id.id());
        } catch (Exception e) {
            log.error("Error al verificar existencia de usuario {}: {}", id, e.getMessage(), e);
            throw new PersistenceException("Error al verificar existencia de usuario", e);
        }
    }

    @Override
    public void updateEnabled(UserId id, boolean enabled) {
        try {
            var maybe = userRepository.findById(id.id());
            maybe.ifPresent(u -> {
                u.setStatus(enabled ? Status.ACTIVO : Status.INACTIVO);
                userRepository.save(u);
            });
        } catch (Exception e) {
            log.error("Error al actualizar estado del usuario {}: {}", id, e.getMessage(), e);
            throw new PersistenceException("Error al actualizar estado del usuario", e);
        }
    }

    @Override
    public Page<UserDomain> searchByUsernameOrUserId(String email, Status status, Pageable pageable) {
        try {
            // normalizar email: trim, lower, tratar cadenas vacías como null
            String qemail = null;
            if (email != null) {
                String trimmed = email.trim();
                if (!trimmed.isBlank()) qemail = trimmed.toLowerCase();
            }

            Page<User> page;
            if (qemail == null && status == null) {
                // Sin filtro, devolver findAll por página
                page = userRepository.findAll(pageable);
            } else {
                // pasar qemail (puede ser null) y status al repo que hace la búsqueda compuesta
                page = userRepository.searchByUsernameOrUserId(qemail, status, pageable);
            }

            return page.map(UserJPAMapper::toDomain);
        } catch (Exception e) {
            log.error("Error al buscar usuarios con email='{}' normalizado='{}' status='{}': {}", email, (email==null?null:email.trim()), status, e.getMessage(), e);
            throw new PersistenceException("Error al buscar usuarios", e);
        }
    }
}
