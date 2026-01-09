/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Date;

/**
 *
 * @author thais
 */
public class Permission {
    private int permissionId;
    private String permissionName;
    private String permissionDescription;
    private String module;
    private Date createdAt;

    public Permission() {
    }

    public Permission(int permissionId, String permissionName, String permissionDescription, String module, Date createdAt) {
        this.permissionId = permissionId;
        this.permissionName = permissionName;
        this.permissionDescription = permissionDescription;
        this.module = module;
        this.createdAt = createdAt;
    }

    public int getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionDescription() {
        return permissionDescription;
    }

    public void setPermissionDescription(String permissionDescription) {
        this.permissionDescription = permissionDescription;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Permissions{" + "permissionId=" + permissionId + ", permissionName=" + permissionName + ", permissionDescription=" + permissionDescription + ", module=" + module + ", createdAt=" + createdAt + '}';
    }
}
