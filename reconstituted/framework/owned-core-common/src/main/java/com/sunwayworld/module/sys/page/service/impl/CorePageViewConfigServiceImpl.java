/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.page.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.page.bean.CorePageViewConfigBean;
import com.sunwayworld.module.sys.page.dao.CorePageViewConfigDao;
import com.sunwayworld.module.sys.page.service.CorePageViewConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CorePageViewConfigServiceImpl
implements CorePageViewConfigService {
    @Autowired
    private CorePageViewConfigDao configDao;

    @Override
    public CorePageViewConfigBean selectById(String id) {
        return (CorePageViewConfigBean)this.configDao.selectByIdIfPresent(id);
    }
}
