/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customquery.presistent.dao.impl;

import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryButtonBean;
import com.sunwayworld.cloud.module.item.customquery.presistent.dao.CoreCustomQueryButtonDao;
import com.sunwayworld.cloud.module.item.customquery.presistent.mapper.CoreCustomQueryButtonMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCustomQueryButtonDaoImpl
extends MybatisDaoSupport<CoreCustomQueryButtonBean, Long>
implements CoreCustomQueryButtonDao {
    @Autowired
    private CoreCustomQueryButtonMapper coreCustomQueryButtonMapper;

    public CoreCustomQueryButtonMapper getMapper() {
        return this.coreCustomQueryButtonMapper;
    }
}
