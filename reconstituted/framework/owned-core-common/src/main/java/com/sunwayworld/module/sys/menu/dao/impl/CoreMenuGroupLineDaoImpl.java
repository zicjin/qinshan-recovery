/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.menu.bean.CoreMenuGroupLineBean;
import com.sunwayworld.module.sys.menu.dao.CoreMenuGroupLineDao;
import com.sunwayworld.module.sys.menu.mapper.CoreMenuGroupLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreMenuGroupLineDaoImpl
extends MybatisDaoSupport<CoreMenuGroupLineBean, Long>
implements CoreMenuGroupLineDao {
    @Autowired
    private CoreMenuGroupLineMapper groupLineMapper;

    public CoreMenuGroupLineMapper getMapper() {
        return this.groupLineMapper;
    }
}
