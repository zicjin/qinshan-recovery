/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.dailysupervision.controller.impl;

import com.sunwayworld.cloud.module.qms.dailysupervision.controller.CoreDailySupervisionController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u65e5\u5e38\u76d1\u7763")
@Controller
@GikamBean
public class CoreDailySupervisionControllerImpl
implements CoreDailySupervisionController {
    @Override
    @Log(value="\u65e5\u5e38\u76d1\u7763\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/qms/daily-supervisions/core-daily-supervision-edit-list";
    }

    @Override
    @Log(value="\u65e5\u5e38\u76d1\u7763\u5ba1\u6838\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String auditListPage() {
        return "module/qms/daily-supervisions/core-daily-supervision-audit-list";
    }

    @Override
    @Log(value="\u65e5\u5e38\u76d1\u7763\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/qms/daily-supervisions/core-daily-supervision-search-list";
    }
}
