/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.persistent.dao.impl;

import com.sunwayworld.cloud.module.les.bean.CoreLesDsFieldBean;
import com.sunwayworld.cloud.module.les.persistent.dao.CoreLesDsFieldDao;
import com.sunwayworld.cloud.module.les.persistent.mapper.CoreLesDsFieldMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreLesDsFieldDaoImpl
extends MybatisDaoSupport<CoreLesDsFieldBean, Long>
implements CoreLesDsFieldDao {
    @Autowired
    private CoreLesDsFieldMapper coreLesDsFieldMapper;

    public CoreLesDsFieldMapper getMapper() {
        return this.coreLesDsFieldMapper;
    }
}
