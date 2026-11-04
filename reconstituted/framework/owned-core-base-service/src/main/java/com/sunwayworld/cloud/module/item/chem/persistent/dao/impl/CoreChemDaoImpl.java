/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.chem.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.chem.bean.CoreChemBean;
import com.sunwayworld.cloud.module.item.chem.persistent.dao.CoreChemDao;
import com.sunwayworld.cloud.module.item.chem.persistent.mapper.CoreChemMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreChemDaoImpl
extends MybatisDaoSupport<CoreChemBean, Long>
implements CoreChemDao {
    @Autowired
    private CoreChemMapper chemMapper;

    public CoreChemMapper getMapper() {
        return this.chemMapper;
    }
}
