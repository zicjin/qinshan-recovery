package com.sunwayworld.emc.module.bp.result.run.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcAnalysisRecordDTO;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcQcRelatedOrderBean;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean.EmcQcCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

/**
 * @author zhengxy@sunwayworld.com 2021年05月28日
 */
public interface PfRunService extends EmcRunService, GenericChoosableService<EmcRunBean, String> {

    void qcAuditSubmit(RestJsonWrapperBean wrapper);

    void qcAuditRunUndo(RestJsonWrapperBean wrapper);

    void submitToReport(List<EmcProjectBean> projectList);

    void addQc(List<EmcOrderTaskBean> orderTaskList, List<EmcQcCategoryBean> qcCategoryList, String stockBatchNo, boolean formulaComputing);

    void completeWeighing(RestJsonWrapperBean wrapper);

    void completePretreat(RestJsonWrapperBean wrapper);

    void copyWeight(RestJsonWrapperBean wrapper);

    RestValidationResultBean validateResultStatus(RestJsonWrapperBean wrapper);

    RestValidationResultBean validateProjectResultStatus(RestJsonWrapperBean wrapper);

    RestValidationResultBean validateRunResultStatus(List<String> runIdList);

    void linkWeightingRun(RestJsonWrapperBean wrapper);

    void unlinkWeightingRun(RestJsonWrapperBean wrapper);

    void linkRun(RestJsonWrapperBean wrapper);

    void unlinkRun(RestJsonWrapperBean wrapper);

    void combineOrderTask(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectUniqueOrderTask(RestJsonWrapperBean wrapper);

    Page<EmcQcRelatedOrderBean> selectQcRelatedOrder(RestJsonWrapperBean wrapper);

    Long deleteQcRelatedOrder(RestJsonWrapperBean wrapper);

    void linkOrder(RestJsonWrapperBean wrapper);

    void unlinkOrder(RestJsonWrapperBean wrapper);

    Page<EmcEquiptBean> selectAcqEquiptPagination(String runId);

    void composeAllPdf(RestJsonWrapperBean wrapper);

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

    void composeAllRunListReportPdf(RestJsonWrapperBean wrapper);

    void composeReportPdfOfProject(List<EmcRunBean> runList, String noticeUserId);

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
}
