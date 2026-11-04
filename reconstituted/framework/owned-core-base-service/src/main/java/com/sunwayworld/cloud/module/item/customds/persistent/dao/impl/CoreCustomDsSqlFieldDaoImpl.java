/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsSqlFieldBean;
import com.sunwayworld.cloud.module.item.customds.persistent.dao.CoreCustomDsSqlFieldDao;
import com.sunwayworld.cloud.module.item.customds.persistent.mapper.CoreCustomDsSqlFieldMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCustomDsSqlFieldDaoImpl
extends MybatisDaoSupport<CoreCustomDsSqlFieldBean, Long>
implements CoreCustomDsSqlFieldDao {
    @Autowired
    private CoreCustomDsSqlFieldMapper coreCustomDsSqlFieldMapper;

    public CoreCustomDsSqlFieldMapper getMapper() {
        return this.coreCustomDsSqlFieldMapper;
    }
}
