/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mytask.resource.impl;

import com.sunwayworld.cloud.module.mytask.bean.CoreMyTaskBean;
import com.sunwayworld.cloud.module.mytask.resource.CoreMyTaskResource;
import com.sunwayworld.cloud.module.mytask.service.CoreMyTaskService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u65e5\u7a0b\u7ba1\u7406")
@RestController
@GikamBean
@RequestMapping(value={"/secure/core/module/my-tasks"})
public class CoreMyTaskResourceImpl
implements CoreMyTaskResource,
AbstractGenericResource<CoreMyTaskService, CoreMyTaskBean, Long> {
    @Autowired
    private CoreMyTaskService coreMyTaskService;

    @Override
    public CoreMyTaskService getService() {
        return this.coreMyTaskService;
    }

    @Override
    @Log(value="\u65b0\u589e\u65e5\u7a0b", type=LogType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        return this.getService().insert(wrapper);
    }
}
