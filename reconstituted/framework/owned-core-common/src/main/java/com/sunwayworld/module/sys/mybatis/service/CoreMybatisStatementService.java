/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.mybatis.service;

import com.sunwayworld.module.sys.mybatis.bean.CoreMybatisStatementPropertyBean;
import java.util.List;

public interface CoreMybatisStatementService {
    public List<CoreMybatisStatementPropertyBean> selectMybatisStatementPropertyList(String var1, String var2);
}
