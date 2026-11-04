/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.menu.dao.CoreMenuDeletedDao;
import com.sunwayworld.module.sys.menu.service.CoreMenuDeletedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreMenuDeletedServiceImpl
implements CoreMenuDeletedService {
    @Autowired
    private CoreMenuDeletedDao menuDeletedDao;

    @Override
    public CoreMenuDeletedDao getDao() {
        return this.menuDeletedDao;
    }
}
