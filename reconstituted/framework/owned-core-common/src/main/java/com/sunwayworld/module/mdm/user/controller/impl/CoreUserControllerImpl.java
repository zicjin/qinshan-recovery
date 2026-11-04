/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.controller.CoreUserController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@GikamBean
public class CoreUserControllerImpl
implements CoreUserController {
    @Override
    @Log(value="\u6838\u5fc3\u7cfb\u7edf\u7528\u6237\u5217\u8868\u9875\u9762", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/mdm/users/core-user-edit-list";
    }

    @Override
    @Log(value="\u6838\u5fc3\u7cfb\u7edf\u7528\u6237\u8be6\u60c5\u9875\u9762", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/mdm/users/core-user-detail";
    }

    @Override
    @Log(value="\u4fee\u6539\u5bc6\u7801\u9875\u9762\u8df3\u8f6c", type=LogType.CONTROLLER)
    @RequestMapping(value={"/changePassword"})
    public String changePassword() {
        return "module/mdm/users/core-user-change-password";
    }

    @Override
    @Log(value="\u7cfb\u7edf\u7528\u6237\u9009\u62e9\u9875\u9762\u8df3\u8f6c", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/mdm/users/core-user-choose-list";
    }

    @Override
    @Log(value="\u7cfb\u7edf\u7528\u6237\u9009\u62e9\u9875\u9762\uff08\u7cbe\u7b80\u9875\u9762\uff09\u8df3\u8f6c", type=LogType.CONTROLLER)
    public String simpleChoosePage() {
        return "module/mdm/users/core-user-simple-choose-list";
    }

    @Override
    @Log(value="\u7cfb\u7edf\u7528\u6237\u591a\u9009\u9875\u9762\u8df3\u8f6c", type=LogType.CONTROLLER)
    public String chooseMultPage() {
        return "module/mdm/users/core-user-choose-mult-list";
    }
}
