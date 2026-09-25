package com.sunwayworld.emc.module.bp.result.run.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.result.run.bean.EmcAnalysisRecordDTO;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcQcRelatedOrderBean;
import com.sunwayworld.emc.module.bp.result.run.resource.PfRunResource;
import com.sunwayworld.emc.module.bp.result.run.service.PfRunService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2021年05月28日
 */
@LogModule("样品测试批")
@RestController
@GikamBean
public class PfRunResourceImpl extends EmcRunResourceImpl implements PfRunResource {

    @Autowired
    private PfRunService runService;

    @Override
    public PfRunService getService() {
        return runService;
    }

    @Override
    @Log(value = "质控审核提交", type = LogType.UPDATE)
    @RequestMapping(value = "/action/qc-audit-submit", method = RequestMethod.PUT)
    public void qcAuditSubmit(RestJsonWrapperBean wrapper) {
        runService.qcAuditSubmit(wrapper);
    }

    @Override
    @Log(value = "质控审核-批退回", type = LogType.UPDATE)
    @RequestMapping(value = "/action/qc-audit-run-undo", method = RequestMethod.PUT)
    public void qcAuditRunUndo(RestJsonWrapperBean wrapper) {
        runService.qcAuditRunUndo(wrapper);
    }

    @Override
    @Log(value = "完成称重", type = LogType.UPDATE)
    @RequestMapping(value = "/action/complete-weighing", method = RequestMethod.POST)
    public void completeWeighing(RestJsonWrapperBean wrapper) {
        runService.completeWeighing(wrapper);
    }

    @Override
    @Log(value = "完成前处理", type = LogType.UPDATE)
    @RequestMapping(value = "/action/complete-pretreat", method = RequestMethod.POST)
    public void completePretreat(RestJsonWrapperBean wrapper) {
        runService.completePretreat(wrapper);
    }

    @Override
    @Log(value = "分析录入-批录入检出项", type = LogType.UPDATE)
    @RequestMapping(value = "/action/batch-input-result-rule", method = RequestMethod.POST)
    public void batchInputResultRule(RestJsonWrapperBean wrapper) {
        runService.batchInputResultRule(wrapper);
    }

    @Override
    @Log(value = "分析录入-提交校验结果是否超标", type = LogType.UPDATE)
    @RequestMapping(value = "/action/validate-result-status", method = RequestMethod.POST)
    public RestValidationResultBean validateResultStatus(RestJsonWrapperBean wrapper) {
        return runService.validateResultStatus(wrapper);
    }

    @Override
    @Log(value = "复制称重", type = LogType.UPDATE)
    @RequestMapping(value = "/action/copy-weight", method = RequestMethod.POST)
    public void copyWeight(RestJsonWrapperBean wrapper) {
        runService.copyWeight(wrapper);
    }


    @Override
    @Log(value = "质控审核-提交校验结果是否超标", type = LogType.UPDATE)
    @RequestMapping(value = "/action/validate-project-result-status", method = RequestMethod.POST)
    public RestValidationResultBean validateProjectResultStatus(RestJsonWrapperBean wrapper) {
        return runService.validateProjectResultStatus(wrapper);
    }

    @Override
    @Log(value = "关联称重批次", type = LogType.UPDATE)
    @RequestMapping(value = "/action/link-weight-run", method = RequestMethod.POST)
    public void linkWeightingRun(RestJsonWrapperBean wrapper) {
        runService.linkWeightingRun(wrapper);
    }

    @Override
    @Log(value = "解除关联称重批次", type = LogType.UPDATE)
    @RequestMapping(value = "/action/unlink-weight-run", method = RequestMethod.POST)
    public void unlinkWeightingRun(RestJsonWrapperBean wrapper) {
        runService.unlinkWeightingRun(wrapper);
    }

    @Override
    @Log(value = "关联分析批次", type = LogType.UPDATE)
    @RequestMapping(value = "/action/link-run", method = RequestMethod.POST)
    public void linkRun(RestJsonWrapperBean wrapper) {
        runService.linkRun(wrapper);
    }

    @Override
    @Log(value = "解除关联分析批次", type = LogType.UPDATE)
    @RequestMapping(value = "/action/unlink-run", method = RequestMethod.POST)
    public void unlinkRun(RestJsonWrapperBean wrapper) {
        runService.unlinkRun(wrapper);
    }

    @Override
    @Log(value = "合并样品测试", type = LogType.UPDATE)
    @RequestMapping(value = "/action/combine-order-task", method = RequestMethod.POST)
    public void combineOrderTask(RestJsonWrapperBean wrapper) {
        runService.combineOrderTask(wrapper);
    }

