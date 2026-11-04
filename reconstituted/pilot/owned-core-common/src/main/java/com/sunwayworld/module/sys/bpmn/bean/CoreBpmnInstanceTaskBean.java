/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_BPMN_INST_TASK")
public class CoreBpmnInstanceTaskBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    private static final long serialVersionUID = 8731362359855549210L;
    @Id
    private Long id;
    private Long instId;
    private String taskId;
    private String taskName;
    private String statusCode;
    @NotNull(defaultValue="0")
    private String counterSign;
    private String status;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstId() {
        return this.instId;
    }

    public void setInstId(Long instId) {
        this.instId = instId;
    }

    public String getTaskId() {
        return this.taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getCounterSign() {
        return this.counterSign;
    }

    public void setCounterSign(String counterSign) {
        this.counterSign = counterSign;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
