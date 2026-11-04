/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.support.domain.BaseData;
import org.springframework.lang.Nullable;

public class CoreBpmnInstanceCandidatorDTO
extends AbstractBaseData
implements BaseData {
    private static final long serialVersionUID = 8526910356224985462L;
    private Long instId;
    private Long instTaskId;
    private String candidatorId;
    @Nullable
    private Long roleId;
    @Nullable
    private Long taskUserId;
    @Nullable
    private Long taskRoleId;

    public Long getInstId() {
        return this.instId;
    }

    public void setInstId(Long instId) {
        this.instId = instId;
    }

    public Long getInstTaskId() {
        return this.instTaskId;
    }

    public void setInstTaskId(Long instTaskId) {
        this.instTaskId = instTaskId;
    }

    public String getCandidatorId() {
        return this.candidatorId;
    }

    public void setCandidatorId(String candidatorId) {
        this.candidatorId = candidatorId;
    }

    public Long getRoleId() {
        return this.roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getTaskUserId() {
        return this.taskUserId;
    }

    public void setTaskUserId(Long taskUserId) {
        this.taskUserId = taskUserId;
    }

    public Long getTaskRoleId() {
        return this.taskRoleId;
    }

    public void setTaskRoleId(Long taskRoleId) {
        this.taskRoleId = taskRoleId;
    }
}
