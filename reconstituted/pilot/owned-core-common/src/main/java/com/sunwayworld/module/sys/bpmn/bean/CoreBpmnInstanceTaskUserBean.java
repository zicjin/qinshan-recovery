/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_BPMN_INST_TASK_USER")
public class CoreBpmnInstanceTaskUserBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    private static final long serialVersionUID = 1949248283735445930L;
    @Id
    private Long id;
    private Long instId;
    private Long instTaskId;
    private String userId;

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

    public Long getInstTaskId() {
        return this.instTaskId;
    }

    public void setInstTaskId(Long instTaskId) {
        this.instTaskId = instTaskId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
