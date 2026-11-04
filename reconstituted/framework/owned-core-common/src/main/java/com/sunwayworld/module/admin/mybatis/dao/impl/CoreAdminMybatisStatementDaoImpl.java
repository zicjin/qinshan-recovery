/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.mybatis.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.module.admin.mybatis.bean.CoreAdminMybatisStatementBean;
import com.sunwayworld.module.admin.mybatis.dao.CoreAdminMybatisStatementDao;
import com.sunwayworld.module.admin.mybatis.mapper.CoreAdminMybatisStatementMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreAdminMybatisStatementDaoImpl
extends MybatisDaoSupport<CoreAdminMybatisStatementBean, Long>
implements CoreAdminMybatisStatementDao {
    @Autowired
    private CoreAdminMybatisStatementMapper statementMapper;

    public CoreAdminMybatisStatementMapper getMapper() {
        return this.statementMapper;
    }

    @Override
    public List<CoreAdminMybatisStatementBean> selectActiveList(String menuId, String mapperId) {
        return this.statementMapper.selectActiveList(menuId, mapperId).stream().map(m -> PersistableHelper.mapToPersistable(m, CoreAdminMybatisStatementBean.class)).collect(Collectors.toList());
    }
}
