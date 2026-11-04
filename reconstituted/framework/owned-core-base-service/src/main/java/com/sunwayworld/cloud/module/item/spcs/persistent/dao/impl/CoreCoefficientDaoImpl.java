/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.spcs.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.spcs.bean.CoreCoefficientBean;
import com.sunwayworld.cloud.module.item.spcs.persistent.dao.CoreCoefficientDao;
import com.sunwayworld.cloud.module.item.spcs.persistent.mapper.CoreCoefficientMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCoefficientDaoImpl
extends MybatisDaoSupport<CoreCoefficientBean, Long>
implements CoreCoefficientDao {
    @Autowired
    private CoreCoefficientMapper mapper;

    public CoreCoefficientMapper getMapper() {
        return this.mapper;
    }
}
