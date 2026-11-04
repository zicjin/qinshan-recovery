/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.qualitycontrol.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.qualitycontrol.bean.CoreQualityControlBean;
import com.sunwayworld.cloud.module.qms.qualitycontrol.persistent.dao.CoreQualityControlDao;
import com.sunwayworld.cloud.module.qms.qualitycontrol.persistent.mapper.CoreQualityControlMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreQualityControlDaoImpl
extends MybatisDaoSupport<CoreQualityControlBean, Long>
implements CoreQualityControlDao {
    @Autowired
    private CoreQualityControlMapper coreQualityControlMapper;

    public CoreQualityControlMapper getMapper() {
        return this.coreQualityControlMapper;
    }
}
