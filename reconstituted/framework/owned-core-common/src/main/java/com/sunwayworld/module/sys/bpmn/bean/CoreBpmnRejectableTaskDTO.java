/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.support.domain.BaseData;
import com.sunwayworld.framework.utils.ObjectUtils;
import java.util.Optional;

public class CoreBpmnRejectableTaskDTO
extends AbstractBaseData
implements BaseData {
    private static final long serialVersionUID = -527699645477065148L;
    private String taskName;
    private String statusCode;

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

    public boolean equals(Object target) {
        if (target == null) {
            return false;
        }
        if (target == this) {
            return true;
        }
        if (!(target instanceof CoreBpmnRejectableTaskDTO)) {
            return false;
        }
        CoreBpmnRejectableTaskDTO task = (CoreBpmnRejectableTaskDTO)target;
        return ObjectUtils.equals(this.getTaskName(), task.getTaskName()) && ObjectUtils.equals(this.getStatusCode(), task.getStatusCode());
    }

    public int hashCode() {
        return (Optional.ofNullable(this.getTaskName()).orElse("") + "-" + Optional.ofNullable(this.getStatusCode()).orElse("")).hashCode();
    }
}
