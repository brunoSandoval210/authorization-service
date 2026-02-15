package com.autorization.autorization.auth.adapter.out.jpa.entity;

import com.autorization.autorization.shared.domain.model.Maintenance;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "modules")
public class Module extends Maintenance implements Serializable {
    @Id
    private UUID moduleId;

    private String name;
    private String path;
    private String icon;

    @Builder.Default
    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY)
    private List<Permission> permissions = new ArrayList<>();
}