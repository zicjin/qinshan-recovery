/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customquery.presistent.dao.impl;

import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryFieldBean;
import com.sunwayworld.cloud.module.item.customquery.presistent.dao.CoreCustomQueryFieldDao;
import com.sunwayworld.cloud.module.item.customquery.presistent.mapper.CoreCustomQueryFieldMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCustomQueryFieldDaoImpl
extends MybatisDaoSupport<CoreCustomQueryFieldBean, Long>
implements CoreCustomQueryFieldDao {
    @Autowired
    private CoreCustomQueryFieldMapper coreCustomQueryFieldMapper;

    public CoreCustomQueryFieldMapper getMapper() {
        return this.coreCustomQueryFieldMapper;
    }
}
