/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.mybatis.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.mybatis.bean.CoreAdminMybatisStatementBean;
import com.sunwayworld.module.admin.mybatis.dao.CoreAdminMybatisStatementDao;
import com.sunwayworld.module.admin.mybatis.service.CoreAdminMybatisStatementService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreAdminMybatisStatementServiceImpl
implements CoreAdminMybatisStatementService {
    @Autowired
    private CoreAdminMybatisStatementDao mybatisStatementDao;

    @Override
    public CoreAdminMybatisStatementDao getDao() {
        return this.mybatisStatementDao;
    }

    @Override
    public List<CoreAdminMybatisStatementBean> selectActiveList(String menuId, String mapperId) {
        return this.getDao().selectActiveList(menuId, mapperId);
    }
}
