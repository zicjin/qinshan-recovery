// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.projectfunction.module.mdm.baasemdm.equiptrequisition.service;

import com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.service.EmcEquiptRequisitionService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

public interface PfEquiptRequisitionService
extends EmcEquiptRequisitionService {
    public void deleteInstrumentOut(RestJsonWrapperBean var1);
}
