/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.persistent.dao;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpModulePageI18nBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import java.util.List;
import java.util.Map;

public interface LcdpModulePageI18nDao
extends GenericDao<LcdpModulePageI18nBean, Long> {
    public List<Map<String, Object>> selectAllI18nMessage(MapperParameter var1);

    public List<Map<String, Object>> selectEffectiveByCondition(MapperParameter var1);
}
