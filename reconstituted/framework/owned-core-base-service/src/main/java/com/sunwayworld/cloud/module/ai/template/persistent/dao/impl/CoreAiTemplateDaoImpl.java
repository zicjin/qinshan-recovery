/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.ai.template.persistent.dao.impl;

import com.sunwayworld.cloud.module.ai.template.bean.CoreAiTemplateBean;
import com.sunwayworld.cloud.module.ai.template.persistent.dao.CoreAiTemplateDao;
import com.sunwayworld.cloud.module.ai.template.persistent.mapper.CoreAiTemplateMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreAiTemplateDaoImpl
extends MybatisDaoSupport<CoreAiTemplateBean, Long>
implements CoreAiTemplateDao {
    @Autowired
    private CoreAiTemplateMapper coreAiTemplateMapper;

    public CoreAiTemplateMapper getMapper() {
        return this.coreAiTemplateMapper;
    }
}
