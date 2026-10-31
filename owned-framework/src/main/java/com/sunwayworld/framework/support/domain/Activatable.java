package com.sunwayworld.framework.support.domain;
/** Source-facing contract selected from the archived supplier inventory. */
public interface Activatable<ID extends java.io.Serializable> extends Persistable<ID> {
    String getActivatedFlag();
    void setActivatedFlag(String value);
    String getActivatedById();
    void setActivatedById(String value);
    String getActivatedByName();
    void setActivatedByName(String value);
    java.time.LocalDateTime getActivatedTime();
    void setActivatedTime(java.time.LocalDateTime value);
}
