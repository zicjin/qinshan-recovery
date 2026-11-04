/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsFieldBean;
import com.sunwayworld.cloud.module.item.customds.persistent.dao.CoreCustomDsFieldDao;
import com.sunwayworld.cloud.module.item.customds.persistent.mapper.CoreCustomDsFieldMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCustomDsFieldDaoImpl
extends MybatisDaoSupport<CoreCustomDsFieldBean, Long>
implements CoreCustomDsFieldDao {
    @Autowired
    private CoreCustomDsFieldMapper coreCustomDsFieldMapper;

    public CoreCustomDsFieldMapper getMapper() {
        return this.coreCustomDsFieldMapper;
    }
}
