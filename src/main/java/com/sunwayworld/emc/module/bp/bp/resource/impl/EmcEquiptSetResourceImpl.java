// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.bp.resource.impl;

import com.sunwayworld.emc.module.bp.bp.resource.EmcEquiptSetResource;
import com.sunwayworld.emc.module.bp.bp.service.EmcEquiptSetService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u4eea\u5668\u6821\u51c6\u8bb0\u5f55")
@RestController
@GikamBean
public class EmcEquiptSetResourceImpl
implements EmcEquiptSetResource {
    @Autowired
    private EmcEquiptSetService emcEquiptSetService;

    @Override
    public EmcEquiptSetService getService() {
        return this.emcEquiptSetService;
    }

    @Override
    @Log(value="\u65b0\u589e\u4eea\u5668\u6821\u51c6\u8bb0\u5f55", type=LogType.INSERT)
    @RequestMapping(value={"/insert"}, method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }
}
