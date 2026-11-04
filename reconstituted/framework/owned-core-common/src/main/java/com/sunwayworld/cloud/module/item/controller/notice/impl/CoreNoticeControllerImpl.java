/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.controller.notice.impl;

import com.sunwayworld.cloud.module.item.controller.notice.CoreNoticeController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreNoticeControllerImpl
implements CoreNoticeController {
    @Override
    @Log(value="\u901a\u77e5\u516c\u544a\u7f16\u5236\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/item/notices/core-notice-edit-list";
    }

    @Override
    @Log(value="\u901a\u77e5\u516c\u544a\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/item/notices/core-notice-detail";
    }

    @Override
    @Log(value="\u901a\u77e5\u516c\u544a\u5ba1\u6838\u9875", type=LogType.CONTROLLER)
    public String auditListPage() {
        return "module/item/notices/core-notice-audit-list";
    }

    @Override
    @Log(value="\u901a\u77e5\u516c\u544a\u67e5\u8be2\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/item/notices/core-notice-search-list";
    }

    @Override
    @Log(value="\u901a\u77e5\u516c\u544a\u64a4\u9500\u9875", type=LogType.CONTROLLER)
    public String cancelListPage() {
        return "module/item/notices/core-notice-cancel-list";
    }
}
