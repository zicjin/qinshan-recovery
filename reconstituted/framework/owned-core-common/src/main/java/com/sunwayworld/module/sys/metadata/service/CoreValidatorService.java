/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.metadata.service;

import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.module.sys.metadata.bean.CoreValidatorBean;
import java.util.List;

public interface CoreValidatorService {
    public <T extends Persistable<?>> List<String> validate(T var1);

    public List<CoreValidatorBean> selectEnabledValidatorByTable(String var1);
}
