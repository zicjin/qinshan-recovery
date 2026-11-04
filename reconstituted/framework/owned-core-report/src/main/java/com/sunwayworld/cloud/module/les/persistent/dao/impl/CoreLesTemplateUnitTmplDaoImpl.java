/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.persistent.dao.impl;

import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateUnitTmplBean;
import com.sunwayworld.cloud.module.les.persistent.dao.CoreLesTemplateUnitTmplDao;
import com.sunwayworld.cloud.module.les.persistent.mapper.CoreLesTemplateUnitTmplMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreLesTemplateUnitTmplDaoImpl
extends MybatisDaoSupport<CoreLesTemplateUnitTmplBean, Long>
implements CoreLesTemplateUnitTmplDao {
    @Autowired
    private CoreLesTemplateUnitTmplMapper mapper;

    public CoreLesTemplateUnitTmplMapper getMapper() {
        return this.mapper;
    }
}
