/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.fileacqconfig.controller;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/equipt/file-acq-configs/page"})
public interface CoreFileAcqConfigController
extends GenericEditListPageController,
GenericChoosePageController {
    @RequestMapping(value={"/test-acq-result-list"})
    public String testAcqResultListPage();
}
