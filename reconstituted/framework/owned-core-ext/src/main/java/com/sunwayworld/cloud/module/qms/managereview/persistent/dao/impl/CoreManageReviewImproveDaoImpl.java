/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.managereview.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.managereview.bean.CoreManageReviewImproveBean;
import com.sunwayworld.cloud.module.qms.managereview.persistent.dao.CoreManageReviewImproveDao;
import com.sunwayworld.cloud.module.qms.managereview.persistent.mapper.CoreManageReviewImproveMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreManageReviewImproveDaoImpl
extends MybatisDaoSupport<CoreManageReviewImproveBean, Long>
implements CoreManageReviewImproveDao {
    @Autowired
    private CoreManageReviewImproveMapper coreManageReviewImproveMapper;

    public CoreManageReviewImproveMapper getMapper() {
        return this.coreManageReviewImproveMapper;
    }
}
