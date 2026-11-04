/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.customsurvey.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.customsurvey.bean.CoreCustomSurveyBean;
import com.sunwayworld.cloud.module.qms.customsurvey.persistent.dao.CoreCustomSurveyDao;
import com.sunwayworld.cloud.module.qms.customsurvey.persistent.mapper.CoreCustomSurveyMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCustomSurveyDaoImpl
extends MybatisDaoSupport<CoreCustomSurveyBean, Long>
implements CoreCustomSurveyDao {
    @Autowired
    private CoreCustomSurveyMapper coreCustomSurveyMapper;

    public CoreCustomSurveyMapper getMapper() {
        return this.coreCustomSurveyMapper;
    }
}
