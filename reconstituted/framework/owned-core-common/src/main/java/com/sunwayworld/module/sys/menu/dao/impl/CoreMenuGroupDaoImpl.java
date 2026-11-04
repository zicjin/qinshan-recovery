/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.menu.bean.CoreMenuGroupBean;
import com.sunwayworld.module.sys.menu.dao.CoreMenuGroupDao;
import com.sunwayworld.module.sys.menu.mapper.CoreMenuGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreMenuGroupDaoImpl
extends MybatisDaoSupport<CoreMenuGroupBean, String>
implements CoreMenuGroupDao {
    @Autowired
    private CoreMenuGroupMapper groupMapper;

    public CoreMenuGroupMapper getMapper() {
        return this.groupMapper;
    }
}
