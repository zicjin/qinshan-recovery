package com.sunwayworld.emc.module.erp.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjectChangeBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.erp.bean.ConfirmOrderChangeReq;
import com.sunwayworld.emc.module.erp.bean.MidContractReviewAuditsDto;
import com.sunwayworld.emc.module.erp.bean.RestResult;
import com.sunwayworld.emc.module.erp.bean.UserContractReviewAuditStatus;
import com.sunwayworld.emc.module.erp.constant.ErpConstant;
import com.sunwayworld.emc.module.erp.service.ErpOrderService;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpBean;
import com.sunwayworld.emc.module.mdm.emp.emp.service.EmcEmpService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.sys.remotecall.service.CoreRemoteCallRequestService;

/**
 * @author zhengxy@sunwayworld.com 2023年07月26日
 */
@Repository
public class ErpOrderServiceImpl implements ErpOrderService {

    private static final Logger logger = LoggerFactory.getLogger(ErpOrderServiceImpl.class);

    @Autowired
    private CoreOrgService coreOrgService;

    @Autowired
    private EmcEmpService emcEmpService;

    @Autowired
    private CoreRemoteCallRequestService remoteCallRequestService;

    @Override
    public void updateMerchandiser(RestJsonWrapperBean wrapper) {

    }

    @Override
    public void confirmOrderChange(List<EmcProjectChangeBean> projectList, boolean reject, String remark) {
        List<EmcProjectChangeBean> validProjectList = projectList.stream().filter(project -> !ObjectUtils.isEmpty(project.getErpGbId())
                && !ObjectUtils.isEmpty(project.getErpReviewId())).collect(Collectors.toList());
        // 如果不是ERP 推送的任务，不发送审核结果
        if (validProjectList.isEmpty()) {
            return;
        }

        String merchantId = getMerchantId();
        Long erpUserId = getErpUserId();
        validProjectList.forEach(project -> {
            ConfirmOrderChangeReq changeReq = new ConfirmOrderChangeReq();
            changeReq.setGbId(project.getErpGbId());
            changeReq.setMerchantId(merchantId);
            changeReq.setOperatorGbId(erpUserId);
            changeReq.setType(1);
            changeReq.setRemark(remark);

            // 如果是退回，则需要填写退回原因
            if (reject) {
                logger.info("项目{}变更被拒绝原因为：{}", project.getProjNo(), remark);
                changeReq.setType(0);
            }

            String jsonStr = JSONObject.toJSONString(changeReq);
            logger.info("项目{}推送变更审核结果参数：{}", project.getProjNo(), jsonStr);
            // 通过远程调用模块记录请求参数和请求返回结果
            String result = remoteCallRequestService.processSyncRequest("confirmOrderChange", jsonStr);
            logger.info("项目{}推送变更审核返回结果：{}", project.getProjNo(), result);
            RestResult<Object> restResult = JSON.parseObject(result, new TypeReference<RestResult<Object>>(){});

            if (StringUtils.equals(restResult.getResultCode(), ErpConstant.SUCCESS_CODE)) {
                logger.info("项目{}推送变更审核结果成功", project.getProjNo());
            }
        });
    }

    @Override
    public String confirmOrderChangeToErp(String jsonStr) {
        return HttpClientManager.getInstance().sendHttpPost(getErpServiceUrl() + ErpConstant.CONFIRM_ORDER_CHANGE_INTERFACE, jsonStr);
    }

    @Override
    public void reviewOrderContract(List<EmcProjectBean> projectList, boolean reject, String rejectReason) {
        List<EmcProjectBean> validProjectList = projectList.stream().filter(project -> !ObjectUtils.isEmpty(project.getErpGbId())
                && !ObjectUtils.isEmpty(project.getErpReviewId())).collect(Collectors.toList());
        // 如果不是ERP 推送的任务，不发送审核结果
        if (validProjectList.isEmpty()) {
            return;
        }

        String merchantId = getMerchantId();
        Long erpUserId = getErpUserId();
        validProjectList.forEach(project -> {
            MidContractReviewAuditsDto reviewAuditsDto = new MidContractReviewAuditsDto();
            reviewAuditsDto.setMerchantId(merchantId);
            reviewAuditsDto.setOperatorGbId(erpUserId);
            reviewAuditsDto.setReviewId(project.getErpReviewId());
            reviewAuditsDto.setState(UserContractReviewAuditStatus.PASSED.getCode());

            // 如果是退回，则需要填写退回原因
            if (reject) {
                logger.info("项目{}方案被拒绝原因为：{}", project.getProjNo(), rejectReason);
                reviewAuditsDto.setFailReason(rejectReason);
                reviewAuditsDto.setState(UserContractReviewAuditStatus.REJECTED.getCode());
            }

            String jsonStr = JSONObject.toJSONString(reviewAuditsDto);
            logger.info("项目{}推送方案审核结果参数：{}", project.getProjNo(), jsonStr);
            // 通过远程调用模块记录请求参数和请求返回结果
            String result = remoteCallRequestService.processSyncRequest("contractsReview", jsonStr);
            logger.info("项目{}推送方案审核返回结果：{}", project.getProjNo(), result);
            RestResult<Object> restResult = JSON.parseObject(result, new TypeReference<RestResult<Object>>(){});

            if (StringUtils.equals(restResult.getResultCode(), ErpConstant.SUCCESS_CODE)) {
                logger.info("项目{}推送方案审核结果成功", project.getProjNo());
            }/* else {
                throw new UnexpectedException(restResult.getMessage());
            }*/
        });
    }

    @Override
    public String reviewOrderContractToErp(String jsonStr) {
        return HttpClientManager.getInstance().sendHttpPost(getErpServiceUrl() + ErpConstant.REVIEW_ORDER_CONTRACT_INTERFACE, jsonStr);
    }

    @Override
    public void pushOrderChange(RestJsonWrapperBean wrapper) {
        // 暂时不做
    }

    public String getMerchantId() {
        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
        CoreOrgBean parentOrg = coreOrgService.selectById(parentOrgId);

        return parentOrg.getExt$Item("erpMerchantId");
    }

    private Long getErpUserId() {
        String loginUserId = LocalContextHelper.getLoginUserId();
        EmcEmpBean emp = emcEmpService.selectById(loginUserId);

        return emp.getErpGbId();
    }

    private String getErpServiceUrl() {
        String merchantId = getMerchantId();
        String constantKey = StringUtils.isEmpty(merchantId) ? "ERP_SERVICE_URL" : "ERP_SERVICE_URL_" + merchantId;
        return Optional.ofNullable(ApplicationContextHelper.getConstantValue(constantKey)).orElse(ErpConstant.ERP_BASE_URL);
    }
}
