// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.mdm.oa.resource.impl;

import com.sunwayworld.emc.module.mdm.oa.resource.EmcOaEquiptResource;
import com.sunwayworld.emc.module.mdm.oa.service.EmcOaEquiptService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="OA\u8bbe\u5907")
@RestController
@GikamBean
public class EmcOaEquiptResourceImpl
implements EmcOaEquiptResource {
    @Autowired
    private EmcOaEquiptService emcOaEquiptService;

    @Override
    public EmcOaEquiptService getService() {
        return this.emcOaEquiptService;
    }

    @Override
    @Log(value="\u65b0\u589eOA\u8bbe\u5907", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }
}
