/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.mybatis.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.mybatis.bean.CoreMybatisStatementPropertyBean;
import com.sunwayworld.module.sys.mybatis.dao.CoreMybatisStatementPropertyDao;
import com.sunwayworld.module.sys.mybatis.mapper.CoreMybatisStatementPropertyMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreMybatisStatementPropertyDaoImpl
extends MybatisDaoSupport<CoreMybatisStatementPropertyBean, Long>
implements CoreMybatisStatementPropertyDao {
    @Autowired
    private CoreMybatisStatementPropertyMapper mapper;

    @Override
    public List<CoreMybatisStatementPropertyBean> selectList(String namespace, String statementId) {
        return this.mapper.selectList(namespace, statementId);
    }
}
