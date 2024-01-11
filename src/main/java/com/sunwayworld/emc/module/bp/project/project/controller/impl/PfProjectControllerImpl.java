package com.sunwayworld.emc.module.bp.project.project.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.project.project.controller.PfProjectController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2021年11月24日
 */
@Controller
@GikamBean
public class PfProjectControllerImpl extends EmcProjectControllerImpl implements PfProjectController {

    @Override
    public String shuttleChoosePage() {
        return "emc/module/bp/project/projects/emc-project-shuttle-choose-list";
    }

    @Override
    public String reportFileChoosePage() {
        return "emc/module/sa/project-queries/emc-project-report-choose-list";
    }

    @Override
    @Log(value = "项目记录单文件", type = LogType.CONTROLLER)
    public String projectAnnexListPage() {
        return "emc/module/sa/project-queries/emc-project-annex-list";
    }

    @Log(value = "项目送样记录单文件", type = LogType.CONTROLLER)
    public String deliverAnnexListPage() {
        return "emc/module/bp/deliveries/emc-deliver-annex-list";
    }

    @Override
    public String projectLineChoosePage() {
        return "emc/module/sys/reports/emc-report-config-line-choose-list";
    }

    @Override
    public String projOrderChoosePage() {
        return "emc/module/bp/project/projects/emc-proj-order-choose-list";
    }

    @Override
    public String orderItemGroupChoosePage() {
        return "emc/module/bp/project/projects/emc-order-itemgroup-choose-list";
    }

    @Override
    public String projOrderOrderChoosePage() {
        return "emc/module/bp/project/projects/emc-proj-order-order-choose-list";
    }

    @Override
    public String orderItemGroupOrderChoosePage() {
        return "emc/module/bp/project/projects/emc-order-itemgroup-order-choose-list";
    }

    @Override
    @Log(value = "更新送样评价指标", type = LogType.CONTROLLER)
    public String updateOrderIndexPage() {
        return "emc/module/bp/project/projects/emc-update-order-index";
    }

}
