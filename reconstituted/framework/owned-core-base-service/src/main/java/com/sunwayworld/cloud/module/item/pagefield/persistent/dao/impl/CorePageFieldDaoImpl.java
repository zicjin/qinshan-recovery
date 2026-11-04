/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.pagefield.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.pagefield.bean.CorePageFieldBean;
import com.sunwayworld.cloud.module.item.pagefield.persistent.dao.CorePageFieldDao;
import com.sunwayworld.cloud.module.item.pagefield.persistent.mapper.CorePageFieldMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CorePageFieldDaoImpl
extends MybatisDaoSupport<CorePageFieldBean, Long>
implements CorePageFieldDao {
    @Autowired
    private CorePageFieldMapper corePageFieldMapper;

    public CorePageFieldMapper getMapper() {
        return this.corePageFieldMapper;
    }
}
