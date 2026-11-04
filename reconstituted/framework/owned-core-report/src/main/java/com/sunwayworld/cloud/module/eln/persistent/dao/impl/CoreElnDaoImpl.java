/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.persistent.dao.impl;

import com.sunwayworld.cloud.module.eln.bean.CoreElnBean;
import com.sunwayworld.cloud.module.eln.persistent.dao.CoreElnDao;
import com.sunwayworld.cloud.module.eln.persistent.mapper.CoreElnMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreElnDaoImpl
extends MybatisDaoSupport<CoreElnBean, Long>
implements CoreElnDao {
    @Autowired
    private CoreElnMapper coreElnMapper;

    public CoreElnMapper getMapper() {
        return this.coreElnMapper;
    }
}
