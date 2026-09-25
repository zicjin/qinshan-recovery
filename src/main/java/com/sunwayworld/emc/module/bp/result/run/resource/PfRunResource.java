package com.sunwayworld.emc.module.bp.result.run.resource;

import com.sunwayworld.emc.module.bp.result.run.bean.EmcAnalysisRecordDTO;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcQcRelatedOrderBean;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.result.run.service.PfRunService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

/**
 * @author zhengxy@sunwayworld.com 2021年05月28日
 */
@RequestMapping("/secure/emc/module/bp/result/runs")
public interface PfRunResource extends EmcRunResource, GenericChoosableResource<PfRunService, EmcRunBean, String> {

    void qcAuditSubmit(RestJsonWrapperBean wrapper);

    void qcAuditRunUndo(RestJsonWrapperBean wrapper);

    void completeWeighing(RestJsonWrapperBean wrapper);

    void completePretreat(RestJsonWrapperBean wrapper);

    void copyWeight(RestJsonWrapperBean wrapper);

    RestValidationResultBean validateResultStatus(RestJsonWrapperBean wrapper);

    RestValidationResultBean validateProjectResultStatus(RestJsonWrapperBean wrapper);

    void linkWeightingRun(RestJsonWrapperBean wrapper);

    void unlinkWeightingRun(RestJsonWrapperBean wrapper);

    void linkRun(RestJsonWrapperBean wrapper);

    void unlinkRun(RestJsonWrapperBean wrapper);

    void combineOrderTask(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectUniqueOrderTask(RestJsonWrapperBean wrapper);

    Long deleteQcRelatedOrder(RestJsonWrapperBean wrapper);

    Page<EmcQcRelatedOrderBean> selectQcRelatedOrder(RestJsonWrapperBean wrapper);

    void linkOrder(RestJsonWrapperBean wrapper);

    void unlinkOrder(RestJsonWrapperBean wrapper);

    Page<EmcEquiptBean> selectAcqEquiptPagination(String runId);

    void composeAllPdf(RestJsonWrapperBean wrapper);

    void composeAllRunListReportPdf(RestJsonWrapperBean wrapper);

    void batchUpdateQcResult(RestJsonWrapperBean wrapper);

    void createTempHumidity(RestJsonWrapperBean wrapper);

    void submitProjectRun(RestJsonWrapperBean wrapper);

    void copyQcOrderTo(RestJsonWrapperBean wrapper);

    void copyReplaceQcOrder(RestJsonWrapperBean wrapper);

    void syncAnalysisUserChange(RestJsonWrapperBean wrapper);

    void syncWeighingUserChange(RestJsonWrapperBean wrapper);

    void syncPretreatUserUserChange(RestJsonWrapperBean wrapper);

    void syncQCSTD(RestJsonWrapperBean wrapper);

    void editSyncQCSTD(RestJsonWrapperBean wrapper);

    void cancelCombineOrderTask(RestJsonWrapperBean wrapper);

    Page<EmcItemBean> selectBatchEditItem(RestJsonWrapperBean wrapper);

    void batchEditItem(RestJsonWrapperBean wrapper);

    void checkPass(RestJsonWrapperBean wrapper);

    String checkWeightingStatus(RestJsonWrapperBean wrapper);

    void syncVolume(RestJsonWrapperBean wrapper);

    void chooseBeforeHandle(RestJsonWrapperBean wrapper);

    void chooseLimit(RestJsonWrapperBean wrapper);

    void setDefaultCurveValue(RestJsonWrapperBean wrapper);

    String selectComposeFlag(RestJsonWrapperBean wrapper);

    void syncOliFilm(RestJsonWrapperBean wrapper);

    void deleteQcOrdertask(RestJsonWrapperBean wrapper);

    void resetCompose(RestJsonWrapperBean wrapper);

    void addItemgroup(RestJsonWrapperBean wrapper);

    void removeItemgroup(RestJsonWrapperBean wrapper);

    String selectNotInItemgroup(RestJsonWrapperBean wrapper);

    String selectInItemgroup(RestJsonWrapperBean wrapper);

    Page<EmcOrderItemGroupBean> selectUniqueOrderItemGroup(RestJsonWrapperBean wrapper);

    void combineOrderItemGroup(RestJsonWrapperBean wrapper);

    void cancelCombineOrderItemGroup(RestJsonWrapperBean wrapper);

    String selectRelateOrder(RestJsonWrapperBean wrapper);

    void undoAuditedRun(RestJsonWrapperBean wrapper);

    EmcAnalysisRecordDTO selectBrowseRecordFile(RestJsonWrapperBean wrapper);

    EmcAnalysisRecordDTO selectRunAuditFile(RestJsonWrapperBean wrapper);

    Long bathUploadFile(RestJsonWrapperBean wrapper);

    String exportOriginalRecordExcelZip(RestJsonWrapperBean wrapper);
}
