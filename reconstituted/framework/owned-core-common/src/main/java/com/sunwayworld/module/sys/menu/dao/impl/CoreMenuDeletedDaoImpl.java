/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.menu.bean.CoreMenuDeletedBean;
import com.sunwayworld.module.sys.menu.dao.CoreMenuDeletedDao;
import com.sunwayworld.module.sys.menu.mapper.CoreMenuDeletedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreMenuDeletedDaoImpl
extends MybatisDaoSupport<CoreMenuDeletedBean, Long>
implements CoreMenuDeletedDao {
    @Autowired
    private CoreMenuDeletedMapper menuDeletedMapper;

    public CoreMenuDeletedMapper getMapper() {
        return this.menuDeletedMapper;
    }
}
