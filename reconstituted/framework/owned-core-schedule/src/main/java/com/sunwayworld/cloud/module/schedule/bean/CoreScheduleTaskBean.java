/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_SCHEDULE_TASK")
public class CoreScheduleTaskBean
extends AbstractInsertable<String>
implements Insertable<String> {
    @Transient
    private static final long serialVersionUID = -7003779803159681376L;
    @Id
    private String id;
    private String taskName;
    private String serviceName;
    private String methodName;
    private String ifExecutableServiceName;
    private String ifExecutableMethodName;
    @NotNull(defaultValue="default")
    private String type;
    private String remark;
    private String taskScript;
    private String lcdpServiceMethod;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getIfExecutableServiceName() {
        return this.ifExecutableServiceName;
    }

    public void setIfExecutableServiceName(String ifExecutableServiceName) {
        this.ifExecutableServiceName = ifExecutableServiceName;
    }

    public String getIfExecutableMethodName() {
        return this.ifExecutableMethodName;
    }

    public void setIfExecutableMethodName(String ifExecutableMethodName) {
        this.ifExecutableMethodName = ifExecutableMethodName;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTaskScript() {
        return this.taskScript;
    }

    public void setTaskScript(String taskScript) {
        this.taskScript = taskScript;
    }

    public String getLcdpServiceMethod() {
        return this.lcdpServiceMethod;
    }

    public void setLcdpServiceMethod(String lcdpServiceMethod) {
        this.lcdpServiceMethod = lcdpServiceMethod;
    }
}
