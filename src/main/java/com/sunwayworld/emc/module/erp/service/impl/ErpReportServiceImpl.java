package com.sunwayworld.emc.module.erp.service.impl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportSendInfoBean;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportSendInfoService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.emc.module.erp.bean.MidOrderReportAuditsApplyDto;
import com.sunwayworld.emc.module.erp.bean.MidOrderReportAuditsDto;
import com.sunwayworld.emc.module.erp.bean.MidOrderReportSendDTO;
import com.sunwayworld.emc.module.erp.bean.MidReportDTO;
import com.sunwayworld.emc.module.erp.bean.MidReportDetailDTO;
import com.sunwayworld.emc.module.erp.bean.MidUpdateMerchandiserDTO;
import com.sunwayworld.emc.module.erp.bean.ReportAuditStatus;
import com.sunwayworld.emc.module.erp.bean.RestResult;
import com.sunwayworld.emc.module.erp.constant.ErpConstant;
import com.sunwayworld.emc.module.erp.service.ErpReportService;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpBean;
import com.sunwayworld.emc.module.mdm.emp.emp.service.EmcEmpService;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.OkHttpClientUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallRequestBean;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallResponseBean;
import com.sunwayworld.module.sys.remotecall.service.CoreRemoteCallRequestService;
import com.sunwayworld.module.sys.remotecall.service.CoreRemoteCallResponseService;

/**
 * @author zhengxy@sunwayworld.com 2023年07月26日
 */
@Repository
public class ErpReportServiceImpl implements ErpReportService {

    private static final Logger logger = LoggerFactory.getLogger(ErpReportServiceImpl.class);

    @Autowired
    @Lazy
    private EmcProjectService projectService;

    @Autowired
    private CoreOrgService coreOrgService;

    @Autowired
    private CoreRemoteCallRequestService remoteCallRequestService;

    @Autowired
    private CoreRemoteCallResponseService remoteCallResponseService;

    @Autowired
    private CoreFileService coreFileService;

    @Autowired
    private EmcEmpService emcEmpService;

    @Autowired
    private EmcSampleService sampleService;

    @Autowired
    private EmcReportSendInfoService reportSendInfoService;

    @Override
    public void updateMerchandiser(List<EmcReportBean> reportList) {
        // 如果不是从ERP推送过来的任务，则不用更新跟单员
        List<EmcProjectBean> erpProjectList = getErpProjectList(reportList);
        if (erpProjectList.isEmpty()) {
            return;
        }

        String merchantId = getMerchantId();
        EmcEmpBean erpUser = getErpUser();

        erpProjectList.forEach(project -> {
            MidUpdateMerchandiserDTO updateMerchandiserDto = new MidUpdateMerchandiserDTO();
            updateMerchandiserDto.setMerchantId(merchantId);
            updateMerchandiserDto.setOperatorGbId(erpUser.getErpGbId());
            updateMerchandiserDto.setMerchandiserGbId(erpUser.getErpGbId());
            updateMerchandiserDto.setMerchandiserJobNumber(erpUser.getErpJobNumber());
            updateMerchandiserDto.setGbId(project.getErpGbId());

            String jsonStr = JSONObject.toJSONString(updateMerchandiserDto);
            logger.info("项目{}分配跟单员参数: {}", project.getProjNo(), jsonStr);
            String result = remoteCallRequestService.processSyncRequest("updateMerchandiser", jsonStr);
            logger.info("项目{}分配跟单员结果：{}", project.getProjNo(), result);

            RestResult<Object> restResult = JSON.parseObject(result, new TypeReference<RestResult<Object>>(){});

            if (StringUtils.equals(restResult.getResultCode(), ErpConstant.SUCCESS_CODE)) {
                logger.info("项目{}分配跟单员成功", project.getProjNo());
            }
        });
    }

    @Override
    public String updateMerchandiserToErp(String jsonStr) {
        return HttpClientManager.getInstance().sendHttpPost(getErpServiceUrl() + ErpConstant.UPDATE_MERCHANDISER_INTERFACE, jsonStr);
    }