    @Override
    @Log(value = "查询样品测试", type = LogType.SELECT)
    @RequestMapping(value = "/unique-order-task/queries", method = RequestMethod.POST)
    public Page<EmcOrderTaskBean> selectUniqueOrderTask(RestJsonWrapperBean wrapper) {
        return runService.selectUniqueOrderTask(wrapper);
    }

    @Override
    @Log(value = "删除QC关联样品", type = LogType.DELETE)
    @RequestMapping(value = "/qc-related-order", method = RequestMethod.DELETE)
    public Long deleteQcRelatedOrder(RestJsonWrapperBean wrapper) {
        return runService.deleteQcRelatedOrder(wrapper);
    }

    @Override
    @Log(value = "查询QC关联样品", type = LogType.SELECT)
    @RequestMapping(value = "/qc-related-order/queries", method = RequestMethod.POST)
    public Page<EmcQcRelatedOrderBean> selectQcRelatedOrder(RestJsonWrapperBean wrapper) {
        return runService.selectQcRelatedOrder(wrapper);
    }

    @Override
    @Log(value = "QC关联样品", type = LogType.INSERT)
    @RequestMapping(value = "/action/link-order", method = RequestMethod.POST)
    public void linkOrder(RestJsonWrapperBean wrapper) {
        runService.linkOrder(wrapper);
    }

    @Override
    @Log(value = "QC解除关联样品", type = LogType.INSERT)
    @RequestMapping(value = "/action/unlink-order", method = RequestMethod.POST)
    public void unlinkOrder(RestJsonWrapperBean wrapper) {
        runService.unlinkOrder(wrapper);
    }


    @Override
    @Log(value = "分析录入-文件采集时选择仪器", type = LogType.SELECT)
    @RequestMapping(value = "/acqEquipt/{runId}", method = RequestMethod.POST)
    public Page<EmcEquiptBean> selectAcqEquiptPagination(@PathVariable String runId) {
        return runService.selectAcqEquiptPagination(runId);
    }

    @Override
    @Log(value = "合并项目报告", type = LogType.INSERT)
    @RequestMapping(value = "/action/compose-all-pdf", method = RequestMethod.POST)
    public void composeAllPdf(RestJsonWrapperBean wrapper) {
        runService.composeAllPdf(wrapper);
    }

    @Override
    @Log(value = "合并项目报告", type = LogType.INSERT)
    @RequestMapping(value = "/action/compose-all-runlist-pdf", method = RequestMethod.POST)
    public void composeAllRunListReportPdf(RestJsonWrapperBean wrapper) {
        runService.composeAllRunListReportPdf(wrapper);
    }

    @Override
    @Log(value = "多质控批录入", type = LogType.UPDATE)
    @RequestMapping(value = "/action/batch-input-qc", method = RequestMethod.POST)
    public void batchUpdateQcResult(RestJsonWrapperBean wrapper) {
        runService.batchUpdateQcResult(wrapper);
    }

    @Override
    @Log(value = "生成温湿度数据", type = LogType.UPDATE)
    @RequestMapping(value = "/action/create-temp-humidity", method = RequestMethod.POST)
    public void createTempHumidity(RestJsonWrapperBean wrapper) {
        runService.createTempHumidity(wrapper);
    }

    @Override
    @Log(value = "提交项目下批次", type = LogType.UPDATE)
    @RequestMapping(value = "/action/submit-project-run", method = RequestMethod.POST)
    public void submitProjectRun(RestJsonWrapperBean wrapper) {
        runService.submitProjectRun(wrapper);
    }

    @Override
    @Log(value = "复制质控到", type = LogType.INSERT)
    @RequestMapping(value = "/action/copy-qc-order-to", method = RequestMethod.POST)
    public void copyQcOrderTo(RestJsonWrapperBean wrapper) {
        runService.copyQcOrderTo(wrapper);
    }

    @Override
    @Log(value = "复制替换质控", type = LogType.INSERT)
    @RequestMapping(value = "/action/copy-replace-qc-order", method = RequestMethod.POST)
    public void copyReplaceQcOrder(RestJsonWrapperBean wrapper) {
        runService.copyReplaceQcOrder(wrapper);
    }

