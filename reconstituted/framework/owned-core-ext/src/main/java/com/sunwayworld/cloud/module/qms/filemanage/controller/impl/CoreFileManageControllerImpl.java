/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filemanage.controller.impl;

import com.sunwayworld.cloud.module.qms.filemanage.controller.CoreFileManageController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u6587\u4ef6\u7ba1\u7406")
@Controller
@GikamBean
public class CoreFileManageControllerImpl
implements CoreFileManageController {
    @Override
    @Log(value="\u6587\u4ef6\u7ba1\u7406\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/qms/file-manages/core-file-manage-edit-list";
    }

    @Override
    @Log(value="\u6587\u4ef6\u7ba1\u7406\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/qms/file-manages/core-file-manage-detail";
    }

    @Override
    @Log(value="\u6587\u4ef6\u7248\u672c\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String fileManagedetailPage() {
        return "module/qms/file-manages/core-file-manage-version-detail";
    }

    @Override
    @Log(value="\u6587\u4ef6\u7ba1\u7406\u5ba1\u6838\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String auditListPage() {
        return "module/qms/file-manages/core-file-manage-audit-list";
    }

    @Override
    @Log(value="\u6587\u4ef6\u7ba1\u7406\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/qms/file-manages/core-file-manage-search-list";
    }
}
