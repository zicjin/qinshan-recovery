package com.sunwayworld.emc.module.bp.report.report.resource;

import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.archive.bean.EmcArchiveCategoryBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-18
 */
@RequestMapping("/secure/emc/module/bp/report/reports")
public interface EmcReportResource extends GenericResource<EmcReportService, EmcReportBean, Long>, GenericAuditableResource<EmcReportService, EmcReportBean, Long> {

    Page<EmcOrderTaskBean> selectUndoableAnalysisOrgDataTypePagination(RestJsonWrapperBean wrapper);

    void bizUndo(RestJsonWrapperBean wrapper);

    Long viewDocument(RestJsonWrapperBean wrapper) throws Exception;

    Page<EmcReportResultBean> selectReportResultPagination(RestJsonWrapperBean wrapper);

    void generateReportResultByFolder(RestJsonWrapperBean wrapper);

    Page<EmcFolderBean> selectFolderListByReportId(Long id, RestJsonWrapperBean wrapper);

    void regroupReport(RestJsonWrapperBean wrapper);

    Page<EmcArchiveCategoryBean> selectArchiveCategoryPagination(RestJsonWrapperBean wrapper);

    void archiveFile(RestJsonWrapperBean wrapper);

    Long viewRsReport(RestJsonWrapperBean wrapper);

    Long viewDataSheet(RestJsonWrapperBean wrapper);

    Page<EmcRunBean> selectRunByProjId(Long projId, RestJsonWrapperBean wrapper);

    void undoRun(Long projId, RestJsonWrapperBean wrapper);

    void submitUndid(RestJsonWrapperBean wrapper);

    int updateByProjectId(RestJsonWrapperBean wrapper);
}
