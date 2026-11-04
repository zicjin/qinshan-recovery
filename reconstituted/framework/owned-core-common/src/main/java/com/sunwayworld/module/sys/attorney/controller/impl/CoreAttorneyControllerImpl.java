/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.attorney.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.attorney.controller.CoreAttorneyController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreAttorneyControllerImpl
implements CoreAttorneyController {
    @Override
    @Log(value="\u59d4\u6258\u4ee3\u7406\u7f16\u5236\u5217\u8868\u9875\u9762", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/sys/attorneys/core-attorney-edit-list";
    }

    @Override
    @Log(value="\u59d4\u6258\u4ee3\u7406\u67e5\u8be2\u5217\u8868\u9875\u9762", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/sys/attorneys/core-attorney-search-list";
    }
}
