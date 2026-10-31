// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.item.admarea.resource.impl;

import com.sunwayworld.emc.module.item.admarea.resource.EmcAdmAreaResource;
import com.sunwayworld.emc.module.item.admarea.service.EmcAdmAreaService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u7701\u5e02\u533a")
@RestController
@GikamBean
public class EmcAdmAreaResourceImpl
implements EmcAdmAreaResource {
    @Autowired
    private EmcAdmAreaService emcAdmAreaService;

    @Override
    public EmcAdmAreaService getService() {
        return this.emcAdmAreaService;
    }

    @Override
    @Log(value="\u65b0\u589e\u7701\u5e02\u533a", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public String insert(RestJsonWrapperBean wrapper) {
        return (String)this.getService().insert(wrapper);
    }
}
