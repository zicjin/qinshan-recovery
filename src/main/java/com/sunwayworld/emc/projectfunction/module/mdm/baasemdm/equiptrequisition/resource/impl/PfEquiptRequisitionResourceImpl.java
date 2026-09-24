// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.projectfunction.module.mdm.baasemdm.equiptrequisition.resource.impl;

import com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.resource.impl.EmcEquiptRequisitionResourceImpl;
import com.sunwayworld.emc.projectfunction.module.mdm.baasemdm.equiptrequisition.service.PfEquiptRequisitionService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@GikamBean
public class PfEquiptRequisitionResourceImpl
extends EmcEquiptRequisitionResourceImpl {
    @Autowired
    private PfEquiptRequisitionService pfEquiptRequisitionService;

    @Log(value="\u5220\u9664", type=LogType.UPDATE)
    @RequestMapping(value={"/delete-instrument-out"}, method={RequestMethod.POST})
    public void deleteInstrumentOut(RestJsonWrapperBean wrapper) {
        this.pfEquiptRequisitionService.deleteInstrumentOut(wrapper);
    }
}
