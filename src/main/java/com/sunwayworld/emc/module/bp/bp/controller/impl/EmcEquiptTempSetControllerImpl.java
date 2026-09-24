// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.bp.controller.impl;

import com.sunwayworld.emc.module.bp.bp.controller.EmcEquiptTempSetController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class EmcEquiptTempSetControllerImpl
implements EmcEquiptTempSetController {
    @Override
    @Log(value="\u6e29\u5ea6\u4f20\u611f\u5668\u6027\u80fd\u6838\u67e5\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/bp/equipt-temp-sets/emc-equipt-temp-set-edit-list";
    }

    @Override
    @Log(value="\u6e29\u5ea6\u4f20\u611f\u5668\u6027\u80fd\u6838\u67e5\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/bp/equipt-temp-sets/emc-equipt-temp-set-search-list";
    }

    @Override
    @Log(value="\u6e29\u5ea6\u4f20\u611f\u5668\u6027\u80fd\u6838\u67e5\u9009\u62e9\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "emc/module/bp/equipt-temp-sets/emc-equipt-temp-set-choose-list";
    }
}
