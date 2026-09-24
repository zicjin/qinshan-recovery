// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.bp.controller.impl;

import com.sunwayworld.emc.module.bp.bp.controller.EmcEquiptSwSetController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class EmcEquiptSwSetControllerImpl
implements EmcEquiptSwSetController {
    @Override
    @Log(value="\u6c34\u6e29\u4eea\u5668\u6821\u51c6\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/bp/equipt-sw-sets/emc-equipt-sw-set-edit-list";
    }

    @Override
    @Log(value="\u6c34\u6e29\u4eea\u5668\u6821\u51c6\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/bp/equipt-sw-sets/emc-equipt-sw-set-search-list";
    }

    @Override
    @Log(value="\u6c34\u6e29\u4eea\u5668\u6821\u51c6\u9009\u62e9\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "emc/module/bp/equipt-sw-sets/emc-equipt-sw-set-choose-list";
    }
}
