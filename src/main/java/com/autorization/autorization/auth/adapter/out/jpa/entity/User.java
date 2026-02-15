package com.autorization.autorization.auth.adapter.out.jpa.entity;

import com.autorization.autorization.shared.domain.model.Maintenance;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends Maintenance implements Serializable {
    @Id
    private UUID userId;

    private String name;
    private String lastName;
    private String secondName;

    @Column(unique = true, nullable = false)
    private String email;
    private String password;

    @Column(name = "is_enabled")
    private boolean isEnabled;// true si el usuario esta habilitado, false si no lo esta

    @Column(name = "account_non_expired")
    private boolean accountNonExpired;// true si la cuenta no ha expirado, false si ha expirado

    @Column(name = "account_non_locked")
    private boolean accountNonLocked;// true si la cuenta no esta bloqueada, false si esta bloqueada

    @Column(name = "credentials_non_expired")
    private boolean credentialsNonExpired;// true si las credenciales no han expirado, false si han expirado

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY) // EAGER para que se carguen los roles al cargar el usuario y cascade ALL para
                                        // que se guarden los roles al guardar el usuario
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
}
