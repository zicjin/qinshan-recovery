/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customquery.presistent.dao.impl;

import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryConditionBean;
import com.sunwayworld.cloud.module.item.customquery.presistent.dao.CoreCustomQueryConditionDao;
import com.sunwayworld.cloud.module.item.customquery.presistent.mapper.CoreCustomQueryConditionMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCustomQueryConditionDaoImpl
extends MybatisDaoSupport<CoreCustomQueryConditionBean, Long>
implements CoreCustomQueryConditionDao {
    @Autowired
    private CoreCustomQueryConditionMapper coreCustomQueryConditionMapper;

    public CoreCustomQueryConditionMapper getMapper() {
        return this.coreCustomQueryConditionMapper;
    }
}
