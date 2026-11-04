/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.constant.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.constant.bean.CoreConstantBean;
import com.sunwayworld.module.sys.constant.dao.CoreConstantDao;
import com.sunwayworld.module.sys.constant.service.CoreConstantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreConstantServiceImpl
implements CoreConstantService {
    @Autowired
    private CoreConstantDao constantDao;

    @Override
    public CoreConstantDao getDao() {
        return this.constantDao;
    }

    @Override
    public CoreConstantBean selectDetail(String id) {
        return (CoreConstantBean)this.getDao().selectByIdIfPresent(id);
    }
}
