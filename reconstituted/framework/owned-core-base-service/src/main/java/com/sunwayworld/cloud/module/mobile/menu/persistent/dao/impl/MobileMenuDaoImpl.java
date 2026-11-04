/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.menu.persistent.dao.impl;

import com.sunwayworld.cloud.module.mobile.menu.bean.MobileMenuBean;
import com.sunwayworld.cloud.module.mobile.menu.persistent.dao.MobileMenuDao;
import com.sunwayworld.cloud.module.mobile.menu.persistent.mapper.MobileMenuMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class MobileMenuDaoImpl
extends MybatisDaoSupport<MobileMenuBean, Long>
implements MobileMenuDao {
    @Autowired
    private MobileMenuMapper mobileFolderMapper;

    public MobileMenuMapper getMapper() {
        return this.mobileFolderMapper;
    }
}
