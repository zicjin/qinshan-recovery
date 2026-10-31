package com.sunwayworld.emc.module.bp.report.report.service;

import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportRecordDTO;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportSendInfoBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.module.item.file.bean.CoreFileBean;

/**
 * @author zhengxy@sunwayworld.com 2021年06月24日
 */
public interface PfReportService extends EmcReportService {

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

	Long viewNewReport(RestJsonWrapperBean wrapper);

	Long projectUndo(RestJsonWrapperBean wrapper);

	void seal(RestJsonWrapperBean wrapper);

	void signIssuePdfIfRequired(CoreFileBean coreFile);

	void editItem(RestJsonWrapperBean wrapper);
}
