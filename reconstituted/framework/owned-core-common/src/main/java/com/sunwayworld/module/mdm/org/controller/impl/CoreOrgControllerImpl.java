/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.org.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.org.controller.CoreOrgController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreOrgControllerImpl
implements CoreOrgController {
    @Override
    @Log(value="\u7cfb\u7edf\u5355\u4f4d\u7f16\u5236\u5217\u8868\u9875\u9762", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/mdm/orgs/core-org-edit-list";
    }

    @Override
    @Log(value="\u7cfb\u7edf\u5355\u4f4d\u8be6\u60c5\u9875\u9762", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/mdm/orgs/core-org-detail";
    }

    @Override
    @Log(value="\u7cfb\u7edf\u5355\u4f4d\u9009\u62e9\u9875\u9762\u8df3\u8f6c", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/mdm/orgs/core-org-choose-list";
    }

    @Override
    @Log(value="\u7cfb\u7edf\u5355\u4f4d\u9009\u62e9\u9875\u9762\uff08\u7cbe\u7b80\u9875\u9762\uff09\u8df3\u8f6c", type=LogType.CONTROLLER)
    public String simpleChoosePage() {
        return "module/mdm/orgs/core-org-simple-choose-list";
    }
}
