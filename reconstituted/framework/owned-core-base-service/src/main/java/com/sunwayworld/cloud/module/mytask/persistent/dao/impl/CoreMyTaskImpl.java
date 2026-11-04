/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mytask.persistent.dao.impl;

import com.sunwayworld.cloud.module.mytask.bean.CoreMyTaskBean;
import com.sunwayworld.cloud.module.mytask.persistent.dao.CoreMyTaskDao;
import com.sunwayworld.cloud.module.mytask.persistent.mapper.CoreMyTaskMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreMyTaskImpl
extends MybatisDaoSupport<CoreMyTaskBean, Long>
implements CoreMyTaskDao {
    @Autowired
    private CoreMyTaskMapper coreMyTaskMapper;

    public CoreMyTaskMapper getMapper() {
        return this.coreMyTaskMapper;
    }
}
