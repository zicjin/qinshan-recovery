// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.bp.resource.impl;

import com.sunwayworld.emc.module.bp.bp.resource.EmcEquiptFlowSetResource;
import com.sunwayworld.emc.module.bp.bp.service.EmcEquiptFlowSetService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u5e9f\u6c14\u6d41\u91cf\u6821\u51c6")
@RestController
@GikamBean
public class EmcEquiptFlowSetResourceImpl
implements EmcEquiptFlowSetResource {
    @Autowired
    private EmcEquiptFlowSetService emcEquiptFlowSetService;

    @Override
    public EmcEquiptFlowSetService getService() {
        return this.emcEquiptFlowSetService;
    }

    @Override
    @Log(value="\u65b0\u589e\u5e9f\u6c14\u6d41\u91cf\u6821\u51c6", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }
}
