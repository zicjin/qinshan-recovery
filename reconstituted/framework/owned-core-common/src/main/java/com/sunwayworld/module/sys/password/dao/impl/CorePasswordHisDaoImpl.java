/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.password.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.password.bean.CorePasswordHisBean;
import com.sunwayworld.module.sys.password.dao.CorePasswordHisDao;
import com.sunwayworld.module.sys.password.mapper.CorePasswordHisMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CorePasswordHisDaoImpl
extends MybatisDaoSupport<CorePasswordHisBean, Long>
implements CorePasswordHisDao {
    @Autowired
    private CorePasswordHisMapper hisMapper;

    public CorePasswordHisMapper getMapper() {
        return this.hisMapper;
    }
}
