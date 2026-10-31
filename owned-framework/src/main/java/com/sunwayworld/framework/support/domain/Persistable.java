package com.sunwayworld.framework.support.domain;

public interface Persistable<ID> extends BaseData {
    ID getId();
    void setId(ID id);
}
