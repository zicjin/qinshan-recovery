/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.bean;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorAlarmDTO;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorIndicatorDTO;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorMessageBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorServerDTO;
import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CoreMonitorMessageDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = -4609952110347485510L;
    private Map<String, Long> monitorConfig;
    private List<CoreMonitorServerDTO> serverList;
    private CoreMonitorIndicatorDTO systemIndicator;
    private CoreMonitorAlarmDTO systemAlarm;
    private List<CoreMonitorMessageBean> systemMessageList;

    public List<CoreMonitorServerDTO> getServerList() {
        return this.serverList;
    }

    public void setServerList(List<CoreMonitorServerDTO> serverList) {
        this.serverList = serverList;
    }

    public CoreMonitorIndicatorDTO getSystemIndicator() {
        return this.systemIndicator;
    }

    public void setSystemIndicator(CoreMonitorIndicatorDTO systemIndicator) {
        this.systemIndicator = systemIndicator;
    }

    public CoreMonitorAlarmDTO getSystemAlarm() {
        return this.systemAlarm;
    }

    public void setSystemAlarm(CoreMonitorAlarmDTO systemAlarm) {
        this.systemAlarm = systemAlarm;
    }

    public List<CoreMonitorMessageBean> getSystemMessageList() {
        return this.systemMessageList;
    }

    public void setSystemMessageList(List<CoreMonitorMessageBean> systemMessageList) {
        this.systemMessageList = systemMessageList;
    }

    public Map<String, Long> getMonitorConfig() {
        return this.monitorConfig;
    }

    public void setMonitorConfig(Map<String, Long> monitorConfig) {
        this.monitorConfig = monitorConfig;
    }
}
