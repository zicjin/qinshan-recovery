/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.metadata.dao;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.sys.metadata.bean.CoreTableBean;
import java.util.List;
import java.util.Map;

public interface CoreTableDao
extends GenericDao<CoreTableBean, String> {
    public List<Map<String, Object>> selectFieldListByCondition(MapperParameter var1);
}
