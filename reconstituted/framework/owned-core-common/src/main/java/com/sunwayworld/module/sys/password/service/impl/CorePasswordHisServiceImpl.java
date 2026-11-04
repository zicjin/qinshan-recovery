/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.password.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.password.dao.CorePasswordHisDao;
import com.sunwayworld.module.sys.password.service.CorePasswordHisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CorePasswordHisServiceImpl
implements CorePasswordHisService {
    @Autowired
    private CorePasswordHisDao hisDao;

    @Override
    public CorePasswordHisDao getDao() {
        return this.hisDao;
    }
}
