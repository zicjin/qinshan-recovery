package com.sunwayworld.emc.module.mdm.org.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.module.mdm.org.controller.impl.CoreOrgControllerImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class EmcOrgControllerImpl
extends CoreOrgControllerImpl {
    @Log(value="\u7ec4\u7ec7\u7ed3\u6784\u8be6\u60c5\u9875\u9762", type=LogType.CONTROLLER)
    public String detailPage() {
        return "emc/module/mdm/orgs/emc-org-detail";
    }

    @Log(value="\u7ec4\u7ec7\u7ed3\u6784\u5217\u8868\u9875\u9762", type=LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/orgs/emc-org-edit-list";
    }

    @Log(value="\u7ec4\u7ec7\u7ed3\u6784\u9009\u62e9\u9875\u9762", type=LogType.CONTROLLER)
    public String choosePage() {
        return "emc/module/mdm/orgs/emc-org-choose-list";
    }

    @Log(value="\u7ec4\u7ec7\u7ed3\u6784\u9009\u62e9\u4e0b\u62c9", type=LogType.CONTROLLER)
    @RequestMapping(value={"/select-list"})
    public String selectPage() {
        return "emc/module/mdm/orgs/emc-org-select-list";
    }

    @Log(value="\u7cfb\u7edf\u5355\u4f4d\u9009\u62e9\u9875\u9762\uff08\u7cbe\u7b80\u9875\u9762\uff09\u8df3\u8f6c", type=LogType.CONTROLLER)
    public String simpleChoosePage() {
        return "emc/module/mdm/orgs/emc-org-simple-choose-list";
    }
}
