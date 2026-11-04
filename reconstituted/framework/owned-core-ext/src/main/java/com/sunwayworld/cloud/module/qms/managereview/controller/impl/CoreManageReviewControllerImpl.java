/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.managereview.controller.impl;

import com.sunwayworld.cloud.module.qms.managereview.controller.CoreManageReviewController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u7ba1\u7406\u8bc4\u5ba1")
@Controller
@GikamBean
public class CoreManageReviewControllerImpl
implements CoreManageReviewController {
    @Override
    @Log(value="\u7ba1\u7406\u8bc4\u5ba1\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/qms/manage-reviews/core-manage-review-edit-list";
    }

    @Override
    @Log(value="\u7ba1\u7406\u8bc4\u5ba1\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/qms/manage-reviews/core-manage-review-detail";
    }

    @Override
    @Log(value="\u7ba1\u7406\u8bc4\u5ba1\u5ba1\u6838\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String auditListPage() {
        return "module/qms/manage-reviews/core-manage-review-audit-list";
    }

    @Override
    @Log(value="\u7ba1\u7406\u8bc4\u5ba1\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/qms/manage-reviews/core-manage-review-search-list";
    }
}
