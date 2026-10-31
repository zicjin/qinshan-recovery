// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.bp.resource.impl;

import com.sunwayworld.emc.module.bp.bp.resource.EmcEquiptTempSetResource;
import com.sunwayworld.emc.module.bp.bp.service.EmcEquiptTempSetService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u6e29\u5ea6\u4f20\u611f\u5668\u6027\u80fd\u6838\u67e5")
@RestController
@GikamBean
public class EmcEquiptTempSetResourceImpl
implements EmcEquiptTempSetResource {
    @Autowired
    private EmcEquiptTempSetService emcEquiptTempSetService;

    @Override
    public EmcEquiptTempSetService getService() {
        return this.emcEquiptTempSetService;
    }

    @Override
    @Log(value="\u65b0\u589e\u6e29\u5ea6\u4f20\u611f\u5668\u6027\u80fd\u6838\u67e5", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }
}
