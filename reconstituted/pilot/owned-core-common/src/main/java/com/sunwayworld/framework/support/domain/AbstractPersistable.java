/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.domain;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.support.domain.Persistable;
import java.io.Serializable;

public abstract class AbstractPersistable<ID extends Serializable>
extends AbstractBaseData
implements Persistable<ID> {
    @Transient
    private static final long serialVersionUID = -7975469100144912804L;
}
