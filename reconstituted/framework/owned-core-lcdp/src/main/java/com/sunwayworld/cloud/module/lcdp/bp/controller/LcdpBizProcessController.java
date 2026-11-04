/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.controller;

import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/lcdp/bps/page"})
public interface LcdpBizProcessController
extends GenericEditListPageController,
GenericDetailPageController {
    @RequestMapping(value={"/process-status-detail"})
    public String processStatusDetailPage();

    @RequestMapping(value={"/diagram-viewer"})
    public String lcdpDiagramViewerPage();

    @RequestMapping(value={"/script-edit"})
    public String scriptEditPage();
}
