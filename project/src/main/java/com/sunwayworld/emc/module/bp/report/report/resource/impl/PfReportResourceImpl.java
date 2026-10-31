package com.sunwayworld.emc.module.bp.report.report.resource.impl;

import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportRecordDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportSendInfoBean;
import com.sunwayworld.emc.module.bp.report.report.resource.PfReportResource;
import com.sunwayworld.emc.module.bp.report.report.service.PfReportService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.file.bean.CoreFileBean;

/**
 * @author licm@sunwayworld.com
 * @date 2021-11-24
 */
@LogModule("报告")
@RestController
@GikamBean
public class PfReportResourceImpl extends EmcReportResourceImpl implements PfReportResource {

    @Autowired
    private PfReportService reportService;

    @Override
    @Log(value = "报告完成", type = LogType.UPDATE)
    @RequestMapping(value = "/complete-report", method = RequestMethod.POST)
    public String completeReport(RestJsonWrapperBean wrapper) {
    	 return reportService.completeReport(wrapper);
    }

    @Override
    @Log(value = "查找日期", type = LogType.SELECT)
    @RequestMapping(value = "/select-complete-date", method = RequestMethod.POST)
    public String selectCompleteDate(RestJsonWrapperBean wrapper) {
        return reportService.selectCompleteDate(wrapper);
    }

    @Override
    @Log(value = "查找完成状态", type = LogType.SELECT)
    @RequestMapping(value = "/select-complete-status", method = RequestMethod.POST)
    public String selectCompleteStatus(RestJsonWrapperBean wrapper) {
        return reportService.selectCompleteStatus(wrapper);
    }

    @Override
    @Log(value = "创建报告", type = LogType.INSERT)
    @RequestMapping(value = "/action/create", method = RequestMethod.POST)
    public void createReport(RestJsonWrapperBean wrapper) {
        reportService.createReport(wrapper);
    }

    @Override
    @Log(value = "生成报告数据", type = LogType.INSERT)
    @RequestMapping(value = "/action/generate-data", method = RequestMethod.POST)
    public void generateReportResult(RestJsonWrapperBean wrapper) {
        reportService.generateReportResult(wrapper);
    }

    @Override
    @Log(value = "查看报告", type = LogType.INSERT)
    @RequestMapping(value = "/action/view-report", method = RequestMethod.POST)
    public Long viewReport(RestJsonWrapperBean wrapper) {
        return reportService.viewReport(wrapper);
    }

    @Override
    @Log(value = "查看质控报告", type = LogType.INSERT)
    @RequestMapping(value = "/action/view-qc-report", method = RequestMethod.POST)
    public Long viewQcReport(RestJsonWrapperBean wrapper) {
        return reportService.viewQcReport(wrapper);
    }

    @Override
    @Log(value = "校验结果是否超标", type = LogType.UPDATE)
    @RequestMapping(value = "/action/validate-result-status", method = RequestMethod.POST)
    public RestValidationResultBean validateResultStatus(RestJsonWrapperBean wrapper) {
        return reportService.validateResultStatus(wrapper);
    }

    @Override
    @Log(value = "创建合并报告", type = LogType.INSERT)
    @RequestMapping(value = "/action/create-merge-report", method = RequestMethod.POST)
    public void createMergeReport(RestJsonWrapperBean wrapper) {
        reportService.createMergeReport(wrapper);
    }

    @Override
    @Log(value = "生成合并报告数据", type = LogType.INSERT)
    @RequestMapping(value = "/action/generate-merge-data", method = RequestMethod.POST)
    public void generateMergeReportResult(RestJsonWrapperBean wrapper) {
        reportService.generateMergeReportResult(wrapper);
    }

    @Override
    @Log(value = "取消报告完成", type = LogType.UPDATE)
    @RequestMapping(value = "/cancel-complete-report", method = RequestMethod.POST)
    public String cancelCompleteReport(RestJsonWrapperBean wrapper) {
        return reportService.cancelCompleteReport(wrapper);
    }

    @Override
    @Log(value = "查询报告发送信息", type = LogType.SELECT)
    @RequestMapping(value = "/{projId}/report-send-info", method = RequestMethod.GET)
    public EmcReportSendInfoBean selectSendInfo(@PathVariable Long projId) {
        return reportService.selectSendInfo(projId);
    }

    @Override
    @Log(value = "查询报告浏览信息", type = LogType.SELECT)
    @RequestMapping(value = "/browse-record/queries", method = RequestMethod.POST)
    public EmcReportRecordDTO selectReportRecordDTO(RestJsonWrapperBean wrapper) {
        return reportService.selectReportRecordDTO(wrapper);
    }

    @Override
    @Log(value = "查询报告发送信息列表", type = LogType.SELECT)
    @RequestMapping(value = "/report-send-info/queries", method = RequestMethod.POST)
    public Page<EmcReportSendInfoBean> selectSendInfoPagination(RestJsonWrapperBean wrapper) {
        return reportService.selectSendInfoPagination(wrapper);
    }

    @Override
    @Log(value = "合成项目所有原始记录文件", type = LogType.SELECT)
    @RequestMapping(value = "/action/compose-project-run-record", method = RequestMethod.POST)
    public CoreFileBean composeProjectRunRecord(RestJsonWrapperBean wrapper) {
        return reportService.composeProjectRunRecord(wrapper);
    }

    @Override
    public Long viewNewReport(RestJsonWrapperBean wrapper) {
        return reportService.viewNewReport(wrapper);
    }

    @Override
    public Long projectUndo(RestJsonWrapperBean wrapper) {
        return reportService.projectUndo(wrapper);
    }

    @Log(value = "盖章", type = LogType.INSERT)
    @RequestMapping(value = "/seal", method = RequestMethod.POST)
    public Long seal(RestJsonWrapperBean wrapper) {
        reportService.seal(wrapper);
        return 1L;
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    @Log(value = "打包指定的一个或多个附件，并返回下载的路径", type = LogType.EXPORT)
    public String pack(RestJsonWrapperBean jsonWrapper) {
        return getService().pack(jsonWrapper);
    }

    @RequestMapping(value = "/action/report-pack", method = RequestMethod.POST)
    @Log(value = "打包盖章报告附件", type = LogType.EXPORT)
    public String packReportFile(RestJsonWrapperBean jsonWrapper) {
        return getService().packReportFile(jsonWrapper);
    }

    @Log(value = "编辑分析项", type = LogType.UPDATE)
    @RequestMapping(value = "/edit-item", method = RequestMethod.POST)
    public Long editItem(RestJsonWrapperBean wrapper) {
        reportService.editItem(wrapper);
        return 1L;
    }
}
