/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.file.bean.CoreFileLogBean;
import com.sunwayworld.module.item.file.dao.CoreFileLogDao;
import com.sunwayworld.module.item.file.mapper.CoreFileLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreFileLogDaoImpl
extends MybatisDaoSupport<CoreFileLogBean, Long>
implements CoreFileLogDao {
    @Autowired
    private CoreFileLogMapper coreFileLogMapper;

    public CoreFileLogMapper getMapper() {
        return this.coreFileLogMapper;
    }
}
