/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.persistent.dao.impl;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeInstanceBean;
import com.sunwayworld.cloud.module.office.persistent.dao.CoreOfficeInstanceDao;
import com.sunwayworld.cloud.module.office.persistent.mapper.CoreOfficeInstanceMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreOfficeInstanceDaoImpl
extends MybatisDaoSupport<CoreOfficeInstanceBean, Long>
implements CoreOfficeInstanceDao {
    @Autowired
    private CoreOfficeInstanceMapper coreOfficeInstanceMapper;

    public CoreOfficeInstanceMapper getMapper() {
        return this.coreOfficeInstanceMapper;
    }
}
