/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.dms.persistent.dao.impl;

import com.sunwayworld.cloud.module.dms.bean.CoreDmsCategoryBean;
import com.sunwayworld.cloud.module.dms.persistent.dao.CoreDmsCategoryDao;
import com.sunwayworld.cloud.module.dms.persistent.mapper.CoreDmsCategoryMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreDmsCategoryDaoImpl
extends MybatisDaoSupport<CoreDmsCategoryBean, String>
implements CoreDmsCategoryDao {
    @Autowired
    private CoreDmsCategoryMapper dmsCategoryMapper;

    public CoreDmsCategoryMapper getMapper() {
        return this.dmsCategoryMapper;
    }
}
