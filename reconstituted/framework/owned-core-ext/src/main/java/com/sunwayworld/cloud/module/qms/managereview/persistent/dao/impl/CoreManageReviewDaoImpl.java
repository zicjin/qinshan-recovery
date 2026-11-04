/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.managereview.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.managereview.bean.CoreManageReviewBean;
import com.sunwayworld.cloud.module.qms.managereview.persistent.dao.CoreManageReviewDao;
import com.sunwayworld.cloud.module.qms.managereview.persistent.mapper.CoreManageReviewMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreManageReviewDaoImpl
extends MybatisDaoSupport<CoreManageReviewBean, Long>
implements CoreManageReviewDao {
    @Autowired
    private CoreManageReviewMapper coreManageReviewMapper;

    public CoreManageReviewMapper getMapper() {
        return this.coreManageReviewMapper;
    }
}
