/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.dailysupervision.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.dailysupervision.bean.CoreDailySupervisionBean;
import com.sunwayworld.cloud.module.qms.dailysupervision.persistent.dao.CoreDailySupervisionDao;
import com.sunwayworld.cloud.module.qms.dailysupervision.persistent.mapper.CoreDailySupervisionMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreDailySupervisionDaoImpl
extends MybatisDaoSupport<CoreDailySupervisionBean, Long>
implements CoreDailySupervisionDao {
    @Autowired
    private CoreDailySupervisionMapper coreDailySupervisionMapper;

    public CoreDailySupervisionMapper getMapper() {
        return this.coreDailySupervisionMapper;
    }
}
