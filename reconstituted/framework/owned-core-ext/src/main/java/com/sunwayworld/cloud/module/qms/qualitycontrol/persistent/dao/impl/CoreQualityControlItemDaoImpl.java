/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.qualitycontrol.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.qualitycontrol.bean.CoreQualityControlItemBean;
import com.sunwayworld.cloud.module.qms.qualitycontrol.persistent.dao.CoreQualityControlItemDao;
import com.sunwayworld.cloud.module.qms.qualitycontrol.persistent.mapper.CoreQualityControlItemMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreQualityControlItemDaoImpl
extends MybatisDaoSupport<CoreQualityControlItemBean, Long>
implements CoreQualityControlItemDao {
    @Autowired
    private CoreQualityControlItemMapper coreQualityControlItemMapper;

    public CoreQualityControlItemMapper getMapper() {
        return this.coreQualityControlItemMapper;
    }
}
