/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.i18n.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nConfigBean;
import com.sunwayworld.module.sys.i18n.dao.CoreI18nConfigDao;
import com.sunwayworld.module.sys.i18n.mapper.CoreI18nConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreI18nConfigDaoImpl
extends MybatisDaoSupport<CoreI18nConfigBean, String>
implements CoreI18nConfigDao {
    @Autowired
    private CoreI18nConfigMapper mapper;

    public CoreI18nConfigMapper getMapper() {
        return this.mapper;
    }
}
