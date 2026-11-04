/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.menu.bean.CoreShortcutMenuLocationBean;
import com.sunwayworld.module.sys.menu.dao.CoreShortcutMenuLocationDao;
import com.sunwayworld.module.sys.menu.mapper.CoreShortcutMenuLocationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreShortcutMenuLocationDaoImpl
extends MybatisDaoSupport<CoreShortcutMenuLocationBean, String>
implements CoreShortcutMenuLocationDao {
    @Autowired
    private CoreShortcutMenuLocationMapper coreShortcutMenuLocationMapper;

    public CoreShortcutMenuLocationMapper getMapper() {
        return this.coreShortcutMenuLocationMapper;
    }
}
