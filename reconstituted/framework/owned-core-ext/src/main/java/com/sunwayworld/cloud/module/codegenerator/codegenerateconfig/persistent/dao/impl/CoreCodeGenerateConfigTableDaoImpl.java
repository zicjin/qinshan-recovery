/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.persistent.dao.impl;

import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean.CoreCodeGenerateConfigTableBean;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.persistent.dao.CoreCodeGenerateConfigTableDao;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.persistent.mapper.CoreCodeGenerateConfigTableMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCodeGenerateConfigTableDaoImpl
extends MybatisDaoSupport<CoreCodeGenerateConfigTableBean, Long>
implements CoreCodeGenerateConfigTableDao {
    @Autowired
    private CoreCodeGenerateConfigTableMapper tableMapper;

    public CoreCodeGenerateConfigTableMapper getMapper() {
        return this.tableMapper;
    }
}
