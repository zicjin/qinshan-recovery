package com.sunwayworld.framework.support.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class AbstractInsertable<ID extends Serializable> extends AbstractPersistable<ID> implements Insertable<ID> {
    private static final long serialVersionUID = 1L;
    private String createdById, createdByName, createdByOrgId, createdByOrgName;
    private LocalDateTime createdTime;
    public String getCreatedById() { return createdById; } public void setCreatedById(String v) { createdById = v; }
    public String getCreatedByName() { return createdByName; } public void setCreatedByName(String v) { createdByName = v; }
    public LocalDateTime getCreatedTime() { return createdTime; } public void setCreatedTime(LocalDateTime v) { createdTime = v; }
    public String getCreatedByOrgId() { return createdByOrgId; } public void setCreatedByOrgId(String v) { createdByOrgId = v; }
    public String getCreatedByOrgName() { return createdByOrgName; } public void setCreatedByOrgName(String v) { createdByOrgName = v; }
}
