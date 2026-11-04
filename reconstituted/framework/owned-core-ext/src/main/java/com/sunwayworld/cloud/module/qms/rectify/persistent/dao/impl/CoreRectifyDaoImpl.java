/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.rectify.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.rectify.bean.CoreRectifyBean;
import com.sunwayworld.cloud.module.qms.rectify.persistent.dao.CoreRectifyDao;
import com.sunwayworld.cloud.module.qms.rectify.persistent.mapper.CoreRectifyMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreRectifyDaoImpl
extends MybatisDaoSupport<CoreRectifyBean, Long>
implements CoreRectifyDao {
    @Autowired
    private CoreRectifyMapper coreRectifyMapper;

    public CoreRectifyMapper getMapper() {
        return this.coreRectifyMapper;
    }
}
