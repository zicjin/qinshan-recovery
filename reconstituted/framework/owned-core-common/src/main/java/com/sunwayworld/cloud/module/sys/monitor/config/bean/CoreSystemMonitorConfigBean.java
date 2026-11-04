/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.sys.monitor.config.bean;

import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_SYSTEM_MONITOR_CONFIG")
public class CoreSystemMonitorConfigBean
extends AbstractPersistable<String>
implements Persistable<String> {
    @Transient
    private static final long serialVersionUID = 5729253581687528506L;
    @Id
    private String id;
    private String configName;
    @Clob
    private String configValue;
    private String configCategory;
    private String remark;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getConfigName() {
        return this.configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getConfigValue() {
        return this.configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getConfigCategory() {
        return this.configCategory;
    }

    public void setConfigCategory(String configCategory) {
        this.configCategory = configCategory;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
