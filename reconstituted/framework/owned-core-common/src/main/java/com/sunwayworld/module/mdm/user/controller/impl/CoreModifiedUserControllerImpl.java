/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.controller.CoreModifiedUserController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreModifiedUserControllerImpl
implements CoreModifiedUserController {
    @Override
    @Log(value="\u9884\u7ea6\u53d8\u66f4\u7528\u6237\u5217\u8868\u9875\u9762", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/mdm/m-users/core-m-user-edit-list";
    }

    @Override
    @Log(value="\u9884\u7ea6\u53d8\u66f4\u7528\u6237\u8be6\u60c5\u9875\u9762", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/mdm/m-users/core-m-user-detail";
    }
}
