package com.autorization.autorization.auth.adapter.out.jpa.repository;


import com.autorization.autorization.auth.adapter.out.jpa.entity.Role;
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
public interface RoleRepository extends JpaRepository<Role,UUID> {

    List<Role> findByRoleIdIn(List<UUID> roleIds);
    Optional<Role> findByName(String roleName);

    @Query("""
    SELECT r FROM Role r
    WHERE (:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    Page<Role> searchByName(String name, Pageable pageable);
    Integer countByStatus(Status status);
}
