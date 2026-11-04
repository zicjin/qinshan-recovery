/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.base.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import java.io.Serializable;

public class CoreServiceStatusDTO
extends AbstractBaseData
implements Serializable {
    @Transient
    private static final long serialVersionUID = -5316024009848891139L;
    private AuditStatus auditStatus;
    private ActiveStatus activeStatus;
    private SuspendStatus suspendStatus;
    private String viewConfig;

    public AuditStatus getAuditStatus() {
        return this.auditStatus;
    }

    public void setAuditStatus(AuditStatus auditStatus) {
        this.auditStatus = auditStatus;
    }

    public ActiveStatus getActiveStatus() {
        return this.activeStatus;
    }

    public void setActiveStatus(ActiveStatus activeStatus) {
        this.activeStatus = activeStatus;
    }

    public SuspendStatus getSuspendStatus() {
        return this.suspendStatus;
    }

    public void setSuspendStatus(SuspendStatus suspendStatus) {
        this.suspendStatus = suspendStatus;
    }

    public String getViewConfig() {
        return this.viewConfig;
    }

    public void setViewConfig(String viewConfig) {
        this.viewConfig = viewConfig;
    }

    public static class SuspendStatus
    implements Serializable {
        private static final long serialVersionUID = 2801146988409729148L;
        private String lastSuspendedFlag;

        public String getLastSuspendedFlag() {
            return this.lastSuspendedFlag;
        }

        public void setLastSuspendedFlag(String lastSuspendedFlag) {
            this.lastSuspendedFlag = lastSuspendedFlag;
        }
    }

    public static class ActiveStatus
    implements Serializable {
        private static final long serialVersionUID = -8484695052903174804L;
        private String activatedFlag;

        public String getActivatedFlag() {
            return this.activatedFlag;
        }

        public void setActivatedFlag(String activatedFlag) {
            this.activatedFlag = activatedFlag;
        }
    }

    public static class AuditStatus
    implements Serializable {
        private static final long serialVersionUID = -903273228694150293L;
        private String processStatus;
        private String statusCode;

        public String getProcessStatus() {
            return this.processStatus;
        }

        public void setProcessStatus(String processStatus) {
            this.processStatus = processStatus;
        }

        public String getStatusCode() {
            return this.statusCode;
        }

        public void setStatusCode(String statusCode) {
            this.statusCode = statusCode;
        }
    }
}
