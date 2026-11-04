/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.persistent.dao.impl;

import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean.CoreCodeGenerateConfigFieldBean;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.persistent.dao.CoreCodeGenerateConfigFieldDao;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.persistent.mapper.CoreCodeGenerateConfigFieldMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCodeGenerateConfigFieldDaoImpl
extends MybatisDaoSupport<CoreCodeGenerateConfigFieldBean, Long>
implements CoreCodeGenerateConfigFieldDao {
    @Autowired
    private CoreCodeGenerateConfigFieldMapper fieldMapper;

    public CoreCodeGenerateConfigFieldMapper getMapper() {
        return this.fieldMapper;
    }
}
