package com.autorization.autorization.auth.domain.port.out;

import com.autorization.autorization.auth.domain.model.permission.vo.PermissionId;
import com.autorization.autorization.auth.domain.model.role.RoleDomain;
import com.autorization.autorization.auth.domain.model.role.vo.RoleId;
import com.autorization.autorization.auth.domain.model.role.vo.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RoleRepositoryPort {
    RoleDomain save(RoleDomain domain);
    Optional<RoleDomain> findById(RoleId id);
    Optional<RoleDomain> findByName(RoleName name);
    List<RoleDomain> findAll();
    boolean existsById(RoleId id);
    void deleteById(RoleId id);
    void updateEnabled(RoleId id, boolean enabled);
    Page<RoleDomain> searchByName(String name, Pageable pageable);
}
