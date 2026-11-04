/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsSqlBean;
import com.sunwayworld.cloud.module.item.customds.persistent.dao.CoreCustomDsSqlDao;
import com.sunwayworld.cloud.module.item.customds.persistent.mapper.CoreCustomDsSqlMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCustomDsSqlDaoImpl
extends MybatisDaoSupport<CoreCustomDsSqlBean, Long>
implements CoreCustomDsSqlDao {
    @Autowired
    private CoreCustomDsSqlMapper coreCustomDsSqlMapper;

    public CoreCustomDsSqlMapper getMapper() {
        return this.coreCustomDsSqlMapper;
    }
}
