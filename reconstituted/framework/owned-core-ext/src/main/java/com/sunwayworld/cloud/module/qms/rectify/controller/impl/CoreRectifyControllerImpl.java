/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.rectify.controller.impl;

import com.sunwayworld.cloud.module.qms.rectify.controller.CoreRectifyController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u7ea0\u6b63\u63aa\u65bd")
@Controller
@GikamBean
public class CoreRectifyControllerImpl
implements CoreRectifyController {
    @Override
    @Log(value="\u7ea0\u6b63\u63aa\u65bd\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/qms/rectifys/core-rectify-edit-list";
    }

    @Override
    @Log(value="\u7ea0\u6b63\u63aa\u65bd\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/qms/rectifys/core-rectify-detail";
    }

    @Override
    @Log(value="\u7ea0\u6b63\u63aa\u65bd\u5ba1\u6838\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String auditListPage() {
        return "module/qms/rectifys/core-rectify-audit-list";
    }

    @Override
    @Log(value="\u7ea0\u6b63\u63aa\u65bd\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/qms/rectifys/core-rectify-search-list";
    }
}
