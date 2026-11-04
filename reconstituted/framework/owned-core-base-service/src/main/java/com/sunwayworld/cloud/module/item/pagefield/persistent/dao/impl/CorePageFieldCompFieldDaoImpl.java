/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.pagefield.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.pagefield.bean.CorePageFieldCompFieldBean;
import com.sunwayworld.cloud.module.item.pagefield.persistent.dao.CorePageFieldCompFieldDao;
import com.sunwayworld.cloud.module.item.pagefield.persistent.mapper.CorePageFieldCompFieldMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CorePageFieldCompFieldDaoImpl
extends MybatisDaoSupport<CorePageFieldCompFieldBean, Long>
implements CorePageFieldCompFieldDao {
    @Autowired
    private CorePageFieldCompFieldMapper corePageFieldCompFieldMapper;

    public CorePageFieldCompFieldMapper getMapper() {
        return this.corePageFieldCompFieldMapper;
    }
}
