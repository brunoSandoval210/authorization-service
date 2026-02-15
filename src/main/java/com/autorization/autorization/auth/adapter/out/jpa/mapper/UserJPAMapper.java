package com.autorization.autorization.auth.adapter.out.jpa.mapper;

import com.autorization.autorization.auth.adapter.out.jpa.entity.Role;
import com.autorization.autorization.auth.adapter.out.jpa.entity.User;
import com.autorization.autorization.auth.adapter.out.jpa.mapper.exception.MappingException;
import com.autorization.autorization.auth.domain.model.role.RoleDomain;
import com.autorization.autorization.auth.domain.model.user.UserDomain;
import com.autorization.autorization.auth.domain.model.user.vo.*;
import com.autorization.autorization.shared.domain.model.Status;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UserJPAMapper {
    public static UserDomain toDomain(User entity) {
        String idStr = entity == null || entity.getUserId() == null ? "-" : entity.getUserId().toString();
        try {
            if (entity == null) return null;

            Status statusValue = entity.getStatus() == null ? Status.ACTIVO : entity.getStatus();

            var id = new UserId(entity.getUserId());
            var names = new UserNames(entity.getName(), entity.getLastName(), entity.getSecondName());
            var email = entity.getEmail() == null ? null : new UserEmail(entity.getEmail());
            var password = entity.getPassword() == null ? null : new UserPassword(entity.getPassword());
            var status = new AccountStatus(
                    entity.isEnabled(),
                    entity.isAccountNonExpired(),
                    entity.isAccountNonLocked(),
                    entity.isCredentialsNonExpired(),
                    statusValue
            );

            // Mapeo profundo hacia dominio (incluye roles y sus permisos)
            Set<RoleDomain> roles = entity.getRoles() == null ? new HashSet<>()
                    : entity.getRoles().stream()
                    .map(RoleJPAMapper::toDomain)
                    .collect(Collectors.toSet());

            return new UserDomain(id, names, email, password, status, roles);
        } catch (Exception e) {
            throw new MappingException("Error al transformar UserEntity a UserDomain. ID: " + idStr, e);
        }
    }

    public static User fromDomain(UserDomain domain) {
        String idStr = domain == null || domain.getUserId() == null ? "-" : domain.getUserId().id().toString();
        try {
            if (domain == null) return null;

            User entity = new User();
            entity.setUserId(domain.getUserId().id());
            entity.setName(domain.getNames().name());
            entity.setLastName(domain.getNames().lastName());
            entity.setSecondName(domain.getNames().secondName());
            entity.setEmail(domain.getEmail() == null ? null : domain.getEmail().value());
            entity.setPassword(domain.getPassword() == null ? null : domain.getPassword().value());

            if (domain.getStatus() != null) {
                entity.setEnabled(domain.getStatus().isEnabled());
                entity.setAccountNonExpired(domain.getStatus().accountNonExpired());
                entity.setAccountNonLocked(domain.getStatus().accountNonLocked());
                entity.setCredentialsNonExpired(domain.getStatus().credentialsNonExpired());
                entity.setStatus(domain.getStatus().status());
            } else {
                // valores por defecto para nueva entidad
                entity.setEnabled(true);
                entity.setAccountNonExpired(true);
                entity.setAccountNonLocked(true);
                entity.setCredentialsNonExpired(true);
                entity.setStatus(Status.ACTIVO);
            }

            // Mapeo de IDs de Roles para persistencia (referencias)
            if (domain.getRoles() != null) {
                entity.setRoles(domain.getRoles().stream()
                        .map(roleDomain -> {
                            Role roleEntity = new Role();
                            roleEntity.setRoleId(roleDomain.getRoleId().id());
                            return roleEntity;
                        })
                        .collect(Collectors.toSet()));
            }
            // entity.setStatus ya seteado arriba
            return entity;
        } catch (Exception e) {
            throw new MappingException("Error al transformar UserDomain a UserEntity. ID: " + idStr, e);
        }
    }
}
