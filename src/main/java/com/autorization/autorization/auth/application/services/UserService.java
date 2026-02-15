package com.autorization.autorization.auth.application.services;

import com.autorization.autorization.auth.adapter.in.web.request.CreateUserRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdateUserRequest;
import com.autorization.autorization.auth.application.dto.out.UserResponse;
import com.autorization.autorization.auth.application.dto.out.UserSecurityResponse;
import com.autorization.autorization.auth.application.services.mapper.UserMapper;
import com.autorization.autorization.auth.domain.exception.*;
import com.autorization.autorization.auth.domain.model.role.RoleDomain;
import com.autorization.autorization.auth.domain.model.role.vo.RoleId;
import com.autorization.autorization.auth.domain.model.user.UserDomain;
import com.autorization.autorization.auth.domain.model.user.vo.AccountStatus;
import com.autorization.autorization.auth.domain.model.user.vo.UserEmail;
import com.autorization.autorization.auth.domain.model.user.vo.UserId;
import com.autorization.autorization.auth.domain.model.user.vo.UserPassword;
import com.autorization.autorization.auth.domain.port.in.GetUserForAuthUseCase;
import com.autorization.autorization.auth.domain.port.in.UserUseCasePort;
import com.autorization.autorization.auth.domain.port.out.RoleRepositoryPort;
import com.autorization.autorization.auth.domain.port.out.UserRepositoryPort;
import com.autorization.autorization.shared.application.dto.PaginatedResponse;
import com.autorization.autorization.shared.domain.model.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación para la gestión de usuarios.
 *
 * Responsabilidades:
 * - Orquestar la creación y actualización de entidades UserDomain a través del UserRepositoryPort.
 * - Aplicar reglas de negocio simples: unicidad de email, normalización, codificación de contraseñas.
 * - Proveer operaciones de activación/desactivación y gestión de roles delegando al repositorio.
 *
 * Nota: la lógica de validación más fina (p.ej. reglas de contraseña) debería residir en el dominio o en validadores dedicados.
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserUseCasePort, GetUserForAuthUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final PasswordEncoder passwordEncoder; // inyectado

    /**
     * Crear un nuevo usuario.
     * - Normaliza el email (trim + lower).
     * - Verifica unicidad si email fue provisto.
     * - Genera dominio desde el request y asegura status por defecto ACTIVO.
     * - Codifica la contraseña antes de persistir.
     *
     * @param request CreateUserRequest con los datos de creación.
     * @return UserResponse con la representación del usuario creado.
     * @throws UserAlreadyExistsException si el email ya está en uso.
     */
    @Override
    public UserResponse create(CreateUserRequest request) {
        String emailNormalized = request.email() == null ? "" : request.email().trim().toLowerCase();

        // comprobar duplicado solo si email no está vacío
        if (!emailNormalized.isBlank()) {
            if (userRepositoryPort.findByEmail(new UserEmail(emailNormalized)).isPresent()){
                throw new UserAlreadyExistsException("El nombre de usuario ya está en uso");
            }
        }

        // mapear request -> dominio
        UserDomain newUser = UserMapper.toDomain(request);
        // asegurar email normalizado en el dominio (si viene)
        if (!emailNormalized.isBlank()) {
            newUser.changeEmail(new UserEmail(emailNormalized));
        }

        // asegurar status por defecto si por alguna razón es null
        if (newUser.getStatus() == null) {
            newUser.changeStatus(new AccountStatus(true, true, true, true, Status.ACTIVO));
        }

        // codificar contraseña antes de persistir
        if (newUser.getPassword() != null && newUser.getPassword().value() != null) {
            String encoded = passwordEncoder.encode(newUser.getPassword().value());
            newUser.changePassword(new UserPassword(encoded));
        }
        UserDomain saved = userRepositoryPort.save(newUser);
        return UserMapper.toResponse(saved);
    }

    /**
     * Actualizar un usuario existente (parcial).
     * - Valida unicidad de email cuando se solicita cambio.
     * - Aplica cambios parciales mediante UserMapper.applyUpdate.
     * - Codifica la contraseña si se cambia.
     *
     * @param id      UUID del usuario a actualizar.
     * @param request UpdateUserRequest con campos a modificar.
     * @return UserResponse con la representación actualizada.
     * @throws UserNotFoundException si no existe el usuario.
     * @throws UserAlreadyExistsException si el nuevo email ya pertenece a otro usuario.
     */
    @Override
    public UserResponse update(UUID id, UpdateUserRequest request) {
        UserId uid = new UserId(id);
        UserDomain existing = userRepositoryPort.findById(uid)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        // Validar email único si se intenta cambiar
        String newEmailStr = request.email();
        if (newEmailStr != null) {
            String emailNormalized = newEmailStr.trim().toLowerCase();
            if (!emailNormalized.isBlank()) {
                UserEmail newEmail = new UserEmail(emailNormalized);
                Optional<UserDomain> byEmail = userRepositoryPort.findByEmail(newEmail);
                if (byEmail.isPresent() && !byEmail.get().getUserId().id().equals(uid.id())) {
                    throw new UserAlreadyExistsException("El email ya está en uso por otro usuario");
                }
                existing.changeEmail(newEmail);
            } else {
                // si se envía email vacío, ignorar el cambio (o lanzar según política)
            }
        }

        // aplicar cambios parciales usando el mapper (aplica names/password si vienen)
        UserMapper.applyUpdate(existing, request);

        // si se ha cambiado la contraseña en el request, codificarla antes de persistir
        if (request.password() != null) {
            var pw = existing.getPassword();
            if (pw != null && pw.value() != null) {
                String encoded = passwordEncoder.encode(pw.value());
                existing.changePassword(new com.autorization.autorization.auth.domain.model.user.vo.UserPassword(encoded));
            }
        }

        UserDomain saved = userRepositoryPort.save(existing);
        return UserMapper.toResponse(saved);
    }

    /**
     * Desactivar un usuario (set enabled = false y status = INACTIVO).
     * Mantiene otras flags de cuenta (accountNonExpired, etc.) según el estado actual.
     *
     * @param id UUID del usuario a desactivar.
     * @throws UserNotFoundException si no existe el usuario.
     */
    @Override
    public void deactivate(UUID id) {
        UserId uid = new UserId(id);
        UserDomain existing = userRepositoryPort.findById(uid)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        // marcar como desactivado
        if (existing.getStatus() != null) {
            var s = existing.getStatus();
            existing.changeStatus(new AccountStatus(
                    false,
                    s.accountNonExpired(),
                    s.accountNonLocked(),
                    s.credentialsNonExpired(),
                    Status.INACTIVO
            ));
        } else {
            existing.changeStatus(new com.autorization.autorization.auth.domain.model.user.vo.AccountStatus(false, true, true, true, Status.INACTIVO));
        }

        userRepositoryPort.save(existing);
    }

    /**
     * Activar un usuario (set enabled = true y status = ACTIVO).
     *
     * @param id UUID del usuario a activar.
     * @throws UserNotFoundException si no existe el usuario.
     */
    @Override
    public void activate(UUID id) {
        UserId uid = new UserId(id);
        UserDomain existing = userRepositoryPort.findById(uid)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        if (existing.getStatus() != null) {
            var s = existing.getStatus();
            existing.changeStatus(new com.autorization.autorization.auth.domain.model.user.vo.AccountStatus(
                    true,
                    s.accountNonExpired(),
                    s.accountNonLocked(),
                    s.credentialsNonExpired(),
                    Status.ACTIVO
            ));
        } else {
            existing.changeStatus(new com.autorization.autorization.auth.domain.model.user.vo.AccountStatus(true, true, true, true, Status.ACTIVO));
        }

        userRepositoryPort.save(existing);
    }

    /**
     * Buscar usuario por id y devolver UserResponse si existe.
     */
    @Override
    public Optional<UserResponse> findById(UUID id) {
        UserId uid = new UserId(id);
        return userRepositoryPort.findById(uid).map(UserMapper::toResponse);
    }

    /**
     * Listar todos los usuarios (sin paginar).
     */
    @Override
    public List<UserResponse> findAll() {
        return userRepositoryPort.findAll().stream().map(UserMapper::toResponse).collect(Collectors.toList());
    }

    /**
     * Asignar rol a usuario (delegado al dominio).
     * @throws UserNotFoundException | RoleNotFoundException según corresponda.
     */
    @Override
    public void assignRole(UUID userId, UUID roleId) {
        UserId uid = new UserId(userId);
        RoleId rid = new RoleId(roleId);

        UserDomain user = userRepositoryPort.findById(uid)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        RoleDomain role = roleRepositoryPort.findById(rid)
                .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado"));

        // delegar regla de negocio al dominio (lanzará RoleAlreadyAssignedException si ya existe)
        user.addRole(role);
        userRepositoryPort.save(user);
    }

    /**
     * Revocar rol de usuario.
     */
    @Override
    public void revokeRole(UUID userId, UUID roleId) {
        UserId uid = new UserId(userId);
        RoleId rid = new RoleId(roleId);

        UserDomain user = userRepositoryPort.findById(uid)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        RoleDomain role = roleRepositoryPort.findById(rid)
                .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado"));

        user.removeRole(role);
        userRepositoryPort.save(user);
    }

    /**
     * Buscar usuarios con paginación y filtros opcionales.
     * - email: filtro parcial por email o userId.
     * - status: ACTIVO/INACTIVO
     *
     * @param email filtro opcional por email/userId
     * @param status estado opcional
     * @param page índice de página (0-based)
     * @param size tamaño de página
     * @return PaginatedResponse con UserResponse
     */
    @Override
    public PaginatedResponse<UserResponse> search(String email, Status status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<UserDomain> domainPage = userRepositoryPort.searchByUsernameOrUserId(email, status, pageable);

        List<UserDomain> domainList = domainPage.getContent();

        // mapear domainList -> List<UserResponse>
        List<UserResponse> responseList = domainList.stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                responseList,
                domainPage.getNumber(),
                domainPage.getSize(),
                domainPage.getTotalElements(),
                domainPage.getTotalPages(),
                domainPage.isLast()
        );
    }

    @Override
    public UserSecurityResponse execute(String email) {
        UserEmail userEmail = new UserEmail(email);
        return userRepositoryPort.findByEmail(userEmail)
                .map(u -> {
                    List<String> authorities = new ArrayList<>();

                    u.getRoles().forEach(role -> {
                        authorities.add("ROLE_" + role.getName().value());

                        role.getPermissions().forEach(perm ->
                                authorities.add(perm.getName().value())
                        );
                    });

                    return new UserSecurityResponse(
                            u.getEmail().value(),
                            u.getPassword().value(),
                            authorities,
                            u.getStatus().isEnabled()
                    );
                })
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
    }
}
