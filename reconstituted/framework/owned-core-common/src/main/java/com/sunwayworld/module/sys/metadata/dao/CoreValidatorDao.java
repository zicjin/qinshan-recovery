/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.metadata.dao;

import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.sys.metadata.bean.CoreValidatorBean;
import java.util.List;

public interface CoreValidatorDao
extends GenericDao<CoreValidatorBean, Long> {
    public List<CoreValidatorBean> selectEnabledValidatorByTable(String var1);
}