    @Override
    public void uploadOrderReportFile(List<EmcReportBean> reportList) {
        // 如果不是从ERP推送过来的任务，则不用上传报告文件
        List<EmcProjectBean> erpProjectList = getErpProjectList(reportList);
        if (erpProjectList.isEmpty()) {
            return;
        }

        List<Long> erpProjectIdList = erpProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());
        List<EmcReportBean> erpReportList = reportList.stream().filter(report -> erpProjectIdList.contains(report.getProjId())).collect(Collectors.toList());
        if (erpReportList.isEmpty()) {
            return;
        }

        List<String> targetIdList = erpReportList.stream().map(report -> "T_EMC_REPORT$" + report.getId()).collect(Collectors.toList());
        List<CoreFileBean> reportFileList = coreFileService.getDao().selectListByOneColumnValues(targetIdList, "targetId");
        List<CoreFileBean> wordFileList = reportFileList.stream().filter(file -> StringUtils.equals(file.getBizCategory(), "wordReport")).collect(Collectors.toList());

        if (wordFileList.isEmpty()) {
            return;
        }

        // 请求头
        Map<String,String> headerMap = new HashMap<>(1);
        // 内容类型
        headerMap.put("Content-Type","multipart/form-data");

        String requestUrl = getErpServiceUrl() + ErpConstant.UPLOAD_ORDER_REPORT_FILE_INTERFACE;

        erpReportList.forEach(report -> {
            CoreFileBean reportFile = wordFileList.stream().filter(file -> StringUtils.contains(file.getTargetId(), report.getId().toString())).findFirst().orElseGet(CoreFileBean::new);
            if (ObjectUtils.isEmpty(reportFile.getId())) {
                return;
            }

            // 如果没有ERP订单ID，则项目可能不是从ERP推送过来的
            EmcProjectBean project = erpProjectList.stream().filter(p -> ObjectUtils.equals(report.getProjId(), p.getId())).findFirst().orElseGet(EmcProjectBean::new);
            if (ObjectUtils.isEmpty(project.getErpGbId())) {
                return;
            }

            Map<String,String> paramMap = new HashMap<>(1);
            paramMap.put("gbId", project.getErpGbId().toString());

            File wordFile = new File(FilePathManager.getLocalPath(CoreFileUtils.toFilePath(reportFile)).toString());
            // 文件参数
            Map<String, File> fileParamMap = new HashMap<>(1);
            fileParamMap.put("file", wordFile);

            try {
                String result = post(requestUrl, fileParamMap, paramMap, headerMap);
                logger.info("上传报告文件返回结果：{}", result);

                // 创建远程调用记录
                createRemoteCall("uploadOrderReportFile", JSON.toJSONString(reportFile), result);

                RestResult<Map<String, String>> restResult = JSON.parseObject(result, new TypeReference<RestResult<Map<String, String>>>(){});

                if (StringUtils.equals(restResult.getResultCode(), ErpConstant.SUCCESS_CODE)) {
                    logger.info("项目{}上传报告文件成功", project.getProjNo());
                    // 将ERP返回的reportUrl信息保存到report中，上传草稿报告和正式报告时需要使用
                    report.setExt$Item("reportUrl", restResult.getResultData().get(wordFile.getName()));
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("上传报告{} - {}失败", report.getId(), report.getReportNo());
            }

        });
    }

    @Override
    public void uploadOrderDraftReport(List<EmcReportBean> reportList) {
        // 如果不是从ERP推送过来的任务，则不用上传报告文件
        List<EmcProjectBean> erpProjectList = getErpProjectList(reportList);
        if (erpProjectList.isEmpty()) {
            return;
        }

        List<Long> erpProjectIdList = erpProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());
        List<EmcReportBean> erpReportList = reportList.stream().filter(report -> erpProjectIdList.contains(report.getProjId())).collect(Collectors.toList());

        List<EmcSampleBean> sampleList = sampleService.getDao().selectListByOneColumnValues(erpProjectIdList, "projId", Arrays.asList("id", "projId", "acceptedDate"));

        Long erpUserId = getErpUserId();

