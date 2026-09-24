package com.sunwayworld.emc.module.bp.project.project.controller.impl;

import com.sunwayworld.emc.module.bp.project.project.controller.PfProjectController;
import com.sunwayworld.emc.module.bp.project.project.controller.impl.EmcProjectControllerImpl;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@GikamBean
public class PfProjectControllerImpl
extends EmcProjectControllerImpl
implements PfProjectController {
    public String shuttleChoosePage() {
        return "emc/module/bp/project/projects/emc-project-shuttle-choose-list";
    }

    public String reportFileChoosePage() {
        return "emc/module/sa/project-queries/emc-project-report-choose-list";
    }

    @Log(value="\u9879\u76ee\u8bb0\u5f55\u5355\u6587\u4ef6", type=LogType.CONTROLLER)
    public String projectAnnexListPage() {
        return "emc/module/sa/project-queries/emc-project-annex-list";
    }

    @Log(value="\u9879\u76ee\u9001\u6837\u8bb0\u5f55\u5355\u6587\u4ef6", type=LogType.CONTROLLER)
    public String deliverAnnexListPage() {
        return "emc/module/bp/deliveries/emc-deliver-annex-list";
    }

    public String projectLineChoosePage() {
        return "emc/module/sys/reports/emc-report-config-line-choose-list";
    }

    public String projOrderChoosePage() {
        return "emc/module/bp/project/projects/emc-proj-order-choose-list";
    }

    public String orderItemGroupChoosePage() {
        return "emc/module/bp/project/projects/emc-order-itemgroup-choose-list";
    }

    public String projOrderOrderChoosePage() {
        return "emc/module/bp/project/projects/emc-proj-order-order-choose-list";
    }

    public String orderItemGroupOrderChoosePage() {
        return "emc/module/bp/project/projects/emc-order-itemgroup-order-choose-list";
    }

    @Log(value="\u66f4\u65b0\u9001\u6837\u8bc4\u4ef7\u6307\u6807", type=LogType.CONTROLLER)
    public String updateOrderIndexPage() {
        return "emc/module/bp/project/projects/emc-update-order-index";
    }

    @RequestMapping(value={"/copy-to"})
    @Log(value="\u590d\u5236\u5230", type=LogType.CONTROLLER)
    public String copyToPage() {
        return "emc/module/bp/project/projects/emc-project-copy-to";
    }

    @Log(value="\u7ed8\u753b\u677f", type=LogType.CONTROLLER)
    @RequestMapping(value={"/draw-board"})
    public String drawBoardPage() {
        return "emc/module/bp/project/projects/emc-project-draw-board";
    }
}
