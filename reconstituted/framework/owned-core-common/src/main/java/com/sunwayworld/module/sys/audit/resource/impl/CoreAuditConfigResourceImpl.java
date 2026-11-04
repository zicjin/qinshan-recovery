/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.audit.bean.CoreAuditConfigColumnBean;
import com.sunwayworld.module.sys.audit.resource.CoreAuditConfigResource;
import com.sunwayworld.module.sys.audit.service.CoreAuditConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u65b0\u7248\u5ba1\u8ba1\u8ddf\u8e2a\u914d\u7f6e")
@GikamBean
@RestController
public class CoreAuditConfigResourceImpl
implements CoreAuditConfigResource {
    @Autowired
    private CoreAuditConfigService configService;

    @Override
    public CoreAuditConfigService getService() {
        return this.configService;
    }

    @Override
    @Log(value="\u521d\u59cb\u5316\u5ba1\u8ba1\u8ddf\u8e2a\u914d\u7f6e", type=LogType.INSERT)
    public void init() {
        this.getService().init();
    }

    @Override
    @Log(value="\u901a\u8fc7\u5ba1\u8ba1\u8ddf\u8e2a\u914d\u7f6e\u7f16\u7801\u67e5\u8be2\u5217\u914d\u7f6e", type=LogType.SELECT)
    public Page<CoreAuditConfigColumnBean> selectConfigColumnPaginationByConfigId(@PathVariable String id) {
        return this.getService().selectConfigColumnPaginationById(id);
    }

    @Override
    public void initConfigColumn(String id) {
        this.getService().initConfigColumn(id);
    }

    @Override
    @Log(value="\u542f\u7528\u5df2\u9009\u4e2d\u5217\u914d\u7f6e\u7684\u5ba1\u8ba1\u8ddf\u8e2a", type=LogType.ACTIVATE)
    public void enableConfigColumn(String id, RestJsonWrapperBean wrapper) {
        this.getService().enableConfigColumn(id, wrapper);
    }

    @Override
    @Log(value="\u505c\u7528\u5df2\u9009\u4e2d\u5217\u914d\u7f6e\u7684\u5ba1\u8ba1\u8ddf\u8e2a", type=LogType.SUSPEND)
    public void disableConfigColumn(String id, RestJsonWrapperBean wrapper) {
        this.getService().disableConfigColumn(id, wrapper);
    }
}
