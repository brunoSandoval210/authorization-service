package com.autorization.autorization.auth.adapter.out.jpa.repository;

import com.autorization.autorization.auth.adapter.out.jpa.entity.Permission;
import com.autorization.autorization.shared.domain.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    boolean existsByName(String name);
    Optional<Permission> findByName(String name);
    List<Permission> findByPermissionIdIn(List<Long> permissionIds);
    @Query("""
    SELECT p FROM Permission p
    WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    Page<Permission> searchByName(String name, Pageable pageable);

    Integer countByStatus(Status status);
}
