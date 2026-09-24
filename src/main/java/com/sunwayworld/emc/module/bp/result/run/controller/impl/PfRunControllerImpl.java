package com.sunwayworld.emc.module.bp.result.run.controller.impl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.result.run.controller.PfRunController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2021年05月28日
 */
@Controller
@GikamBean
public class PfRunControllerImpl extends EmcRunControllerImpl implements PfRunController {

    @Override
    @Log(value = "质控审核页面", type = LogType.CONTROLLER)
    public String qcAuditPage() {
        return "emc/module/bp/result/runs/emc-qc-audit-list";
    }

    @Override
    @Log(value = "任务指派页面", type = LogType.CONTROLLER)
    public String taskAssignPage() {
        return "emc/module/bp/result/runs/emc-task-assign-list";
    }

    @Override
    @Log(value = "样品称重页面", type = LogType.CONTROLLER)
    public String runWeighingPage() {
        return "emc/module/bp/result/runs/emc-run-weighing-list";
    }

    @Override
    @Log(value = "前处理页面", type = LogType.CONTROLLER)
    public String runPretreatPage() {
        return "emc/module/bp/result/runs/emc-run-pretreat-list";
    }

    @Override
    @Log(value = "结果录入页面", type = LogType.CONTROLLER)
    public String newResultInput() {
        return "emc/module/bp/result/runs/emc-run-weighting-detail";
    }

    @Override
    @Log(value = "复制称重页面", type = LogType.CONTROLLER)
    public String copyWeightListPage() {
        return "emc/module/bp/result/runs/emc-run-copy-weight-list";
    }

    @Override
    @Log(value = "样品制样页面", type = LogType.CONTROLLER)
    public String orderPreparation() {
        return "emc/module/bp/result/runs/emc-order-preparations-edit-list";
    }

    @Log(value = "样品批量修改页", type = LogType.CONTROLLER)
    @Override
    public String batchUpdateOrderPage() {
        return "emc/module/bp/result/runs/emc-run-order-preparations-batch-update";
    }

    @Override
    @Log(value = "分析仪器曲线页面", type = LogType.CONTROLLER)
    public String equiptCrves() {
        return "emc/module/bp/result/runs/emc-run-equiptcrves-edit-list";
    }

    @Override
    @Log(value = "分析仪器曲线详情页面", type = LogType.CONTROLLER)
    public String equiptCrvesDetail() {
        return "emc/module/bp/result/runs/emc-run-equiptcrves-detail-list";
    }

    @Override
    @Log(value = "历史分析仪器曲线页面", type = LogType.CONTROLLER)
    public String historyEquiptCrves() {
        return "emc/module/bp/result/runs/emc-run-history-equiptcrves-list";
    }

    @Override
    @Log(value = "历史分析仪器曲线详情页面", type = LogType.CONTROLLER)
    public String historyEquiptCrvesDetail() {
        return "emc/module/bp/result/runs/emc-run-equiptcrves-history-detail-list";
    }

    @Override
    @Log(value = "样品测试选择页面", type = LogType.CONTROLLER)
    public String orderTaskChoosePage() {
        return "emc/module/bp/result/runs/emc-run-order-task-choose-list";
    }

    @Override
    @Log(value = "QC关联样品页面", type = LogType.CONTROLLER)
    public String qcRelatedOrderPage() {
        return "emc/module/bp/result/runs/emc-run-qc-related-order-list";
    }

    @Override
    @Log(value = "修改样品测试仪器页面", type = LogType.CONTROLLER)
    public String editTaskEquiptPage() {
        return "emc/module/bp/result/runs/emc-run-edit-task-equipt-list";
    }

    @Override
    @Log(value = "文件采集选择仪器页面", type = LogType.CONTROLLER)
    public String chooseAcqEquipt() {
        return "emc/module/bp/result/runs/emc-run-choose-acq-equipt-list";
    }

    @Override
    @Log(value = "合并原始记录", type = LogType.CONTROLLER)
    public String runAnnexListPage() {
        return "emc/module/bp/result/runs/emc-run-report-annex-list";
    }

    @Override
    @Log(value = "项目温湿度页面", type = LogType.CONTROLLER)
    public String runTempHumidityPage() {
        return "emc/module/bp/result/runs/emc-run-temp-humidity-edit-list";
    }

    @Override
    @Log(value = "修改仪器选择样品页面", type = LogType.CONTROLLER)
    public String runTaskOrderPage() {
        return "emc/module/bp/result/runs/emc-run-edit-task-order-list";
    }

