// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.mdm.oa.controller.impl;

import com.sunwayworld.emc.module.mdm.oa.controller.EmcOaOrgRelationController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class EmcOaOrgRelationControllerImpl
implements EmcOaOrgRelationController {
    @Override
    @Log(value="OA\u90e8\u95e8\u5bf9\u7167\u8868\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/oa-org-relations/emc-oa-org-relation-edit-list";
    }
}
