/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsBean;
import com.sunwayworld.cloud.module.item.customds.persistent.dao.CoreCustomDsDao;
import com.sunwayworld.cloud.module.item.customds.persistent.mapper.CoreCustomDsMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCustomDsDaoImpl
extends MybatisDaoSupport<CoreCustomDsBean, Long>
implements CoreCustomDsDao {
    @Autowired
    private CoreCustomDsMapper coreCustomDsMapper;

    public CoreCustomDsMapper getMapper() {
        return this.coreCustomDsMapper;
    }
}
