/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.persistent.dao.impl;

import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.bean.CoreSqlGenerateTableBean;
import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.persistent.dao.CoreSqlGenerateTableDao;
import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.persistent.mapper.CoreSqlGenerateTableMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreSqlGenerateTableDaoImpl
extends MybatisDaoSupport<CoreSqlGenerateTableBean, Long>
implements CoreSqlGenerateTableDao {
    @Autowired
    private CoreSqlGenerateTableMapper sqlTableMapper;

    public CoreSqlGenerateTableMapper getMapper() {
        return this.sqlTableMapper;
    }
}
