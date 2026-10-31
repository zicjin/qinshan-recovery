// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.mdm.oa.controller.impl;

import com.sunwayworld.emc.module.mdm.oa.controller.EmcOaNewClientController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class EmcOaNewClientControllerImpl
implements EmcOaNewClientController {
    @Override
    @Log(value="OA\u5ba2\u6237\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/oa-new-clients/emc-oa-new-client-edit-list";
    }
}
