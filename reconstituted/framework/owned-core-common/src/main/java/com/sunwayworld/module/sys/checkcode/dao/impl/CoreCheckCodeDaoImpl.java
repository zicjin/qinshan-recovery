/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.checkcode.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.checkcode.bean.CoreCheckCodeBean;
import com.sunwayworld.module.sys.checkcode.dao.CoreCheckCodeDao;
import com.sunwayworld.module.sys.checkcode.mapper.CoreCheckCodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCheckCodeDaoImpl
extends MybatisDaoSupport<CoreCheckCodeBean, Long>
implements CoreCheckCodeDao {
    @Autowired
    private CoreCheckCodeMapper coreCheckCodeMapper;

    public CoreCheckCodeMapper getMapper() {
        return this.coreCheckCodeMapper;
    }
}
