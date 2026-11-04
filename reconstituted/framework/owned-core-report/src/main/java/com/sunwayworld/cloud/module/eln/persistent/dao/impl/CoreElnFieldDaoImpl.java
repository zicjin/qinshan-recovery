/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.persistent.dao.impl;

import com.sunwayworld.cloud.module.eln.bean.CoreElnFieldBean;
import com.sunwayworld.cloud.module.eln.persistent.dao.CoreElnFieldDao;
import com.sunwayworld.cloud.module.eln.persistent.mapper.CoreElnFieldMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreElnFieldDaoImpl
extends MybatisDaoSupport<CoreElnFieldBean, Long>
implements CoreElnFieldDao {
    @Autowired
    private CoreElnFieldMapper coreElnFieldMapper;

    public CoreElnFieldMapper getMapper() {
        return this.coreElnFieldMapper;
    }
}
