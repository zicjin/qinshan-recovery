/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.mybatis.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.admin.mybatis.bean.CoreAdminMybatisStatementBean;
import java.util.List;
import org.springframework.lang.Nullable;

public interface CoreAdminMybatisStatementService
extends GenericService<CoreAdminMybatisStatementBean, Long> {
    public List<CoreAdminMybatisStatementBean> selectActiveList(@Nullable String var1, @Nullable String var2);
}
