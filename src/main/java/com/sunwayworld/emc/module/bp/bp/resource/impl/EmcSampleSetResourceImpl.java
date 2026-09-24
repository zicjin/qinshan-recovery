// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.bp.resource.impl;

import com.sunwayworld.emc.module.bp.bp.resource.EmcSampleSetResource;
import com.sunwayworld.emc.module.bp.bp.service.EmcSampleSetService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u91c7\u6837\u57fa\u51c6\u542b\u6c27\u914d\u7f6e")
@RestController
@GikamBean
public class EmcSampleSetResourceImpl
implements EmcSampleSetResource {
    @Autowired
    private EmcSampleSetService emcSampleSetService;

    @Override
    public EmcSampleSetService getService() {
        return this.emcSampleSetService;
    }

    @Override
    @Log(value="\u65b0\u589e\u91c7\u6837\u57fa\u51c6\u542b\u6c27\u914d\u7f6e", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }
}
