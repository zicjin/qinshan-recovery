/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Activatable;
import com.sunwayworld.framework.support.domain.Insertable;
import com.sunwayworld.framework.utils.ObjectUtils;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_SCHEDULE_TASK_CONFIG")
public class CoreScheduleTaskConfigBean
extends AbstractInsertable<String>
implements Insertable<String>,
Activatable<String> {
    @Transient
    private static final long serialVersionUID = -659803964636885762L;
    @Id
    private String id;
    private String uid;
    private String configName;
    private String taskId;
    private String cron;
    private String param;
    @NotNull(defaultValue="0")
    private Long times;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    private String remark;
    @NotNull(defaultValue="0")
    private String activatedFlag;
    private String activatedById;
    private String activatedByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activatedTime;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getConfigName() {
        return this.configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getTaskId() {
        return this.taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getCron() {
        return this.cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getParam() {
        return this.param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Long getTimes() {
        return this.times;
    }

    public void setTimes(Long times) {
        this.times = times;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String getActivatedFlag() {
        return this.activatedFlag;
    }

    @Override
    public void setActivatedFlag(String activatedFlag) {
        this.activatedFlag = activatedFlag;
    }

    @Override
    public String getActivatedById() {
        return this.activatedById;
    }

    @Override
    public void setActivatedById(String activatedById) {
        this.activatedById = activatedById;
    }

    @Override
    public String getActivatedByName() {
        return this.activatedByName;
    }

    @Override
    public void setActivatedByName(String activatedByName) {
        this.activatedByName = activatedByName;
    }

    @Override
    public LocalDateTime getActivatedTime() {
        return this.activatedTime;
    }

    @Override
    public void setActivatedTime(LocalDateTime activatedTime) {
        this.activatedTime = activatedTime;
    }

    public boolean equals(Object target) {
        if (target == null) {
            return false;
        }
        if (target == this) {
            return true;
        }
        if (!CoreScheduleTaskConfigBean.class.equals(target.getClass())) {
            return false;
        }
        CoreScheduleTaskConfigBean taskConfig = (CoreScheduleTaskConfigBean)target;
        return ObjectUtils.equals(taskConfig.getId(), this.getId()) && ObjectUtils.equals(taskConfig.getCron(), this.getCron()) && ObjectUtils.equals(taskConfig.getParam(), this.getParam()) && ObjectUtils.equals(taskConfig.getTaskId(), this.getTaskId()) && ObjectUtils.equals(taskConfig.getStartTime(), this.getStartTime()) && ObjectUtils.equals(taskConfig.getEndTime(), this.getEndTime());
    }

    public boolean updated(CoreScheduleTaskConfigBean target) {
        return ObjectUtils.equals(this.getId(), target.getId()) && (!ObjectUtils.equals(this.getCron(), target.getCron()) || !ObjectUtils.equals(this.getParam(), target.getParam()) || !ObjectUtils.equals(this.getStartTime(), target.getStartTime()) || !ObjectUtils.equals(this.getEndTime(), target.getEndTime()));
    }
}
