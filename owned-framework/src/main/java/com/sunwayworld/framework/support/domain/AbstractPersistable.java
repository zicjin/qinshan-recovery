package com.sunwayworld.framework.support.domain;

import java.io.Serializable;

public abstract class AbstractPersistable<ID extends Serializable> extends AbstractBaseData implements Persistable<ID> {
    private static final long serialVersionUID = 1L;
}
