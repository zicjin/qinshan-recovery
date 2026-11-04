/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.i18n.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.i18n.CoreI18nCache;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nMessageBean;
import com.sunwayworld.module.sys.i18n.dao.CoreI18nMessageDao;
import com.sunwayworld.module.sys.i18n.mapper.CoreI18nMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreI18nMessageDaoImpl
extends MybatisDaoSupport<CoreI18nMessageBean, Long>
implements CoreI18nMessageDao {
    @Autowired
    private CoreI18nMessageMapper mapper;

    public CoreI18nMessageMapper getMapper() {
        return this.mapper;
    }

    @Override
    public void cacheEvict(CoreI18nMessageBean oldItem, CoreI18nMessageBean newItem) {
        CoreI18nCache.instance().reload();
    }
}
