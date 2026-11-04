/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.mybatis.resource;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.admin.mybatis.bean.CoreAdminMybatisStatementBean;
import com.sunwayworld.module.admin.mybatis.service.CoreAdminMybatisStatementService;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/admin/mybatis-statements"})
public interface CoreAdminMybatisStatementResource
extends GenericResource<CoreAdminMybatisStatementService, CoreAdminMybatisStatementBean, Long> {
}
