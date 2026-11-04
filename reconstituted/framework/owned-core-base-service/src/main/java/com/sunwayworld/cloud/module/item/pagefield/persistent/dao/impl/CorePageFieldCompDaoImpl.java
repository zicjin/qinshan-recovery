/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.pagefield.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.pagefield.bean.CorePageFieldCompBean;
import com.sunwayworld.cloud.module.item.pagefield.persistent.dao.CorePageFieldCompDao;
import com.sunwayworld.cloud.module.item.pagefield.persistent.mapper.CorePageFieldCompMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CorePageFieldCompDaoImpl
extends MybatisDaoSupport<CorePageFieldCompBean, Long>
implements CorePageFieldCompDao {
    @Autowired
    private CorePageFieldCompMapper corePageFieldCompMapper;

    public CorePageFieldCompMapper getMapper() {
        return this.corePageFieldCompMapper;
    }
}
