/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.metadata.dao;

import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.sys.metadata.bean.CoreImpValidatorBean;
import java.util.List;
import java.util.Map;

public interface CoreImpValidatorDao
extends GenericDao<CoreImpValidatorBean, Long> {
    public List<Map<String, Object>> selectImpValidatorByTable(String var1);
}
