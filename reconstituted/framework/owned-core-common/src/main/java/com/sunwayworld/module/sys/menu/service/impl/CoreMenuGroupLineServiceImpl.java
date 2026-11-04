/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.menu.dao.CoreMenuGroupLineDao;
import com.sunwayworld.module.sys.menu.service.CoreMenuGroupLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreMenuGroupLineServiceImpl
implements CoreMenuGroupLineService {
    @Autowired
    private CoreMenuGroupLineDao groupLineDao;

    @Override
    public CoreMenuGroupLineDao getDao() {
        return this.groupLineDao;
    }
}
