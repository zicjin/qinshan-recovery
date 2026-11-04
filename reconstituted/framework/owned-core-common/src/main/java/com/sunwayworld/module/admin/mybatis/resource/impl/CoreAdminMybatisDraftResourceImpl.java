/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.mybatis.resource.impl;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.mybatis.bean.CoreAdminMybatisDraftBean;
import com.sunwayworld.module.admin.mybatis.resource.CoreAdminMybatisDraftResource;
import com.sunwayworld.module.admin.mybatis.service.CoreAdminMybatisDraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
public class CoreAdminMybatisDraftResourceImpl
implements CoreAdminMybatisDraftResource {
    @Autowired
    private CoreAdminMybatisDraftService mybatisDraftService;

    @Override
    public CoreAdminMybatisDraftService getService() {
        return this.mybatisDraftService;
    }

    @Override
    public CoreAdminMybatisDraftBean selectDraftForModification(@RequestParam String menuId, @RequestParam String mybatisStatementId) {
        return null;
    }

    @Override
    public Long insertOrUpdateDraft(RestJsonWrapperBean wrapper) {
        return this.getService().insertOrUpdateDraft(wrapper);
    }
}
