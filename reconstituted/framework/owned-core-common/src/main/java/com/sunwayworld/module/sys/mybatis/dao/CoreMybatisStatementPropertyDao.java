/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.mybatis.dao;

import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.sys.mybatis.bean.CoreMybatisStatementPropertyBean;
import java.util.List;

public interface CoreMybatisStatementPropertyDao
extends GenericDao<CoreMybatisStatementPropertyBean, Long> {
    public List<CoreMybatisStatementPropertyBean> selectList(String var1, String var2);
}
