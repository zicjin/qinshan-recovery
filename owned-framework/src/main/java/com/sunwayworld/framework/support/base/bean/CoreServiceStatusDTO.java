package com.sunwayworld.framework.support.base.bean;

/** Recorded source contract. Unimplemented operations fail explicitly. */
public class CoreServiceStatusDTO extends com.sunwayworld.framework.support.domain.AbstractBaseData implements java.io.Serializable {
    public CoreServiceStatusDTO() {  }
    public CoreServiceStatusDTO.AuditStatus getAuditStatus() { throw new UnsupportedOperationException("Owned read-only boundary: CoreServiceStatusDTO.getAuditStatus is not implemented"); }
    public void setAuditStatus(com.sunwayworld.framework.support.base.bean.CoreServiceStatusDTO.AuditStatus p0) { throw new UnsupportedOperationException("Owned read-only boundary: CoreServiceStatusDTO.setAuditStatus is not implemented"); }
    public CoreServiceStatusDTO.ActiveStatus getActiveStatus() { throw new UnsupportedOperationException("Owned read-only boundary: CoreServiceStatusDTO.getActiveStatus is not implemented"); }
    public void setActiveStatus(com.sunwayworld.framework.support.base.bean.CoreServiceStatusDTO.ActiveStatus p0) { throw new UnsupportedOperationException("Owned read-only boundary: CoreServiceStatusDTO.setActiveStatus is not implemented"); }
    public CoreServiceStatusDTO.SuspendStatus getSuspendStatus() { throw new UnsupportedOperationException("Owned read-only boundary: CoreServiceStatusDTO.getSuspendStatus is not implemented"); }
    public void setSuspendStatus(com.sunwayworld.framework.support.base.bean.CoreServiceStatusDTO.SuspendStatus p0) { throw new UnsupportedOperationException("Owned read-only boundary: CoreServiceStatusDTO.setSuspendStatus is not implemented"); }
    public java.lang.String getViewConfig() { throw new UnsupportedOperationException("Owned read-only boundary: CoreServiceStatusDTO.getViewConfig is not implemented"); }
    public void setViewConfig(java.lang.String p0) { throw new UnsupportedOperationException("Owned read-only boundary: CoreServiceStatusDTO.setViewConfig is not implemented"); }
    public static class AuditStatus implements java.io.Serializable {
        public AuditStatus() {  }
        public java.lang.String getProcessStatus() { throw new UnsupportedOperationException("Owned read-only boundary: AuditStatus.getProcessStatus is not implemented"); }
        public void setProcessStatus(java.lang.String p0) { throw new UnsupportedOperationException("Owned read-only boundary: AuditStatus.setProcessStatus is not implemented"); }
        public java.lang.String getStatusCode() { throw new UnsupportedOperationException("Owned read-only boundary: AuditStatus.getStatusCode is not implemented"); }
        public void setStatusCode(java.lang.String p0) { throw new UnsupportedOperationException("Owned read-only boundary: AuditStatus.setStatusCode is not implemented"); }
    }
    public static class ActiveStatus implements java.io.Serializable {
        public ActiveStatus() {  }
        public java.lang.String getActivatedFlag() { throw new UnsupportedOperationException("Owned read-only boundary: ActiveStatus.getActivatedFlag is not implemented"); }
        public void setActivatedFlag(java.lang.String p0) { throw new UnsupportedOperationException("Owned read-only boundary: ActiveStatus.setActivatedFlag is not implemented"); }
    }
    public static class SuspendStatus implements java.io.Serializable {
        public SuspendStatus() {  }
        public java.lang.String getLastSuspendedFlag() { throw new UnsupportedOperationException("Owned read-only boundary: SuspendStatus.getLastSuspendedFlag is not implemented"); }
        public void setLastSuspendedFlag(java.lang.String p0) { throw new UnsupportedOperationException("Owned read-only boundary: SuspendStatus.setLastSuspendedFlag is not implemented"); }
    }
}
