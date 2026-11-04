/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.customsurvey.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.customsurvey.bean.CoreCustomSurveyItemBean;
import com.sunwayworld.cloud.module.qms.customsurvey.persistent.dao.CoreCustomSurveyItemDao;
import com.sunwayworld.cloud.module.qms.customsurvey.persistent.mapper.CoreCustomSurveyItemMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCustomSurveyItemDaoImpl
extends MybatisDaoSupport<CoreCustomSurveyItemBean, Long>
implements CoreCustomSurveyItemDao {
    @Autowired
    private CoreCustomSurveyItemMapper coreCustomSurveyItemMapper;

    public CoreCustomSurveyItemMapper getMapper() {
        return this.coreCustomSurveyItemMapper;
    }
}
