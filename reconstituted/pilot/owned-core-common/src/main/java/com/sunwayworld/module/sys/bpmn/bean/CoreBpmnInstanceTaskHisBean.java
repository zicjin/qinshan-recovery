/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_BPMN_INST_TASK_HIS")
public class CoreBpmnInstanceTaskHisBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    private static final long serialVersionUID = -5189198580740014600L;
    @Id
    private Long id;
    private Long instId;
    private String statusCode;
    private String candidatorJson;

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

    public String getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getCandidatorJson() {
        return this.candidatorJson;
    }

    public void setCandidatorJson(String candidatorJson) {
        this.candidatorJson = candidatorJson;
    }
}
