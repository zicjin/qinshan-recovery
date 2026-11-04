/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.customsrtsurvey.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.customsrtsurvey.bean.CoreCustomSrtSurveyItemBean;
import com.sunwayworld.cloud.module.qms.customsrtsurvey.persistent.dao.CoreCustomSrtSurveyItemDao;
import com.sunwayworld.cloud.module.qms.customsrtsurvey.persistent.mapper.CoreCustomSrtSurveyItemMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCustomSrtSurveyItemDaoImpl
extends MybatisDaoSupport<CoreCustomSrtSurveyItemBean, Long>
implements CoreCustomSrtSurveyItemDao {
    @Autowired
    private CoreCustomSrtSurveyItemMapper coreCustomSrtSurveyItemMapper;

    public CoreCustomSrtSurveyItemMapper getMapper() {
        return this.coreCustomSrtSurveyItemMapper;
    }
}
