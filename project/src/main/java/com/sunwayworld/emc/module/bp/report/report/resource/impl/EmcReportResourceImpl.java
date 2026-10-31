package com.sunwayworld.emc.module.bp.report.report.resource.impl;

import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.archive.bean.EmcArchiveCategoryBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.resource.EmcReportResource;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportResultService;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportSendInfoService;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-18
 */

@LogModule("报告")
@RestController
@GikamBean
public class EmcReportResourceImpl implements EmcReportResource {
    @Autowired
    private EmcReportService reportService;

    @Autowired
    private EmcReportResultService reportResultService;

    @Autowired
    private EmcReportSendInfoService emcReportSendInfoService;

    @Override
    public EmcReportService getService() {
        return reportService;
    }

    @Log(value = "报告回退分析科室查询", type = LogType.SELECT)
    @RequestMapping(value = "/analysis-orgs/queries/undoable", method = RequestMethod.POST)
    @Override
    public Page<EmcOrderTaskBean> selectUndoableAnalysisOrgDataTypePagination(RestJsonWrapperBean wrapper) {
        return getService().selectUndoableAnalysisOrgDataTypePagination(wrapper);
    }

    @Log(value = "报告回退", type = LogType.SELECT)
    @RequestMapping(value = "/action/biz-undo", method = RequestMethod.PUT)
    @Override
    public void bizUndo(RestJsonWrapperBean wrapper) {
        getService().bizUndo(wrapper);
    }

    @Override
    @Log(value = "查看Word报告", type = LogType.SELECT)
    @RequestMapping(value = "/view/document", method = RequestMethod.POST)
    public Long viewDocument(RestJsonWrapperBean wrapper) throws Exception {
        return getService().viewDocument(wrapper);
    }

    @Override
    @Log(value = "查看报告结果", type = LogType.SELECT)
    @RequestMapping(value = "/results/queries", method = RequestMethod.POST)
    public Page<EmcReportResultBean> selectReportResultPagination(RestJsonWrapperBean wrapper) {
        return reportResultService.selectPagination(wrapper);
    }

    @Override
    @Log(value = "根据点位重新生成报出结果", type = LogType.INSERT)
    @RequestMapping(value = "/action/generate-report-result", method = RequestMethod.POST)
    public void generateReportResultByFolder(RestJsonWrapperBean wrapper) {
        getService().generateReportResultByFolder(wrapper);
    }

    @Override
    @Log(value = "根据报告ID查询点位信息", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/folders/queries", method = RequestMethod.POST)
    public Page<EmcFolderBean> selectFolderListByReportId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectFolderListByReportId(id, wrapper);
    }

    @Override
    @Log(value = "根据样品测试重新生成报告", type = LogType.INSERT)
    @RequestMapping(value = "/action/regroup-report", method = RequestMethod.POST)
    public void regroupReport(RestJsonWrapperBean wrapper) {
        getService().regroupReport(wrapper);
    }

    @Override
    @Log(value = "查询归档信息", type = LogType.SELECT)
    @RequestMapping(value = "/archive-categories/queries", method = RequestMethod.POST)
    public Page<EmcArchiveCategoryBean> selectArchiveCategoryPagination(RestJsonWrapperBean wrapper) {
        return getService().selectArchiveCategoryPagination(wrapper);
    }

    @Override
    @Log(value = "归档项目文件", type = LogType.INSERT)
    @RequestMapping(value = "/action/archive", method = RequestMethod.POST)
    public void archiveFile(RestJsonWrapperBean wrapper) {
        getService().archiveFile(wrapper);
    }

    @Override
    @Log(value = "查看RS报告", type = LogType.SELECT)
    @RequestMapping(value = "/view/rs-report", method = RequestMethod.POST)
    public Long viewRsReport(RestJsonWrapperBean wrapper) {
        return getService().viewRsReport(wrapper);
    }

    @Override
    @Log(value = "查看数据表", type = LogType.SELECT)
    @RequestMapping(value = "/view/data-sheet", method = RequestMethod.POST)
    public Long viewDataSheet(RestJsonWrapperBean wrapper) {
        return getService().viewDataSheet(wrapper);
    }

    @Override
    @Log(value = "根据任务id查询批", type = LogType.SELECT)
    @RequestMapping(value = "/queries/run/{projId}", method = RequestMethod.POST)
    public Page<EmcRunBean> selectRunByProjId(@PathVariable Long projId, RestJsonWrapperBean wrapper) {
        return getService().selectRunByProjId(projId, wrapper);
    }

    @Override
    @Log(value = "退回批", type = LogType.UPDATE)
    @RequestMapping(value = "/undoRun/{projId}", method = RequestMethod.PUT)
    public void undoRun(@PathVariable Long projId, RestJsonWrapperBean wrapper) {
        getService().undoRun(projId, wrapper);
    }

    @Override
    @Log(value = "重新提交被退回的批", type = LogType.UPDATE)
    @RequestMapping(value = "/submitUndid", method = RequestMethod.PUT)
    public void submitUndid(RestJsonWrapperBean wrapper) {
        getService().submitUndid(wrapper);
    }

    @Override
    @Log(value = "重新提交被退回的批", type = LogType.UPDATE)
    @RequestMapping(value = "/updateByProjectId", method = RequestMethod.PUT)
    public int updateByProjectId(RestJsonWrapperBean wrapperBean) {
        return emcReportSendInfoService.updateByProjectId(wrapperBean);
    }
}
