/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.dailysupervision.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.dailysupervision.bean.CoreDailySupervisionItemBean;
import com.sunwayworld.cloud.module.qms.dailysupervision.persistent.dao.CoreDailySupervisionItemDao;
import com.sunwayworld.cloud.module.qms.dailysupervision.persistent.mapper.CoreDailySupervisionItemMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreDailySupervisionItemDaoImpl
extends MybatisDaoSupport<CoreDailySupervisionItemBean, Long>
implements CoreDailySupervisionItemDao {
    @Autowired
    private CoreDailySupervisionItemMapper coreDailySupervisionItemMapper;

    public CoreDailySupervisionItemMapper getMapper() {
        return this.coreDailySupervisionItemMapper;
    }
}
