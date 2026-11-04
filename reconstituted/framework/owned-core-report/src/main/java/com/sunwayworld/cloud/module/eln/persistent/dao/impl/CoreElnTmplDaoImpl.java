/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.persistent.dao.impl;

import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplBean;
import com.sunwayworld.cloud.module.eln.persistent.dao.CoreElnTmplDao;
import com.sunwayworld.cloud.module.eln.persistent.mapper.CoreElnTmplMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreElnTmplDaoImpl
extends MybatisDaoSupport<CoreElnTmplBean, Long>
implements CoreElnTmplDao {
    @Autowired
    private CoreElnTmplMapper coreElnTmplMapper;

    public CoreElnTmplMapper getMapper() {
        return this.coreElnTmplMapper;
    }
}
