/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.metadata.dao;

import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.sys.metadata.bean.CoreSubmitValidatorBean;
import java.util.List;
import java.util.Map;

public interface CoreSubmitValidatorDao
extends GenericDao<CoreSubmitValidatorBean, Long> {
    public List<Map<String, Object>> selectSubmitValidatorByTable(String var1);
}
