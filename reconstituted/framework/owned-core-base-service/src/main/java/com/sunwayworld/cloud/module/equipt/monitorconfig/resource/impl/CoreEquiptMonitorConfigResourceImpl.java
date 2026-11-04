/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.monitorconfig.resource.impl;

import com.sunwayworld.cloud.module.equipt.monitorconfig.bean.CoreEquiptMonitorConfigBean;
import com.sunwayworld.cloud.module.equipt.monitorconfig.bean.CoreEquiptMonitorLogBean;
import com.sunwayworld.cloud.module.equipt.monitorconfig.resource.CoreEquiptMonitorConfigResource;
import com.sunwayworld.cloud.module.equipt.monitorconfig.service.CoreEquiptMonitorConfigService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u4eea\u5668\u76d1\u6d4b\u914d\u7f6e")
@RestController
@GikamBean
public class CoreEquiptMonitorConfigResourceImpl
implements CoreEquiptMonitorConfigResource,
AbstractGenericResource<CoreEquiptMonitorConfigService, CoreEquiptMonitorConfigBean, Long> {
    @Autowired
    private CoreEquiptMonitorConfigService coreEquiptMonitorConfigService;

    @Override
    public CoreEquiptMonitorConfigService getService() {
        return this.coreEquiptMonitorConfigService;
    }

    @Override
    @Log(value="\u6839\u636e\u4eea\u5668\u76d1\u6d4b\u914d\u7f6e\u8868ID\u67e5\u8be2\u4eea\u5668\u76d1\u6d4b\u6570\u636eLOG\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/monitor-logs/queries"}, method={RequestMethod.POST})
    public Page<CoreEquiptMonitorLogBean> selectMonitorLogPaginationByEquiptMonitorConfigId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectMonitorLogPaginationByEquiptMonitorConfigId(id, wrapper);
    }
}