        erpProjectList.forEach(project -> {
            MidReportDTO reportDto = new MidReportDTO();
            reportDto.setMerchantId(getMerchantId());
            reportDto.setNeedOverride(true);
            reportDto.setNeedSend(false);
            reportDto.setOperatorGbId(erpUserId);

            sampleList.stream().filter(sample -> ObjectUtils.equals(sample.getProjId(), project.getId())
                    && !ObjectUtils.isEmpty(sample.getAcceptedDate()))
                    .map(EmcSampleBean::getAcceptedDate)
                    .min(Comparator.comparing(Function.identity()))
                    .ifPresent(acceptedDate -> reportDto.setStartExperimentTime(acceptedDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));

            List<EmcReportBean> curReportList = erpReportList.stream().filter(report -> ObjectUtils.equals(project.getId(), report.getProjId())).collect(Collectors.toList());
            reportDto.setReports(createReportDetail(project, curReportList));

            String jsonStr = JSONObject.toJSONString(reportDto);
            logger.info("项目{}上传草稿报告参数: {}", project.getProjNo(), jsonStr);
            String result = remoteCallRequestService.processSyncRequest("uploadOrderReport", jsonStr);
            //String result = uploadOrderDraftReport(jsonStr);
            logger.info("项目{}上传草稿报告结果：{}", project.getProjNo(), result);

            RestResult<Object> restResult = JSON.parseObject(result, new TypeReference<RestResult<Object>>(){});

            if (StringUtils.equals(restResult.getResultCode(), ErpConstant.SUCCESS_CODE)) {
                logger.info("项目{}上传草稿报告成功", project.getProjNo());
            }
        });
    }

    @Override
    public String uploadOrderDraftReportToErp(String jsonStr) {
        return HttpClientManager.getInstance().sendHttpPost(getErpServiceUrl() + ErpConstant.UPLOAD_ORDER_DRAFT_REPORT_INTERFACE, jsonStr);
    }

    @Override
    public void submitOrderReportAuditApply(List<EmcReportBean> reportList) {
        // 如果不是从ERP推送过来的任务，则不用上传报告文件
        List<EmcProjectBean> erpProjectList = getErpProjectList(reportList);
        if (erpProjectList.isEmpty()) {
            return;
        }

        EmcEmpBean erpUser = getErpUser();

        erpProjectList.forEach(project -> {
            MidOrderReportAuditsApplyDto auditsApplyDto = new MidOrderReportAuditsApplyDto();
            auditsApplyDto.setGbId(project.getErpGbId());
            auditsApplyDto.setOperatorGbId(erpUser.getErpGbId());
            auditsApplyDto.setMerchantId(getMerchantId());
            auditsApplyDto.setAuditorJobNumber(erpUser.getErpJobNumber());

            String jsonStr = JSONObject.toJSONString(auditsApplyDto);
            logger.info("项目{}提交报告审核申请参数: {}", project.getProjNo(), jsonStr);
            String result = remoteCallRequestService.processSyncRequest("orderReportAuditsApply", jsonStr);
            logger.info("项目{}提交报告审核申请返回结果：{}", project.getProjNo(), result);
            RestResult<Object> restResult = JSON.parseObject(result, new TypeReference<RestResult<Object>>(){});

            if (StringUtils.equals(restResult.getResultCode(), ErpConstant.SUCCESS_CODE)) {
                logger.info("项目{}提交报告审核申请成功", project.getProjNo());
            }
        });
    }

    @Override
    public String submitOrderReportAuditApplyToErp(String jsonStr) {
        return HttpClientManager.getInstance().sendHttpPost(getErpServiceUrl() + ErpConstant.SUBMIT_ORDER_REPORT_AUDIT_APPLY_INTERFACE, jsonStr);
    }


    @Override
    public void auditOrderReport(List<EmcReportBean> reportList, boolean reject, String rejectReason) {
        // 报告审核提交或退回时调用，用于通知ERP报告审核结果
        List<EmcProjectBean> erpProjectList = getErpProjectList(reportList);
        if (erpProjectList.isEmpty()) {
            return;
        }

        String merchantId = getMerchantId();
        Long erpUserId = getErpUserId();

        erpProjectList.forEach(project -> {
            MidOrderReportAuditsDto reportAuditsDto = new MidOrderReportAuditsDto();
            reportAuditsDto.setMerchantId(merchantId);
            reportAuditsDto.setOperatorGbId(erpUserId);
            reportAuditsDto.setGbId(project.getErpGbId());
            reportAuditsDto.setState(ReportAuditStatus.SUCCESS.getCode());

            // 如果是退回，则需要填写退回原因
            if (reject) {
                logger.info("项目{}报告被拒绝原因为：{}", project.getProjNo(), rejectReason);
                reportAuditsDto.setFailReason(rejectReason);
                reportAuditsDto.setState(ReportAuditStatus.FAIL.getCode());
            }

            String jsonStr = JSONObject.toJSONString(reportAuditsDto);
            logger.info("项目{}推送订单报告审核结果参数：{}", project.getProjNo(), jsonStr);
            // 通过远程调用模块记录请求参数和请求返回结果
            String result = remoteCallRequestService.processSyncRequest("orderReportAudit", jsonStr);
            logger.info("项目{}推送订单报告审核返回结果：{}", project.getProjNo(), result);
            RestResult<Object> restResult = JSON.parseObject(result, new TypeReference<RestResult<Object>>(){});

            if (StringUtils.equals(restResult.getResultCode(), ErpConstant.SUCCESS_CODE)) {
                logger.info("项目{}推送订单报告审核结果成功", project.getProjNo());
            }
        });
    }

    @Override
    public String auditOrderReportToErp(String jsonStr) {
        return HttpClientManager.getInstance().sendHttpPost(getErpServiceUrl() + ErpConstant.AUDIT_ORDER_REPORT_INTERFACE, jsonStr);
    }

    @Override
    public void uploadOrderFinalReport(List<EmcReportBean> reportList) {
        List<EmcProjectBean> erpProjectList = getErpProjectList(reportList);
        if (erpProjectList.isEmpty()) {
            return;
        }

        List<Long> erpProjectIdList = erpProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());
        List<EmcReportBean> erpReportList = reportList.stream().filter(report -> erpProjectIdList.contains(report.getProjId())).collect(Collectors.toList());

        Long erpUserId = getErpUserId();

        erpProjectList.forEach(project -> {
            MidReportDTO reportDto = new MidReportDTO();
            reportDto.setMerchantId(getMerchantId());
            reportDto.setNeedOverride(true);
            reportDto.setNeedSend(true);
            reportDto.setOperatorGbId(erpUserId);

            List<EmcReportBean> curReportList = erpReportList.stream().filter(report -> ObjectUtils.equals(report.getProjId(), project.getId())).collect(Collectors.toList());
            reportDto.setReports(createReportDetail(project, curReportList));

            String jsonStr = JSONObject.toJSONString(reportDto);
            String result = remoteCallRequestService.processSyncRequest("uploadOrderFinalReport", jsonStr);
            logger.info("项目{}上传正式报告返回结果为：{}", project.getProjNo(), result);
            RestResult<Object> restResult = JSON.parseObject(result, new TypeReference<RestResult<Object>>(){});

            if (StringUtils.equals(restResult.getResultCode(), ErpConstant.SUCCESS_CODE)) {
                String reportNos = curReportList.stream().map(EmcReportBean::getReportNo).collect(Collectors.joining(","));
                logger.info("项目{}下的报告{}上传正式报告成功", project.getProjNo(), reportNos);
            }
        });
    }

    @Override
    public String uploadOrderFinalReportToErp(String jsonStr) {
        return HttpClientManager.getInstance().sendHttpPost(getErpServiceUrl() + ErpConstant.UPLOAD_ORDER_FINAL_REPORT_INTERFACE, jsonStr);
    }

    @Override
    public void sendOrderReport(List<EmcReportBean> reportList) {
        // 报告发送提交时调用，向ERP发送报告
        List<EmcProjectBean> erpProjectList = getErpProjectList(reportList);
        if (erpProjectList.isEmpty()) {
            return;
        }

        List<Long> erpProjectIdList = erpProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());
        List<EmcReportSendInfoBean> sendInfoList = reportSendInfoService.selectListByFilter(SearchFilter.instance().match("projId", erpProjectIdList).filter(MatchPattern.OR));

        String merchantId = getMerchantId();
        Long erpUserId = getErpUserId();

        erpProjectList.forEach(project -> {
            EmcReportSendInfoBean sendInfo = sendInfoList.stream().filter(info -> ObjectUtils.equals(project.getId(), info.getProjId())).findFirst().orElseGet(EmcReportSendInfoBean::new);

            MidOrderReportSendDTO reportSendDto = new MidOrderReportSendDTO();
            reportSendDto.setMerchantId(merchantId);
            reportSendDto.setOperatorGbId(erpUserId);
            reportSendDto.setOrderGbId(project.getErpGbId());

            // 电子报告
            reportSendDto.setbCopyToEngineer(Constant.YES.equals(sendInfo.getSendToSaler()));
            reportSendDto.setbCopyToMerchandiser(Constant.YES.equals(sendInfo.getSendToQuoter()));
            reportSendDto.setEmail(sendInfo.getSendEmail());

            // 纸质报告
            if (NumberUtils.isNumber(sendInfo.getExpressType())) {
                reportSendDto.setExpressType(NumberUtils.parseInt(sendInfo.getExpressType()));
            }
            reportSendDto.setExpressNo(sendInfo.getExpressNo());
            String recipientPhone = sendInfo.getExt$Item("recipientPhone");
            if (!StringUtils.isEmpty(recipientPhone) && recipientPhone.length() > 4) {
                reportSendDto.setLastFourOfPhone(recipientPhone.substring(recipientPhone.length() - 4));
            }
            reportSendDto.setRemark(sendInfo.getRemark());

            /*reportSendDto.setbActualSendEmail(false);
            reportSendDto.setbJudgeSameMerchantContractBusinessSetting(false);
            reportSendDto.setbRecordFlowLog(false);
            reportSendDto.setbSendReport(false);
            reportSendDto.setbSendSms(false);*/

            String jsonStr = JSONObject.toJSONString(reportSendDto);
            logger.info("项目{}下的报告发送参数为：{}", project.getProjNo(), jsonStr);
            String result = remoteCallRequestService.processSyncRequest("orderReportSend", jsonStr);
            logger.info("项目{}下的报告发送结果为：{}", project.getProjNo(), result);
            RestResult<Object> restResult = JSON.parseObject(result, new TypeReference<RestResult<Object>>(){});

            if (StringUtils.equals(restResult.getResultCode(), ErpConstant.SUCCESS_CODE)) {
                logger.info("项目{}下的报告发送成功", project.getProjNo());
            }
        });
    }

    @Override
    public String sendOrderReportToErp(String jsonStr) {
        return HttpClientManager.getInstance().sendHttpPost(getErpServiceUrl() + ErpConstant.SEND_ORDER_REPORT_INTERFACE, jsonStr);
    }

    @Override
    public void updateReportAuditUser(RestJsonWrapperBean wrapper) {
        // 此方式可以不用调用，提交报告审核时指定审核人就行
    }

    @Override
    public String getMerchantId() {
        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
        CoreOrgBean parentOrg = coreOrgService.selectById(parentOrgId);

        return parentOrg.getExt$Item("erpMerchantId");
    }

    @Override
    public Long getDefaultSaleGbId() {
        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
        EmcEmpBean saleEmp = emcEmpService.selectFirstByFilter(SearchFilter.instance().match("orgId", parentOrgId).filter(MatchPattern.SEQ).match("QUOTER", Constant.YES).filter(MatchPattern.SEQ));

        return Objects.nonNull(saleEmp) ? saleEmp.getErpGbId() : null;
    }

    private Long getErpUserId() {
        String loginUserId = LocalContextHelper.getLoginUserId();
        EmcEmpBean emp = emcEmpService.selectById(loginUserId);

        return Objects.isNull(emp.getErpGbId()) ? getDefaultSaleGbId() : emp.getErpGbId();
    }

    private EmcEmpBean getErpUser() {
        String loginUserId = LocalContextHelper.getLoginUserId();
        EmcEmpBean emp = emcEmpService.selectById(loginUserId);

        return Objects.isNull(emp.getErpGbId()) ? getDefaultSaleUser() : emp;
    }

    private EmcEmpBean getDefaultSaleUser() {
        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
        EmcEmpBean saleEmp = emcEmpService.selectFirstByFilter(SearchFilter.instance().match("orgId", parentOrgId).filter(MatchPattern.SEQ).match("QUOTER", Constant.YES).filter(MatchPattern.SEQ));

        return Optional.ofNullable(saleEmp).orElseGet(EmcEmpBean::new);
    }

    private List<EmcProjectBean> getErpProjectList(List<EmcReportBean> reportList) {
        List<Long> projectIdList = reportList.stream().map(EmcReportBean::getProjId).distinct().collect(Collectors.toList());
        List<EmcProjectBean> projectList = projectService.selectListByIds(projectIdList);

        // 如果不是从ERP推送过来的任务，则不用上传报告文件
        return projectList.stream().filter(project -> !ObjectUtils.isEmpty(project.getErpGbId())).collect(Collectors.toList());
    }

    private List<MidReportDetailDTO> createReportDetail(EmcProjectBean project, List<EmcReportBean> reportList) {
        List<MidReportDetailDTO> reports = new ArrayList<>(reportList.size());
        reportList.forEach(report -> {
            MidReportDetailDTO detailDto = new MidReportDetailDTO();
            detailDto.setOrderGbId(project.getErpGbId());
            detailDto.setReportName(project.getProjName());
            detailDto.setReportUrl(report.getExt$Item("reportUrl"));
            reports.add(detailDto);
        });

        return reports;
    }

    private static String post(String url, Map<String, File> fileParamMap, Map<String, String> paramMap, Map<String, String> headers) throws IOException {
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (MapUtils.isNotEmpty(fileParamMap)) {
            fileParamMap.forEach((key, file) -> requestBody.addFormDataPart(key, file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file)));
        }

        if (MapUtils.isNotEmpty(paramMap)) {
            paramMap.forEach(requestBody::addFormDataPart);
        }

        Request.Builder requestBuilder = new Request.Builder();
        if (MapUtils.isNotEmpty(headers)) {
            headers.forEach(requestBuilder::header);
        }

        RequestBody body = requestBody.build();
        Request request = requestBuilder.url(url).method("POST", body).build();
        Response response = OkHttpClientUtils.execute(request);

        if (response.body() != null) {
            return response.body().string();
        }

        return "{}";
    }

    private String getErpServiceUrl() {
        String merchantId = getMerchantId();
        String constantKey = StringUtils.isEmpty(merchantId) ? "ERP_SERVICE_URL" : "ERP_SERVICE_URL_" + merchantId;
        return Optional.ofNullable(ApplicationContextHelper.getConstantValue(constantKey)).orElse(ErpConstant.ERP_BASE_URL);
    }

    /**
     * 创建远程调用记录
     * @param requestConfigId 请求配置ID
     * @param requestJson 请求JSON
     * @param responseJson 响应JSON
     */
    private void createRemoteCall(String requestConfigId, String requestJson, String responseJson) {
        CoreRemoteCallRequestBean callRequest = new CoreRemoteCallRequestBean();
        callRequest.setId(ApplicationContextHelper.getNextIdentity());
        callRequest.setConfigId(requestConfigId);
        callRequest.setRequestData(requestJson);
        callRequest.setStatus(Constant.YES);
        callRequest.setRequestDateTime(LocalDateTime.now());
        remoteCallRequestService.getDao().insert(callRequest);

        CoreRemoteCallResponseBean callResponse = new CoreRemoteCallResponseBean();
        callResponse.setId(ApplicationContextHelper.getNextIdentity());
        callResponse.setRequestId(callRequest.getId());
        callResponse.setResponseDateTime(LocalDateTime.now());
        callResponse.setStatus(Constant.YES);
        callResponse.setResponseData(responseJson);
        remoteCallResponseService.getDao().insert(callResponse);
    }
}
