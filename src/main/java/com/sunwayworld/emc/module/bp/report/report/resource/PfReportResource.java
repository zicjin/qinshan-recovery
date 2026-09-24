package com.sunwayworld.emc.module.bp.report.report.resource;

import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportRecordDTO;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportSendInfoBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author licm@sunwayworld.com
 * @date 2021-11-24
 */
public interface PfReportResource extends EmcReportResource {

	String completeReport(RestJsonWrapperBean wrapper);

    String selectCompleteDate(RestJsonWrapperBean wrapper);

    String selectCompleteStatus(RestJsonWrapperBean wrapper);

    void createReport(RestJsonWrapperBean wrapper);

    void generateReportResult(RestJsonWrapperBean wrapper);

    Long viewReport(RestJsonWrapperBean wrapper);

    Long viewQcReport(RestJsonWrapperBean wrapper);

    RestValidationResultBean validateResultStatus(RestJsonWrapperBean wrapper);

    void createMergeReport(RestJsonWrapperBean wrapper);

    void generateMergeReportResult(RestJsonWrapperBean wrapper);

    String cancelCompleteReport(RestJsonWrapperBean wrapper);

    EmcReportSendInfoBean selectSendInfo(Long projId);

    EmcReportRecordDTO selectReportRecordDTO(RestJsonWrapperBean wrapper);

    Page<EmcReportSendInfoBean> selectSendInfoPagination(RestJsonWrapperBean wrapper);

    CoreFileBean composeProjectRunRecord(RestJsonWrapperBean wrapper);

    @Log(value = "生成新word报告", type = LogType.INSERT)
    @RequestMapping(value = "/action/view-new-report", method = RequestMethod.POST)
    Long viewNewReport(RestJsonWrapperBean wrapper);

    @Log(value = "项目在报告编制进行回退", type = LogType.INSERT)
    @RequestMapping(value = "/action/project-undo", method = RequestMethod.POST)
    Long projectUndo(RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/action/pack", method = RequestMethod.POST)
    String pack(RestJsonWrapperBean jsonWrapper);
}
