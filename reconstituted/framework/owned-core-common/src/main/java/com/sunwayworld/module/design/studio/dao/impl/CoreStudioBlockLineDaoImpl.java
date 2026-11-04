/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.design.studio.bean.CoreStudioBlockLineBean;
import com.sunwayworld.module.design.studio.dao.CoreStudioBlockLineDao;
import com.sunwayworld.module.design.studio.mapper.CoreStudioBlockLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreStudioBlockLineDaoImpl
extends MybatisDaoSupport<CoreStudioBlockLineBean, Long>
implements CoreStudioBlockLineDao {
    @Autowired
    private CoreStudioBlockLineMapper studioBlockLineMapper;

    public CoreStudioBlockLineMapper getMapper() {
        return this.studioBlockLineMapper;
    }
}
