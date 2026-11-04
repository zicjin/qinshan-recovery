/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filevolume.controller.impl;

import com.sunwayworld.cloud.module.qms.filevolume.controller.CoreFileVolumeController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u6587\u4ef6\u518c")
@Controller
@GikamBean
public class CoreFileVolumeControllerImpl
implements CoreFileVolumeController {
    @Override
    @Log(value="\u6587\u4ef6\u518c\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/qms/file-volumes/core-file-volume-edit-list";
    }

    @Override
    @Log(value="\u6587\u4ef6\u518c\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/qms/file-volumes/core-file-volume-search-list";
    }

    @Override
    @Log(value="\u6587\u4ef6\u518c\u9009\u62e9\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/qms/file-volumes/core-file-volume-choose-list";
    }
}
