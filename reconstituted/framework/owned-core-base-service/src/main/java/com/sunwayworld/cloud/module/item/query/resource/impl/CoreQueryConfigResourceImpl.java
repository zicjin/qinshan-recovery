/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.query.resource.impl;

import com.sunwayworld.cloud.module.item.query.bean.CoreQueryConfigBean;
import com.sunwayworld.cloud.module.item.query.bean.CoreQueryConfigColumnBean;
import com.sunwayworld.cloud.module.item.query.resource.CoreQueryConfigResource;
import com.sunwayworld.cloud.module.item.query.service.CoreQueryConfigService;
import com.sunwayworld.cloud.module.item.support.validator.CoreQueryConfigSaveableValidator;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.activatable.resource.AbstractGenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogModule(value="\u901a\u7528\u67e5\u8be2\u914d\u7f6e")
@GikamBean
public class CoreQueryConfigResourceImpl
implements CoreQueryConfigResource,
AbstractGenericResource<CoreQueryConfigService, CoreQueryConfigBean, String>,
AbstractGenericActivatableResource<CoreQueryConfigService, CoreQueryConfigBean, String> {
    @Autowired
    private CoreQueryConfigService configService;

    @Override
    public CoreQueryConfigService getService() {
        return this.configService;
    }

    @Override
    @Log(value="\u65b0\u589e\u901a\u7528\u67e5\u8be2\u914d\u7f6e", type=LogType.INSERT)
    public String insert(RestJsonWrapperBean wrapper) {
        return this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u4fdd\u5b58\u901a\u7528\u67e5\u8be2\u914d\u7f6e", type=LogType.INSERT)
    @ValidateDataWith(value=CoreQueryConfigSaveableValidator.class)
    public void save(@PathVariable String id, RestJsonWrapperBean jsonWrapper) {
        AbstractGenericResource.super.save(id, jsonWrapper);
    }

    @Override
    @Log(value="\u901a\u8fc7\u914d\u7f6e\u67e5\u8be2\u5217\u8868\u4fe1\u606f", type=LogType.SELECT)
    public Pagination<CoreQueryConfigColumnBean> selectQueryConfigColumnPagination(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectQueryConfigColumnPagination(id, wrapper);
    }

    @Override
    @Log(value="\u901a\u8fc7\u914d\u7f6e\u67e5\u8be2\u6240\u6709\u5df2\u542f\u7528\u7684\u5217\u8868\u4fe1\u606f", type=LogType.SELECT)
    public ArrayList<CoreQueryConfigColumnBean> selectQueryConfigColumnList(@PathVariable String id) {
        return (ArrayList)this.getService().selectQueryConfigColumnList(id);
    }

    @Override
    @Log(value="\u6ce8\u9500\u5217\u8868\u4fe1\u606f", type=LogType.SUSPEND)
    public void suspendConfigColumn(@PathVariable String id, RestJsonWrapperBean wrapper) {
        this.getService().suspendConfigColumn(id, wrapper);
    }

    @Override
    @Log(value="\u542f\u7528\u5217\u8868\u4fe1\u606f", type=LogType.SUSPEND)
    public void unsuspendConfigColumn(@PathVariable String id, RestJsonWrapperBean wrapper) {
        this.getService().unsuspendConfigColumn(id, wrapper);
    }

    @Override
    @Log(value="\u901a\u7528\u67e5\u8be2\u914d\u7f6e\u76f8\u5173\u7684\u67e5\u8be2", type=LogType.SELECT)
    public Pagination<Map<String, Object>> selectRelatedPagination(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectRelatedPagination(id, wrapper);
    }
}
