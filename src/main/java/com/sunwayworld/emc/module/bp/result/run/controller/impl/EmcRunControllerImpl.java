package com.sunwayworld.emc.module.bp.result.run.controller.impl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.result.run.controller.EmcRunController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcRunControllerImpl implements EmcRunController {

    @Override
    @Log(value = "分析录入页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/bp/result/runs/emc-run-edit-list";
    }

    @Override
    @Log(value = "筛选项目页面", type = LogType.CONTROLLER)
    public String projectPage() {
        return "emc/module/bp/result/runs/emc-run-project";
    }

    @Override
    @Log(value = "样品测试信息页面", type = LogType.CONTROLLER)
    public String runDetailPage() {
        return "emc/module/bp/result/runs/emc-run-detail";
    }

    @Override
    @Log(value = "测试批添加详情页面", type = LogType.CONTROLLER)
    public String runInputDetailPage() {
        return "emc/module/bp/result/runs/emc-run-input-detail";
    }

    @Override
    @Log(value = "分析校核页面", type = LogType.CONTROLLER)
    public String auditListPage() {
        return "emc/module/bp/result/runs/emc-run-audit-list";
    }

    @Override
    @Log(value = "分析审核页面", type = LogType.CONTROLLER)
    public String analysisAuditListPage() {
        return "emc/module/bp/result/runs/emc-analysis-audit-list";
    }

    @Override
    @Log(value = "相关性数据校验页面", type = LogType.CONTROLLER)
    @RequestMapping("/correlation-validations")
    public String correlationValidation() {
        return "emc/module/bp/result/runs/emc-analysis-audit-correlation-validation-list";
    }

    @Override
    @Log(value = "样品测试信息页面", type = LogType.CONTROLLER)
    public String analysisRunDetailPage() {
        return "emc/module/bp/result/runs/emc-analysis-run-detail";
    }

    @Override
    @Log(value = "添加批详情页面", type = LogType.CONTROLLER)
    public String addRunDetail() {
        return  "emc/module/bp/result/runs/emc-run-add-run-detail";
    }

    @Override
    @Log(value = "分析预审页面", type = LogType.CONTROLLER)
    public String analysisPretrialListPage() {
        return "emc/module/bp/result/runs/emc-analysis-pretrial-list";
    }

    @Override
    @Log(value = "编辑分析项", type = LogType.CONTROLLER)
    public String editItem() {
        return "emc/module/bp/result/runs/emc-run-edit-item-list";
    }

    @Override
    @Log(value = "查看方法附件", type = LogType.CONTROLLER)
    public String methodFiles() {
        return "emc/module/bp/result/runs/emc-run-method-files-list";
    }

    @Override
    @Log(value = "查看历史数据", type = LogType.CONTROLLER)
    public String historicalData() {
        return "emc/module/bp/result/runs/emc-run-historical-data-list";
    }

    @Override
    @Log(value = "结果录入页面", type = LogType.CONTROLLER)
    public String resultInput() {
        return "emc/module/bp/result/runs/emc-run-result-input-list";
    }

    @Override
    @Log(value = "方法编辑页面", type = LogType.CONTROLLER)
    public String methodEdit() {
        return "emc/module/bp/result/runs/emc-run-method-edit-list";
    }

    @Override
    @Log(value = "批录入时间", type = LogType.CONTROLLER)
    public String batchInput() {
        return "emc/module/bp/result/runs/emc-run-batch-input-list";
    }

    @Override
    @Log(value = "修改仪器", type = LogType.CONTROLLER)
    public String editEquipt() {
        return "emc/module/bp/result/runs/emc-run-edit-equipt-list";
    }

    @Override
    @Log(value = "复制实验室空白到其他批次", type = LogType.CONTROLLER)
    public String copyBlankToPage() {
        return "emc/module/bp/result/runs/emc-run-copy-blank-to-choose-list";
    }

    @Override
    @Log(value = "复制空白加标到其他批次", type = LogType.CONTROLLER)
    public String copyBlankSpkcToPage() {
        return "emc/module/bp/result/runs/emc-run-copy-blank-spkc-to-choose-list";
    }

}
