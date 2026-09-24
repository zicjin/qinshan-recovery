// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.item.admarea.controller.impl;

import com.sunwayworld.emc.module.item.admarea.controller.EmcAdmAreaController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class EmcAdmAreaControllerImpl
implements EmcAdmAreaController {
    @Override
    @Log(value="\u7701\u5e02\u533a\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/item/adm-areas/emc-adm-area-edit-list";
    }

    @Override
    @Log(value="\u7701\u5e02\u533a\u9009\u62e9\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "emc/module/item/adm-areas/emc-adm-area-choose-list";
    }
}
