/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.script.test.persistent.dao.impl;

import com.sunwayworld.cloud.module.script.test.bean.CoreItemBean;
import com.sunwayworld.cloud.module.script.test.persistent.dao.CoreItemDao;
import com.sunwayworld.cloud.module.script.test.persistent.mapper.CoreItemMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreItemDaoImpl
extends MybatisDaoSupport<CoreItemBean, Long>
implements CoreItemDao {
    @Autowired
    private CoreItemMapper coreItemMapper;

    public CoreItemMapper getMapper() {
        return this.coreItemMapper;
    }
}
