package com.autorization.autorization.auth.domain.port.out;

import com.autorization.autorization.auth.domain.model.module.ModuleDomain;
import com.autorization.autorization.auth.domain.model.module.vo.ModuleId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ModuleRepositoryPort {
    ModuleDomain save(ModuleDomain module);
    Optional<ModuleDomain> findById(ModuleId id);
    List<ModuleDomain> findAll();
    void deleteById(ModuleId id);
    void updateEnabled(ModuleId id, boolean enabled);
    Page<ModuleDomain> searchByName(String name, Pageable pageable);
}