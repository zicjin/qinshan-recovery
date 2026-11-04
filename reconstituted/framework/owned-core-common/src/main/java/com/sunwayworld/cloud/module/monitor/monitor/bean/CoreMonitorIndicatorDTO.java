/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.bean;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorReqBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorUserBean;
import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;
import java.util.List;

public class CoreMonitorIndicatorDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = -607414836853160866L;
    private CoreMonitorUserBean curSystemUser;
    private List<CoreMonitorUserBean> systemUserList;
    private CoreMonitorReqBean curSystemReq;
    private List<CoreMonitorReqBean> systemReqList;

    public CoreMonitorReqBean getCurSystemReq() {
        return this.curSystemReq;
    }

    public void setCurSystemReq(CoreMonitorReqBean curSystemReq) {
        this.curSystemReq = curSystemReq;
    }

    public List<CoreMonitorReqBean> getSystemReqList() {
        return this.systemReqList;
    }

    public void setSystemReqList(List<CoreMonitorReqBean> systemReqList) {
        this.systemReqList = systemReqList;
    }

    public CoreMonitorUserBean getCurSystemUser() {
        return this.curSystemUser;
    }

    public void setCurSystemUser(CoreMonitorUserBean curSystemUser) {
        this.curSystemUser = curSystemUser;
    }

    public List<CoreMonitorUserBean> getSystemUserList() {
        return this.systemUserList;
    }

    public void setSystemUserList(List<CoreMonitorUserBean> systemUserList) {
        this.systemUserList = systemUserList;
    }
}
