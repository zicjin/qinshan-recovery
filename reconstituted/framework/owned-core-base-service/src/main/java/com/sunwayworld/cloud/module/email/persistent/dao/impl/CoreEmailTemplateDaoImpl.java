/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.persistent.dao.impl;

import com.sunwayworld.cloud.module.email.bean.CoreEmailTemplateBean;
import com.sunwayworld.cloud.module.email.persistent.dao.CoreEmailTemplateDao;
import com.sunwayworld.cloud.module.email.persistent.mapper.CoreEmailTemplateMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreEmailTemplateDaoImpl
extends MybatisDaoSupport<CoreEmailTemplateBean, String>
implements CoreEmailTemplateDao {
    @Autowired
    private CoreEmailTemplateMapper mapper;

    public CoreEmailTemplateMapper getMapper() {
        return this.mapper;
    }
}
