package com.sunwayworld.emc.module.bp.report.report.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.archive.bean.EmcArchiveCategoryBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-18
 */

public interface EmcReportService extends GenericService<EmcReportBean, Long>, GenericAuditableService<EmcReportBean, Long> {

    void insertByProject(List<Long> projectIdList, boolean viewFlag);

    void bizUndo(RestJsonWrapperBean jsonWrapper);

    Page<EmcOrderTaskBean> selectUndoableAnalysisOrgDataTypePagination(RestJsonWrapperBean wrapper);

    void updateReportStatus(List<EmcOrderTaskBean> orderTaskList);

    Page<EmcReportBean> selectReportOverduePagination(RestJsonWrapperBean wrapper);

    Page<EmcReportBean> selectReportIssueOverduePagination(RestJsonWrapperBean wrapper);

    Long viewDocument(RestJsonWrapperBean wrapper) throws Exception;

    void generateReportResultByFolder(RestJsonWrapperBean wrapper);

    Page<EmcFolderBean> selectFolderListByReportId(Long id, RestJsonWrapperBean wrapper);

    void regroupReport(RestJsonWrapperBean wrapper);

	List<CoreBpmnInstanceStatusDTO<Long>> completeTaskBasic(RestJsonWrapperBean wrapper);

	List<CoreBpmnInstanceStatusDTO<Long>> startProcessBasic(RestJsonWrapperBean wrapper);

	List<CoreBpmnInstanceStatusDTO<Long>> rejectTaskBasic(RestJsonWrapperBean wrapper);

    Page<EmcArchiveCategoryBean> selectArchiveCategoryPagination(RestJsonWrapperBean wrapper);

    void archiveFile(RestJsonWrapperBean wrapper);

    Long viewRsReport(RestJsonWrapperBean wrapper);

    Long viewDataSheet(RestJsonWrapperBean wrapper);

    Page<EmcRunBean> selectRunByProjId(Long projId, RestJsonWrapperBean wrapper);

    void undoRun(Long projId, RestJsonWrapperBean wrapper);

    void submitUndid(RestJsonWrapperBean wrapper);
}
