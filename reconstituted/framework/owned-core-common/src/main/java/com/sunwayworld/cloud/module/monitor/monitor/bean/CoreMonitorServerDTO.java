/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.bean;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorAppRuntimeBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorServerBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorServerRuntimeBean;
import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;
import java.util.List;

public class CoreMonitorServerDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = 8696329927386499798L;
    private CoreMonitorServerBean serverInfo;
    private CoreMonitorServerRuntimeBean curServerRuntime;
    private List<CoreMonitorServerRuntimeBean> serverRuntimeList;
    private List<CoreMonitorAppRuntimeBean> appRuntimeList;

    public CoreMonitorServerBean getServerInfo() {
        return this.serverInfo;
    }

    public void setServerInfo(CoreMonitorServerBean serverInfo) {
        this.serverInfo = serverInfo;
    }

    public List<CoreMonitorServerRuntimeBean> getServerRuntimeList() {
        return this.serverRuntimeList;
    }

    public void setServerRuntimeList(List<CoreMonitorServerRuntimeBean> serverRuntimeList) {
        this.serverRuntimeList = serverRuntimeList;
    }

    public List<CoreMonitorAppRuntimeBean> getAppRuntimeList() {
        return this.appRuntimeList;
    }

    public void setAppRuntimeList(List<CoreMonitorAppRuntimeBean> appRuntimeList) {
        this.appRuntimeList = appRuntimeList;
    }

    public CoreMonitorServerRuntimeBean getCurServerRuntime() {
        return this.curServerRuntime;
    }

    public void setCurServerRuntime(CoreMonitorServerRuntimeBean curServerRuntime) {
        this.curServerRuntime = curServerRuntime;
    }
}
