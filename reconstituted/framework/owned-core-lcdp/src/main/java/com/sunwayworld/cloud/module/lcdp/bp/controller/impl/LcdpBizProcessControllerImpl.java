/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.controller.impl;

import com.sunwayworld.cloud.module.lcdp.bp.controller.LcdpBizProcessController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class LcdpBizProcessControllerImpl
implements LcdpBizProcessController {
    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/lcdp/bps/lcdp-biz-process-edit-list";
    }

    @Override
    @Log(value="\u4e1a\u52a1\u5de5\u4f5c\u6d41\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/lcdp/bps/lcdp-biz-process-detail";
    }

    @Override
    @Log(value="\u4e1a\u52a1\u6d41\u7a0b\u72b6\u6001\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String processStatusDetailPage() {
        return "module/lcdp/bps/lcdp-biz-process-status-detail";
    }

    @Override
    @Log(value="\u4e1a\u52a1\u5de5\u4f5c\u6d41\u6d41\u7a0b\u56fe\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String lcdpDiagramViewerPage() {
        return "module/lcdp/bps/diagram-viewer";
    }

    @Override
    @Log(value="\u4e1a\u52a1\u5de5\u4f5c\u6d41\u811a\u672c\u7f16\u8f91\u9875", type=LogType.CONTROLLER)
    public String scriptEditPage() {
        return "module/lcdp/bps/lcdp-biz-process-script-edit";
    }
}