    @Override
    @Log(value = "测试批下样品选择页面", type = LogType.CONTROLLER)
    public String runOrderChoosePage() {
        return "emc/module/bp/result/runs/emc-run-order-choose-list";
    }

    @Override
    @Log(value = "测试批选择页面", type = LogType.CONTROLLER)
    public String choosePage() {
        return "emc/module/bp/result/runs/emc-run-choose-list";
    }

    @Override
    @Log(value = "测试批下温湿度样品选择页面", type = LogType.CONTROLLER)
    public String runTempOrderPage() {
        return "emc/module/bp/result/runs/emc-run-temp-order-list";
    }

    @Override
    @Log(value = "测试批下温湿度金属样品选择页面", type = LogType.CONTROLLER)
    public String runTempOrderForMetalPage() {
        return "emc/module/bp/result/runs/emc-run-temp-order-formetal-list";
    }

    @Override
    @Log(value = "分析数据修改项目温湿度页面", type = LogType.CONTROLLER)
    public String editRunTempHumidityPage() {
        return "emc/module/bp/edit/run-edits/emc-edit-run-temp-humidity-edit-list";
    }

    @Override
    @Log(value = "分析数据修改测试批下温湿度样品选择页面", type = LogType.CONTROLLER)
    public String runEditTempOrderPage() {
        return "emc/module/bp/edit/run-edits/emc-run-edit-temp-order-list";
    }

    @Override
    @Log(value = "分析录入-批量编辑分析项页面", type = LogType.CONTROLLER)
    public String batchEditItemPage() {
        return "emc/module/bp/result/runs/emc-run-batch-edit-item-list";
    }

    @Override
    @Log(value = "结果录入-标准物质测试页面", type = LogType.CONTROLLER)
    public String stdMatchTestPage() {
        return "emc/module/bp/result/runs/emc-run-std-match-test-edit-list";
    }

    @Override
    @Log(value = "分析录入-理化记录页面", type = LogType.CONTROLLER)
    public String physChemicPage() {
        return "emc/module/bp/result/runs/emc-run-phys-chemic-edit-list";
    }

    @Override
    @Log(value = "分析录入-进度展示页面", type = LogType.CONTROLLER)
    public String taskProcessShowPage() {
        return "emc/module/bp/result/runs/emc-run-task-process-show-edit-list";
    }

    @Override
    @Log(value = "分析录入-进度展示页面", type = LogType.CONTROLLER)
    public String taskProcessShowOrgPage() {
        return "emc/module/bp/result/runs/emc-task-process-show-org-edit-list";
    }

    @Override
    @Log(value = "滤筒信息批量新增页面", type = LogType.CONTROLLER)
    public String oilFilmBatchAddPage() {
        return "emc/module/bp/result/oil-films/emc-oil-film-batch-add";
    }

    @Override
    @Log(value = "项目分析未审核页面", type = LogType.CONTROLLER)
    public String unAuditPage() {
        return "emc/module/bp/result/runs/emc-project-run-unaudit";
    }

    @Override
    @Log(value = "样品分析项组选择页面", type = LogType.CONTROLLER)
    public String orderItemGroupChoosePage() {
        return "emc/module/bp/result/runs/emc-run-order-item-group-choose-list";
    }

    @Override
    @Log(value = "项目分析已审核页面", type = LogType.CONTROLLER)
    public String undoAuditedPage() {
        return "emc/module/bp/result/runs/emc-undo-run-audited";
    }

    @Override
    @Log(value = "分析环节-浏览原始记录页面", type = LogType.CONTROLLER)
    public String browseRecordPage() {
        return "emc/module/bp/result/runs/emc-browse-analysis-record-search-list";
    }

    @Override
    @Log(value = "查询采样单及附件", type = LogType.CONTROLLER)
    public String browseSampleRecordPage() { return "emc/module/bp/result/runs/emc-browse-sample-record-search-list"; }

    @Override
    @Log(value = "分析记录查询", type = LogType.CONTROLLER)
    public String runQuerySearchPage() {
        return "emc/module/bp/result/runs/emc-run-query-search-list";
    }

    @Override
    @RequestMapping("/preprocessing-result-input")
    @Log(value = "前处理分析项数据录入", type = LogType.CONTROLLER)
    public String preprocessingResultInputPage() { return "emc/module/bp/result/runs/emc-run-preprocessing-result-input-list"; }

}
