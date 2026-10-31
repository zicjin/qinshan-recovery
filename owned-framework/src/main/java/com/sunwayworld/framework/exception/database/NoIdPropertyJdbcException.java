package com.sunwayworld.framework.exception.database;

import com.sunwayworld.framework.support.domain.Persistable;

public class NoIdPropertyJdbcException extends RuntimeException {
    private static final long serialVersionUID = 2324908399049790700L;
    public NoIdPropertyJdbcException(Class<? extends Persistable<?>> persistableClass) { super("Persistable class [" + persistableClass.getName() + "]."); }
}
