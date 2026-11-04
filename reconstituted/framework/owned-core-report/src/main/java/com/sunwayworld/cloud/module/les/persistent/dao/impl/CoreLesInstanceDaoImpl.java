/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.persistent.dao.impl;

import com.sunwayworld.cloud.module.les.bean.CoreLesInstanceBean;
import com.sunwayworld.cloud.module.les.persistent.dao.CoreLesInstanceDao;
import com.sunwayworld.cloud.module.les.persistent.mapper.CoreLesInstanceMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreLesInstanceDaoImpl
extends MybatisDaoSupport<CoreLesInstanceBean, Long>
implements CoreLesInstanceDao {
    @Autowired
    private CoreLesInstanceMapper mapper;

    public CoreLesInstanceMapper getMapper() {
        return this.mapper;
    }
}
