/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customquery.presistent.dao.impl;

import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryBean;
import com.sunwayworld.cloud.module.item.customquery.presistent.dao.CoreCustomQueryDao;
import com.sunwayworld.cloud.module.item.customquery.presistent.mapper.CoreCustomQueryMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCustomQueryDaoImpl
extends MybatisDaoSupport<CoreCustomQueryBean, Long>
implements CoreCustomQueryDao {
    @Autowired
    private CoreCustomQueryMapper coreCustomQueryMapper;

    public CoreCustomQueryMapper getMapper() {
        return this.coreCustomQueryMapper;
    }
}
