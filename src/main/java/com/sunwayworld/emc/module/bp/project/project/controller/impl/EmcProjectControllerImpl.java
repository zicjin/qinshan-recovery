package com.sunwayworld.emc.module.bp.project.project.controller.impl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.project.project.controller.EmcProjectController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-27
 */

@Controller
@GikamBean
public class EmcProjectControllerImpl implements EmcProjectController {

    @Log(value = "项目编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/bp/project/projects/emc-project-edit-list";
    }

    @Override
    @Log(value = "项目只读详情信息页", type = LogType.CONTROLLER)
    @RequestMapping("/project-search-detail")
    public String projDetailPage() {
        return "emc/module/bp/project/projects/emc-project-search-detail";
    }

    @Log(value = "样品添加分析项组页面", type = LogType.CONTROLLER)
    @Override
    public String orderAdditionPage() {
        return "emc/module/bp/project/projects/emc-project-order-addition";
    }

    @Log(value = "样品批量修改页", type = LogType.CONTROLLER)
    @Override
    public String batchUpdateOrderPage() {
        return "emc/module/bp/project/projects/emc-project-order-batch-update";
    }

    @Log(value = "样品测试容器组修改页", type = LogType.CONTROLLER)
    @Override
    public String updateOrderContainerGroupPage() {
        return "emc/module/bp/project/projects/emc-project-order-container-group-update";
    }

    @Log(value = "样品添加测试页面", type = LogType.CONTROLLER)
    @Override
    public String testAdditionPage() {
        return "emc/module/bp/project/projects/emc-project-test-addition";
    }

    @Log(value = "样品选择页面", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/bp/project/projects/emc-project-choose-list";
    }

    @Override
    public String schemeEditPage() {
        return "emc/module/bp/project/proj-schemes/emc-schemeedit-audit-list";
    }

    @Override
    public String schemeAuditPage() {
        return "emc/module/bp/project/proj-schemes/emc-schemeaudit-audit-list";
    }

    @Override
    @Log(value = "方案审核查询", type = LogType.CONTROLLER)
    public String schemeQueryPage() {
        return "emc/module/bp/project/proj-schemes/emc-schemeaudit-query-list";
    }

    @Log(value = "复制项目页面", type = LogType.CONTROLLER)
    @Override
    public String copyProjectPage() {
        return "emc/module/bp/project/projects/emc-project-copy";
    }

    @Override
    @Log(value = "现场踏勘页面", type = LogType.CONTROLLER)
    public String prospectPage() {
        return "emc/module/bp/project/project-prospects/emc-project-prospect-edit-list";
    }

}
