package com.sunwayworld.emc.module.index.workspace.controller.impl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.index.workspace.controller.impl.CoreWorkspaceControllerImpl;

@Controller
@GikamBean
public class EmcWorkspaceControllerImpl extends CoreWorkspaceControllerImpl {
    @Override
    @Log(value = "工作空间页面", type = LogType.CONTROLLER)
    public String indexPage() {
        return "emc/module/index/workspace/index";
    }

    @Log(value = "待办页面", type = LogType.CONTROLLER)
    @RequestMapping("/todo")
    public String todoPage() {
        return "emc/module/index/workspace/emc-todo";
    }

    @Log(value = "分析科室统计", type = LogType.CONTROLLER)
    @RequestMapping("/analysis-department-statistics")
    public String analyzeDepartmentStatisticsPage() {
        return "emc/module/index/workspace/emc-analysis-department-statistics";
    }

    @Log(value = "人员工作量统计", type = LogType.CONTROLLER)
    @RequestMapping("/personnel-workload-statistics")
    public String personnelWorkloadStatisticsPage() {
        return "emc/module/index/workspace/emc-personnel-workload-statistics";
    }

    @Log(value = "报告数量统计页面", type = LogType.CONTROLLER)
    @RequestMapping("/project-statistics")
    public String projectStatisticsPage() {
        return "emc/module/index/workspace/emc-project-statistics";
    }

    @Log(value = "年度报告数量统计页面", type = LogType.CONTROLLER)
    @RequestMapping("/annual-project-statistics")
    public String annualProjectStatisticsPage() {
        return "emc/module/index/workspace/emc-annual-project-statistics";
    }

    @Log(value = "报表页面", type = LogType.CONTROLLER)
    @RequestMapping("/report")
    public String reportPage() {
        return "emc/module/index/workspace/emc-report";
    }

    @Log(value = "分析录入页面", type = LogType.CONTROLLER)
    @RequestMapping("/run-analysis")
    public String runAnalysisPage() {
        return "emc/module/index/workspace/emc-run-analysis";
    }

    @Log(value = "采样信息页面", type = LogType.CONTROLLER)
    @RequestMapping("/sample")
    public String samplePage() {
        return "emc/module/index/workspace/emc-sample";
    }

    @Log(value = "业务流程图页面", type = LogType.CONTROLLER)
    @RequestMapping("/business-process-diagram")
    public String businessProcessDiagramPage() {
        return "emc/module/index/workspace/emc-business-process-diagram";
    }

    @Log(value = "附件下载", type = LogType.CONTROLLER)
    @RequestMapping("/download-attachment")
    public String downloadAttachmentPage() {
        return "emc/module/index/workspace/emc-home-attachment-download-list";
    }
}
