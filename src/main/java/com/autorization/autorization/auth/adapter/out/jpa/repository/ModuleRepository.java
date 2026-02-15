package com.autorization.autorization.auth.adapter.out.jpa.repository;

import com.autorization.autorization.auth.adapter.out.jpa.entity.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ModuleRepository extends JpaRepository<Module, UUID> {
    Optional<Module> findByName(String name);
    boolean existsByName(String name);

    @Query("""
    SELECT m FROM Module m
    WHERE (:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    Page<Module> searchByName(String name, Pageable pageable);
}
