/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filetype.controller.impl;

import com.sunwayworld.cloud.module.qms.filetype.controller.CoreFileTypeController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u6587\u4ef6\u7c7b\u578b")
@Controller
@GikamBean
public class CoreFileTypeControllerImpl
implements CoreFileTypeController {
    @Override
    @Log(value="\u6587\u4ef6\u7c7b\u578b\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/qms/file-types/core-file-type-edit-list";
    }

    @Override
    @Log(value="\u6587\u4ef6\u7c7b\u578b\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/qms/file-types/core-file-type-search-list";
    }

    @Override
    @Log(value="\u6587\u4ef6\u7c7b\u578b\u9009\u62e9\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/qms/file-types/core-file-type-choose-list";
    }
}
