/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.sys.monitor.pm.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_SYSTEM_MONITOR_SQL")
public class CoreSystemMonitorSqlBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -8454379468283072421L;
    @Id
    private Long id;
    @Clob
    private String sql;
    private String sqlSource;
    private Double timeConsuming;
    @NotNull(defaultValue="0")
    private String revisionFlag;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime executeTime;
    private String triggerCategory;
    private Integer sqlTimes;
    private String businessType;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getSql() {
        return this.sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getSqlSource() {
        return this.sqlSource;
    }

    public void setSqlSource(String sqlSource) {
        this.sqlSource = sqlSource;
    }

    public Double getTimeConsuming() {
        return this.timeConsuming;
    }

    public void setTimeConsuming(Double timeConsuming) {
        this.timeConsuming = timeConsuming;
    }

    public String getRevisionFlag() {
        return this.revisionFlag;
    }

    public void setRevisionFlag(String revisionFlag) {
        this.revisionFlag = revisionFlag;
    }

    public LocalDateTime getExecuteTime() {
        return this.executeTime;
    }

    public void setExecuteTime(LocalDateTime executeTime) {
        this.executeTime = executeTime;
    }

    public String getTriggerCategory() {
        return this.triggerCategory;
    }

    public void setTriggerCategory(String triggerCategory) {
        this.triggerCategory = triggerCategory;
    }

    public Integer getSqlTimes() {
        return this.sqlTimes;
    }

    public void setSqlTimes(Integer sqlTimes) {
        this.sqlTimes = sqlTimes;
    }

    public String getBusinessType() {
        return this.businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }
}
