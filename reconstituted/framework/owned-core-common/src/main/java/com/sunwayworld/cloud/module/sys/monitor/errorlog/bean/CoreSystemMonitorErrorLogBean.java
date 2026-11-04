/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.sys.monitor.errorlog.bean;

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

@Table(value="T_CORE_SYSTEM_MONITOR_ERRORLOG")
public class CoreSystemMonitorErrorLogBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -3490447880273636445L;
    @Id
    private Long id;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime logTime;
    private String LogDesc;
    private String logCategory;
    private String logModule;
    private String logInterfaceName;
    private String triggerCategory;
    @Clob
    private String logDetail;
    @NotNull(defaultValue="1")
    private Long logTimes;
    @NotNull(defaultValue="0")
    private String revisionFlag;
    private String remark;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getLogTime() {
        return this.logTime;
    }

    public void setLogTime(LocalDateTime logTime) {
        this.logTime = logTime;
    }

    public String getLogDesc() {
        return this.LogDesc;
    }

    public void setLogDesc(String logDesc) {
        this.LogDesc = logDesc;
    }

    public String getLogCategory() {
        return this.logCategory;
    }

    public void setLogCategory(String logCategory) {
        this.logCategory = logCategory;
    }

    public String getLogModule() {
        return this.logModule;
    }

    public void setLogModule(String logModule) {
        this.logModule = logModule;
    }

    public String getLogInterfaceName() {
        return this.logInterfaceName;
    }

    public void setLogInterfaceName(String logInterfaceName) {
        this.logInterfaceName = logInterfaceName;
    }

    public String getTriggerCategory() {
        return this.triggerCategory;
    }

    public void setTriggerCategory(String triggerCategory) {
        this.triggerCategory = triggerCategory;
    }

    public String getLogDetail() {
        return this.logDetail;
    }

    public void setLogDetail(String logDetail) {
        this.logDetail = logDetail;
    }

    public Long getLogTimes() {
        return this.logTimes;
    }

    public void setLogTimes(Long logTimes) {
        this.logTimes = logTimes;
    }

    public String getRevisionFlag() {
        return this.revisionFlag;
    }

    public void setRevisionFlag(String revisionFlag) {
        this.revisionFlag = revisionFlag;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
