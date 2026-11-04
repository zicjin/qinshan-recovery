/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.mybatis.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.mybatis.bean.CoreMybatisStatementPropertyBean;
import com.sunwayworld.module.sys.mybatis.dao.CoreMybatisStatementPropertyDao;
import com.sunwayworld.module.sys.mybatis.service.CoreMybatisStatementService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreMybatisStatementServiceImpl
implements CoreMybatisStatementService {
    @Autowired
    private CoreMybatisStatementPropertyDao mybatisStatementPropertyDao;

    @Override
    @Cacheable(value={"T_CORE_MYBATIS_STMT_PROPERTY.BY_NAMESPACE_STATEMENTID"}, key="#namespace + '.' + #statementId")
    public List<CoreMybatisStatementPropertyBean> selectMybatisStatementPropertyList(String namespace, String statementId) {
        return this.mybatisStatementPropertyDao.selectList(namespace, statementId);
    }
}
