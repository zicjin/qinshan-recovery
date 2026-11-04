/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.mybatis.resource.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.mybatis.resource.CoreAdminMybatisStatementResource;
import com.sunwayworld.module.admin.mybatis.service.CoreAdminMybatisStatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
public class CoreAdminMybatisStatementResourceImpl
implements CoreAdminMybatisStatementResource {
    @Autowired
    private CoreAdminMybatisStatementService mybatisStatementService;

    @Override
    public CoreAdminMybatisStatementService getService() {
        return this.mybatisStatementService;
    }
}
