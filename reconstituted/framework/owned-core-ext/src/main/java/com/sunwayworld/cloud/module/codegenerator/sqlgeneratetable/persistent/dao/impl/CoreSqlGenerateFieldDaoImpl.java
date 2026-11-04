/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.persistent.dao.impl;

import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.bean.CoreSqlGenerateFieldBean;
import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.persistent.dao.CoreSqlGenerateFieldDao;
import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.persistent.mapper.CoreSqlGenerateFieldMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreSqlGenerateFieldDaoImpl
extends MybatisDaoSupport<CoreSqlGenerateFieldBean, Long>
implements CoreSqlGenerateFieldDao {
    @Autowired
    private CoreSqlGenerateFieldMapper sqlFieldMapper;

    public CoreSqlGenerateFieldMapper getMapper() {
        return this.sqlFieldMapper;
    }
}
