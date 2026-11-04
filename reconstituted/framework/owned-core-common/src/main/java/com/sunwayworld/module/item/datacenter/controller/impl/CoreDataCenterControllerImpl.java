/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.datacenter.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.datacenter.controller.CoreDataCenterController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@GikamBean
public class CoreDataCenterControllerImpl
implements CoreDataCenterController {
    @Override
    @Log(value="\u6570\u636e\u4e2d\u5fc3\u5927\u5c4f\u914d\u7f6e\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/item/data-centers/core-data-center-edit-list";
    }

    @Override
    @Log(value="\u6570\u636e\u4e2d\u5fc3\u62a5\u8868\u9009\u62e9\u9875", type=LogType.CONTROLLER)
    @RequestMapping(value={"/report-choose-list"})
    public String reportChooseListPage() {
        return "module/item/data-centers/core-data-center-report-choose-list";
    }

    @Override
    @Log(value="\u6570\u636e\u4e2d\u5fc3\u5927\u5c4f\u67e5\u8be2\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/item/data-centers/core-data-center-search-list";
    }

    @Override
    @Log(value="\u4e2a\u4eba\u6570\u636e\u4e2d\u5fc3\u914d\u7f6e", type=LogType.CONTROLLER)
    @RequestMapping(value={"/user-edit-list"})
    public String userEditListPage() {
        return "module/item/data-centers/core-data-center-user-edit-list";
    }
}
