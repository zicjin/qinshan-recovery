/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;

public class CoreBpmnDiagramTaskCandidatorDTO
extends AbstractBaseData {
    private static final long serialVersionUID = 2583267310032828731L;
    private String statusCode;
    private String taskName;
    private String special;
    private String roleId;
    private String userId;
    private String role;
    private String user;
    private String allUserId;
    private String allUser;

    public String getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getSpecial() {
        return this.special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAllUser() {
        return this.allUser;
    }

    public void setAllUser(String allUser) {
        this.allUser = allUser;
    }

    public String getAllUserId() {
        return this.allUserId;
    }

    public void setAllUserId(String allUserId) {
        this.allUserId = allUserId;
    }

    public String getRoleId() {
        return this.roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
