/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.persistent.dao.impl;

import com.sunwayworld.cloud.module.les.bean.CoreLesInstanceUnitBean;
import com.sunwayworld.cloud.module.les.persistent.dao.CoreLesInstanceUnitDao;
import com.sunwayworld.cloud.module.les.persistent.mapper.CoreLesInstanceUnitMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreLesInstanceUnitDaoImpl
extends MybatisDaoSupport<CoreLesInstanceUnitBean, Long>
implements CoreLesInstanceUnitDao {
    @Autowired
    private CoreLesInstanceUnitMapper mapper;

    public CoreLesInstanceUnitMapper getMapper() {
        return this.mapper;
    }
}
