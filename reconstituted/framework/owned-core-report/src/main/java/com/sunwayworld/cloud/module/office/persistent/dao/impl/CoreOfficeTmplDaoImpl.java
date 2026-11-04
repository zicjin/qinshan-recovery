/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.persistent.dao.impl;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeTmplBean;
import com.sunwayworld.cloud.module.office.persistent.dao.CoreOfficeTmplDao;
import com.sunwayworld.cloud.module.office.persistent.mapper.CoreOfficeTmplMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreOfficeTmplDaoImpl
extends MybatisDaoSupport<CoreOfficeTmplBean, Long>
implements CoreOfficeTmplDao {
    @Autowired
    private CoreOfficeTmplMapper coreOfficeTmplMapper;

    public CoreOfficeTmplMapper getMapper() {
        return this.coreOfficeTmplMapper;
    }
}
