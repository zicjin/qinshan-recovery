/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.user.user.controller.impl;

import com.sunwayworld.cloud.module.user.user.controller.CoreUserPasswordController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@GikamBean
public class CoreUserPasswordControllerImpl
implements CoreUserPasswordController {
    @Override
    @Log(value="\u5bc6\u7801\u91cd\u7f6e-\u8d26\u6237\u767b\u8bb0\u9875\u9762", type=LogType.CONTROLLER)
    @RequestMapping(value={"/password-account"})
    public String passwordAccountPage() {
        return "module/sys/user-passwords/user-password-account";
    }

    @Override
    @Log(value="\u5bc6\u7801\u627e\u56de-\u5bc6\u7801\u66f4\u6539\u9875\u9762", type=LogType.CONTROLLER)
    @RequestMapping(value={"/password-reset"})
    public String passwordResetPage() {
        return "module/sys/user-passwords/user-password-reset";
    }

    @Override
    @Log(value="\u5bc6\u7801\u627e\u56de-\u627e\u56de\u5b8c\u6210\u9875\u9762", type=LogType.CONTROLLER)
    @RequestMapping(value={"/password-complete"})
    public String passwordCompletePage() {
        return "module/sys/user-passwords/user-password-reset-complete";
    }

    @Override
    @Log(value="\u5bc6\u7801\u627e\u56de-\u90ae\u4ef6\u53d1\u9001\u5b8c\u6210\u9875\u9762", type=LogType.CONTROLLER)
    @RequestMapping(value={"/send-complete"})
    public String sendCompletePage() {
        return "module/sys/user-passwords/user-password-send-complete";
    }

    @Override
    @Log(value="\u5bc6\u7801\u627e\u56de-\u94fe\u63a5\u5931\u6548\u9875\u9762", type=LogType.CONTROLLER)
    @RequestMapping(value={"/url-invalid"})
    public String urlInvalidPage() {
        return "module/sys/user-passwords/user-password-url-invalid";
    }

    @Override
    @Log(value="\u5bc6\u7801\u627e\u56de-\u5bc6\u7801\u66f4\u6539\u8df3\u8f6c\u7684\u4e2d\u95f4\u9875\u9762", type=LogType.CONTROLLER)
    @RequestMapping(value={"/password-toreset"})
    public String passwordToResetPage() {
        return "module/sys/user-passwords/user-password-toreset";
    }
}
