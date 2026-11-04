/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.persistent.dao.impl;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeToolbarLineBean;
import com.sunwayworld.cloud.module.office.persistent.dao.CoreOfficeToolbarLineDao;
import com.sunwayworld.cloud.module.office.persistent.mapper.CoreOfficeToolbarLineMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreOfficeToolbarLineDaoImpl
extends MybatisDaoSupport<CoreOfficeToolbarLineBean, Long>
implements CoreOfficeToolbarLineDao {
    @Autowired
    private CoreOfficeToolbarLineMapper coreOfficeToolbarLineMapper;

    public CoreOfficeToolbarLineMapper getMapper() {
        return this.coreOfficeToolbarLineMapper;
    }
}
