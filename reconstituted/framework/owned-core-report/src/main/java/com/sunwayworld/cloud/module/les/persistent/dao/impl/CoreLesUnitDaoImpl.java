/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.persistent.dao.impl;

import com.sunwayworld.cloud.module.les.bean.CoreLesUnitBean;
import com.sunwayworld.cloud.module.les.persistent.dao.CoreLesUnitDao;
import com.sunwayworld.cloud.module.les.persistent.mapper.CoreLesUnitMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreLesUnitDaoImpl
extends MybatisDaoSupport<CoreLesUnitBean, String>
implements CoreLesUnitDao {
    @Autowired
    private CoreLesUnitMapper coreLesUnitMapper;

    public CoreLesUnitMapper getMapper() {
        return this.coreLesUnitMapper;
    }
}
