/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.persistent.dao.impl;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeToolbarBean;
import com.sunwayworld.cloud.module.office.persistent.dao.CoreOfficeToolbarDao;
import com.sunwayworld.cloud.module.office.persistent.mapper.CoreOfficeToolbarMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreOfficeToolbarDaoImpl
extends MybatisDaoSupport<CoreOfficeToolbarBean, Long>
implements CoreOfficeToolbarDao {
    @Autowired
    private CoreOfficeToolbarMapper coreOfficeToolbarMapper;

    public CoreOfficeToolbarMapper getMapper() {
        return this.coreOfficeToolbarMapper;
    }
}
