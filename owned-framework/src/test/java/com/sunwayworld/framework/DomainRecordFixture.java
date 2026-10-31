package com.sunwayworld.framework;

import com.sunwayworld.framework.support.domain.AbstractInsertable;

/** Exercises inherited audit accessors that EmcEmpBean overrides itself. */
public final class DomainRecordFixture extends AbstractInsertable<String> {
    private static final long serialVersionUID = 1L;
    private String id;
    public String getId() { return id; }
    public void setId(String value) { id = value; }
}
