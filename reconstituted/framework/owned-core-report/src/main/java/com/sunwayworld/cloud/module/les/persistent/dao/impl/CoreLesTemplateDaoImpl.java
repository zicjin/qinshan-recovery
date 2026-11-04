/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.persistent.dao.impl;

import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateBean;
import com.sunwayworld.cloud.module.les.persistent.dao.CoreLesTemplateDao;
import com.sunwayworld.cloud.module.les.persistent.mapper.CoreLesTemplateMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreLesTemplateDaoImpl
extends MybatisDaoSupport<CoreLesTemplateBean, Long>
implements CoreLesTemplateDao {
    @Autowired
    private CoreLesTemplateMapper mapper;

    public CoreLesTemplateMapper getMapper() {
        return this.mapper;
    }
}
