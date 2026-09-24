// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.mdm.oa.resource.impl;

import com.sunwayworld.emc.module.mdm.oa.resource.EmcOaOrgResource;
import com.sunwayworld.emc.module.mdm.oa.service.EmcOaOrgService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="OA\u90e8\u95e8")
@RestController
@GikamBean
public class EmcOaOrgResourceImpl
implements EmcOaOrgResource {
    @Autowired
    private EmcOaOrgService emcOaOrgService;

    @Override
    public EmcOaOrgService getService() {
        return this.emcOaOrgService;
    }

    @Override
    @Log(value="\u65b0\u589eOA\u90e8\u95e8", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Log(value="\u540c\u6b65OA", type=LogType.INSERT)
    @RequestMapping(value={"/sync"}, method={RequestMethod.POST})
    public Long syncOaClient(RestJsonWrapperBean wrapper) {
        this.getService().syncOaUserOrg();
        return 1L;
    }

    @Log(value="\u540c\u6b65OA", type=LogType.INSERT)
    @RequestMapping(value={"/sync-byname"}, method={RequestMethod.POST})
    public Long syncOaClientByName(RestJsonWrapperBean wrapper) {
        this.getService().syncOaClientAreaWithParam(wrapper);
        return 1L;
    }
}
