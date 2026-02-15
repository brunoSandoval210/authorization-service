package com.autorization.autorization.auth.adapter.out.jpa.entity;

import com.autorization.autorization.shared.domain.model.Maintenance;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "permissions")
public class Permission extends Maintenance implements Serializable {

    @Id
    private UUID permissionId;

    @Column(name = "permission_name",unique = true, nullable = false)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Module module;

}
