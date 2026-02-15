package com.autorization.autorization.auth.domain.model.module;

import com.autorization.autorization.auth.domain.model.module.vo.ModuleIcon;
import com.autorization.autorization.auth.domain.model.module.vo.ModuleId;
import com.autorization.autorization.auth.domain.model.module.vo.ModuleName;
import com.autorization.autorization.auth.domain.model.module.vo.ModulePath;
import com.autorization.autorization.shared.domain.model.Status;
import com.autorization.autorization.shared.domain.exception.NullValueException;

public class ModuleDomain {
    private final ModuleId moduleId;
    private ModuleName name;
    private ModulePath path;
    private ModuleIcon icon;
    private Status status;

    public ModuleDomain(ModuleId moduleId, ModuleName name, ModulePath path, ModuleIcon icon, Status status) {
        if (moduleId == null) {
            throw new NullValueException("moduleId");
        }
        this.moduleId = moduleId;
        this.name = name;
        this.path = path;
        this.icon = icon;
        this.status = status;
    }

    public ModuleId getModuleId() { return moduleId; }
    public ModuleName getName() { return name; }
    public ModulePath getPath() { return path; }
    public ModuleIcon getIcon() { return icon; }
    public Status getStatus() { return status; }

    public void updateName(ModuleName newName) {
        this.name = newName;
    }

    public void updatePath(ModulePath newPath) {
        this.path = newPath;
    }

    public void updateIcon(ModuleIcon newIcon) {
        this.icon = newIcon;
    }
}