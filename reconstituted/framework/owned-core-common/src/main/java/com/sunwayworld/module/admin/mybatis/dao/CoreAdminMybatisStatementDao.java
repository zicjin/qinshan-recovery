/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.mybatis.dao;

import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.admin.mybatis.bean.CoreAdminMybatisStatementBean;
import java.util.List;

public interface CoreAdminMybatisStatementDao
extends GenericDao<CoreAdminMybatisStatementBean, Long> {
    public List<CoreAdminMybatisStatementBean> selectActiveList(String var1, String var2);
}
