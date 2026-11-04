/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customquery.presistent.dao.impl;

import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomButtonBean;
import com.sunwayworld.cloud.module.item.customquery.presistent.dao.CoreCustomButtonDao;
import com.sunwayworld.cloud.module.item.customquery.presistent.mapper.CoreCustomButtonMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCustomButtonDaoImpl
extends MybatisDaoSupport<CoreCustomButtonBean, Long>
implements CoreCustomButtonDao {
    @Autowired
    private CoreCustomButtonMapper coreCustomButtonMapper;

    public CoreCustomButtonMapper getMapper() {
        return this.coreCustomButtonMapper;
    }
}
