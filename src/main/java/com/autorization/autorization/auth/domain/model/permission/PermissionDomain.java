package com.autorization.autorization.auth.domain.model.permission;


import com.autorization.autorization.auth.domain.model.permission.vo.PermissionDescription;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionId;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionModule;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionName;
import com.autorization.autorization.shared.domain.model.Status;
import com.autorization.autorization.shared.domain.exception.NullValueException;

public class PermissionDomain{
    private final PermissionId permissionId;
    private PermissionName name;
    private PermissionDescription description;
    private PermissionModule module;
    private Status status;

    public PermissionDomain(PermissionId permissionId, PermissionName name, PermissionDescription description, PermissionModule module, Status status) {
        if (permissionId == null) {
            throw new NullValueException("permissionId");
        }
        if (name == null) {
            throw new NullValueException("permissionName");
        }
        // description may be null
        this.permissionId = permissionId;
        this.name = name;
        this.description = description;
        this.module = module;
        this.status = status;
    }

    public PermissionId getPermissionId() {
        return permissionId;
    }

    public PermissionName getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public PermissionDescription getDescription() {
        return description;
    }

    public PermissionModule getModule() {
        return module;
    }

    public void updateName(PermissionName newName) {
        if (newName == null) {
            throw new NullValueException("permissionName");
        }
        this.name = newName;
    }

    public void updateDescription(PermissionDescription newDescription) {
        this.description = newDescription;
    }

}
