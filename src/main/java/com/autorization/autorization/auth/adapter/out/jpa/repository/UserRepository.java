package com.autorization.autorization.auth.adapter.out.jpa.repository;

import com.autorization.autorization.auth.adapter.out.jpa.entity.User;
import com.autorization.autorization.shared.domain.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    @Query("""
    SELECT u FROM User u
    WHERE (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))
      AND (:status IS NULL OR u.status = :status)
    """)
    Page<User> searchByUsernameOrUserId(
            String email,
            Status status,
            Pageable pageable
    );

    Integer countByStatus(Status status);
}
