/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpModulePageI18nBean;
import com.sunwayworld.cloud.module.lcdp.resource.persistent.dao.LcdpModulePageI18nDao;
import com.sunwayworld.cloud.module.lcdp.resource.persistent.mapper.LcdpModulePageI18nMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpModulePageI18nDaoImpl
extends MybatisDaoSupport<LcdpModulePageI18nBean, Long>
implements LcdpModulePageI18nDao {
    @Autowired
    private LcdpModulePageI18nMapper lcdpModulePageI18nMapper;

    public LcdpModulePageI18nMapper getMapper() {
        return this.lcdpModulePageI18nMapper;
    }

    @Override
    public List<Map<String, Object>> selectAllI18nMessage(MapperParameter parameter) {
        return this.lcdpModulePageI18nMapper.selectAllI18nMessage(parameter);
    }

    @Override
    public List<Map<String, Object>> selectEffectiveByCondition(MapperParameter parameter) {
        return this.lcdpModulePageI18nMapper.selectEffectiveByCondition(parameter);
    }
}
