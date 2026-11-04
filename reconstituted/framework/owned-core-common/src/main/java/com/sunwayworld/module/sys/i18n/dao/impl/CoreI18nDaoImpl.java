/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.i18n.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nBean;
import com.sunwayworld.module.sys.i18n.dao.CoreI18nDao;
import com.sunwayworld.module.sys.i18n.mapper.CoreI18nMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreI18nDaoImpl
extends MybatisDaoSupport<CoreI18nBean, Long>
implements CoreI18nDao {
    @Autowired
    private CoreI18nMapper mapper;

    public CoreI18nMapper getMapper() {
        return this.mapper;
    }
}
