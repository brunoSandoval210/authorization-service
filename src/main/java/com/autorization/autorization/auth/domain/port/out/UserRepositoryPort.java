package com.autorization.autorization.auth.domain.port.out;

import com.autorization.autorization.auth.domain.model.user.UserDomain;
import com.autorization.autorization.auth.domain.model.user.vo.UserEmail;
import com.autorization.autorization.auth.domain.model.user.vo.UserId;
import com.autorization.autorization.shared.domain.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    UserDomain save(UserDomain user);
    Optional<UserDomain> findById(UserId id);
    Optional<UserDomain> findByEmail(UserEmail email);
    List<UserDomain> findAll();
    boolean existsById(UserId id);
    void updateEnabled(UserId id, boolean enabled);
    Page<UserDomain> searchByUsernameOrUserId(String email, Status status, Pageable pageable);
}
