/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.ncr.controller.impl;

import com.sunwayworld.cloud.module.qms.ncr.controller.CoreNcrController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u4e0d\u7b26\u5408\u767b\u8bb0")
@Controller
@GikamBean
public class CoreNcrControllerImpl
implements CoreNcrController {
    @Override
    @Log(value="\u6570\u636e\u8868\u4e0d\u7b26\u5408\u767b\u8bb0\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/qms/ncrs/core-ncr-edit-list";
    }

    @Override
    @Log(value="\u6570\u636e\u8868\u4e0d\u7b26\u5408\u767b\u8bb0\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/qms/ncrs/core-ncr-detail";
    }

    @Override
    @Log(value="\u6570\u636e\u8868\u4e0d\u7b26\u5408\u767b\u8bb0\u5ba1\u6838\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String auditListPage() {
        return "module/qms/ncrs/core-ncr-audit-list";
    }

    @Override
    @Log(value="\u6570\u636e\u8868\u4e0d\u7b26\u5408\u767b\u8bb0\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/qms/ncrs/core-ncr-search-list";
    }

    @Override
    @Log(value="\u6570\u636e\u8868\u4e0d\u7b26\u5408\u767b\u8bb0\u9009\u62e9\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/qms/ncrs/core-ncr-choose-list";
    }
}