    @Override
    @Log(value = "任务指派分析人员修改同步监测能力库", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-analysis-user-change", method = RequestMethod.POST)
    public void syncAnalysisUserChange(RestJsonWrapperBean wrapper) {
        runService.syncAnalysisUserChange(wrapper);
    }

    @Override
    @Log(value = "任务指派称重人员修改同步监测能力库", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-weight-user-change", method = RequestMethod.POST)
    public void syncWeighingUserChange(RestJsonWrapperBean wrapper) {
        runService.syncWeighingUserChange(wrapper);
    }

    @Override
    @Log(value = "任务指派前处理人员修改同步监测能力库", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-pretreat-user-change", method = RequestMethod.POST)
    public void syncPretreatUserUserChange(RestJsonWrapperBean wrapper) {
        runService.syncPretreatUserUserChange(wrapper);
    }

    @Override
    @Log(value = "实验室标准样品同步标准试剂管理", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-QCSTD", method = RequestMethod.POST)
    public void syncQCSTD(RestJsonWrapperBean wrapper) {
        runService.syncQCSTD(wrapper);
    }

    @Override
    @Log(value = "实验室标准样品同步标准试剂管理-手动修改", type = LogType.UPDATE)
    @RequestMapping(value = "/action/edit-sync-QCSTD", method = RequestMethod.POST)
    public void editSyncQCSTD(RestJsonWrapperBean wrapper) {
        runService.editSyncQCSTD(wrapper);
    }

    @Override
    @Log(value = "取消合并样品测试", type = LogType.UPDATE)
    @RequestMapping(value = "/action/cancel-combine-order-task", method = RequestMethod.POST)
    public void cancelCombineOrderTask(RestJsonWrapperBean wrapper) {
        runService.cancelCombineOrderTask(wrapper);
    }

    @Override
    @Log(value = "查询分析项", type = LogType.SELECT)
    @RequestMapping(value = "/edited-items/queries", method = RequestMethod.POST)
    public Page<EmcItemBean> selectBatchEditItem(RestJsonWrapperBean wrapper) {
        return runService.selectBatchEditItem(wrapper);
    }

    @Override
    @Log(value = "分析录入-批量编辑分析项", type = LogType.UPDATE)
    @RequestMapping(value = "/action/batch-edit-item", method = RequestMethod.POST)
    public void batchEditItem(RestJsonWrapperBean wrapper) {
        runService.batchEditItem(wrapper);
    }

    @Override
    @Log(value = "分析审核-审核通过", type = LogType.UPDATE)
    @RequestMapping(value = "/action/check-pass", method = RequestMethod.POST)
    public void checkPass(RestJsonWrapperBean wrapper) {
        runService.checkPass(wrapper);
    }

    @Override
    @Log(value = "分析录入-提交检查称重", type = LogType.UPDATE)
    @RequestMapping(value = "/action/check-weighting-status", method = RequestMethod.POST)
    public String checkWeightingStatus(RestJsonWrapperBean wrapper) {
       return runService.checkWeightingStatus(wrapper);
    }

    @Override
    @Log(value = "分析录入-同步采样体积", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-volume", method = RequestMethod.POST)
    public void syncVolume(RestJsonWrapperBean wrapper) {
         runService.syncVolume(wrapper);
    }

    @Override
    @Log(value = "选择前处理", type = LogType.UPDATE)
    @RequestMapping(value = "/action/choose-beforehandle", method = RequestMethod.POST)
    public void chooseBeforeHandle(RestJsonWrapperBean wrapper) {
         runService.chooseBeforeHandle(wrapper);
    }

    @Override
    @Log(value = "选择前处理", type = LogType.UPDATE)
    @RequestMapping(value = "/action/choose-limit", method = RequestMethod.POST)
    public void chooseLimit(RestJsonWrapperBean wrapper) {
         runService.chooseLimit(wrapper);
    }

    @Override
    @Log(value = "更新校准曲线默认值", type = LogType.UPDATE)
    @RequestMapping(value = "/action/set-default-curve-value", method = RequestMethod.POST)
    public void setDefaultCurveValue(RestJsonWrapperBean wrapper) {
        runService.setDefaultCurveValue(wrapper);
    }

    @Override
    @Log(value = "查询合并原始记录状态", type = LogType.SELECT)
    @RequestMapping(value = "/action/select-compose-status", method = RequestMethod.POST)
    public String selectComposeFlag(RestJsonWrapperBean wrapper) {
        return runService.selectComposeFlag(wrapper);
    }

    @Override
    @Log(value = "同步滤筒", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-oliFilm", method = RequestMethod.POST)
    public void syncOliFilm(RestJsonWrapperBean wrapper) {
        runService.syncOliFilm(wrapper);
    }

    @Override
    @Log(value = "删除QC测试(仅分析)", type = LogType.DELETE)
    @RequestMapping(value = "/action/delete-qc-ordertask", method = RequestMethod.POST)
    public void deleteQcOrdertask(RestJsonWrapperBean wrapper) {
         runService.deleteQcOrdertask(wrapper);
    }

    @Override
    @Log(value = "重置原始记录合并状态", type = LogType.DELETE)
    @RequestMapping(value = "/action/reset-compose", method = RequestMethod.POST)
    public void resetCompose(RestJsonWrapperBean wrapper) {
         runService.resetCompose(wrapper);
    }

    @Override
    @Log(value = "查询分析项组", type = LogType.SELECT)
    @RequestMapping(value = "/action/select-itemgroup", method = RequestMethod.POST)
    public String selectNotInItemgroup(RestJsonWrapperBean wrapper) {
        return runService.selectNotInItemgroup(wrapper);
    }

    @Override
    @Log(value = "查询分析项组", type = LogType.SELECT)
    @RequestMapping(value = "/action/select-in-itemgroup", method = RequestMethod.POST)
    public String selectInItemgroup(RestJsonWrapperBean wrapper) {
        return runService.selectInItemgroup(wrapper);
    }

    @Override
    @Log(value = "查询样品分析项组", type = LogType.SELECT)
    @RequestMapping(value = "/unique-order-item-group/queries", method = RequestMethod.POST)
    public Page<EmcOrderItemGroupBean> selectUniqueOrderItemGroup(RestJsonWrapperBean wrapper) {
        return runService.selectUniqueOrderItemGroup(wrapper);
    }

    @Override
    @Log(value = "合并样品分析项组", type = LogType.UPDATE)
    @RequestMapping(value = "/action/combine-order-item-group", method = RequestMethod.POST)
    public void combineOrderItemGroup(RestJsonWrapperBean wrapper) {
        runService.combineOrderItemGroup(wrapper);
    }

    @Override
    @Log(value = "取消合并样品分析项组", type = LogType.UPDATE)
    @RequestMapping(value = "/action/cancel-combine-order-item-group", method = RequestMethod.POST)
    public void cancelCombineOrderItemGroup(RestJsonWrapperBean wrapper) {
        runService.cancelCombineOrderItemGroup(wrapper);
    }

    @Override
    @Log(value = "添加分析项组", type = LogType.SELECT)
    @RequestMapping(value = "/action/add-itemgroup", method = RequestMethod.POST)
    public void addItemgroup(RestJsonWrapperBean wrapper) {
         runService.addItemgroup(wrapper);
    }

    @Override
    @Log(value = "移除分析项组", type = LogType.SELECT)
    @RequestMapping(value = "/action/remove-itemgroup", method = RequestMethod.POST)
    public void removeItemgroup(RestJsonWrapperBean wrapper) {
         runService.removeItemgroup(wrapper);
    }

    @Override
    @Log(value = "查询关联样品", type = LogType.SELECT)
    @RequestMapping(value = "/action/select-relate-order", method = RequestMethod.POST)
    public String selectRelateOrder(RestJsonWrapperBean wrapper) {
        return runService.selectRelateOrder(wrapper);
    }

    @Override
    @Log(value = "回退已提交批", type = LogType.SELECT)
    @RequestMapping(value = "/action/undo-audited-run", method = RequestMethod.POST)
    public void undoAuditedRun(RestJsonWrapperBean wrapper) {
           runService.undoAuditedRun(wrapper);
    }

    @Override
    @Log(value = "查询分析环节-浏览原始记录附件", type = LogType.SELECT)
    @RequestMapping(value = "/browse-record/queries", method = RequestMethod.POST)
    public EmcAnalysisRecordDTO selectBrowseRecordFile(RestJsonWrapperBean wrapper) {
        return runService.selectBrowseRecordFile(wrapper);
    }

    @Log(value = "查询分析环节-浏览原始记录附件", type = LogType.SELECT)
    @RequestMapping(value = "/run-audit-file/queries", method = RequestMethod.POST)
    public EmcAnalysisRecordDTO selectRunAuditFile(RestJsonWrapperBean wrapper) {
        return runService.selectRunAuditFile(wrapper);
    }

    @Log(value = "批量上传附件", type = LogType.INSERT)
    @RequestMapping(value = "/batch-upload-file", method = RequestMethod.POST)
    public Long bathUploadFile(RestJsonWrapperBean wrapper) {
        runService.bathUploadFile(wrapper);
        return 1L;
    }

    @Log(value = "批量导出分析原始记录Excel", type = LogType.SELECT)
    @RequestMapping(value = "/action/export-original-record-excel-zip", method = RequestMethod.POST)
    public String exportOriginalRecordExcelZip(RestJsonWrapperBean wrapper) {
        return runService.exportOriginalRecordExcelZip(wrapper);
    }
}
