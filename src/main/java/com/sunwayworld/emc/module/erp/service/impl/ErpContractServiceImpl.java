package com.sunwayworld.emc.module.erp.service.impl;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TimerTask;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.projectnode.ProjectNodeUtils;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.PfProjectService;
import com.sunwayworld.emc.module.erp.bean.RestResult;
import com.sunwayworld.emc.module.erp.bean.UserContractReviewAuditStatus;
import com.sunwayworld.emc.module.erp.bean.contract.AttachVO;
import com.sunwayworld.emc.module.erp.bean.contract.MidAddressDTO;
import com.sunwayworld.emc.module.erp.bean.contract.MidPlanContractChangeApplyDTO;
import com.sunwayworld.emc.module.erp.bean.contract.MidPlanContractDetailReq;
import com.sunwayworld.emc.module.erp.bean.contract.MidPlanContractToAttachVO;
import com.sunwayworld.emc.module.erp.bean.contract.MidPlanContractToContractVO;
import com.sunwayworld.emc.module.erp.bean.contract.MidPlanContractToDelegateCompanyVO;
import com.sunwayworld.emc.module.erp.bean.contract.MidPlanContractToServerVO;
import com.sunwayworld.emc.module.erp.bean.contract.MidPlanContractToSplitVO;
import com.sunwayworld.emc.module.erp.bean.contract.MidPlanContractToUserContractDTO;
import com.sunwayworld.emc.module.erp.bean.contract.MidPlanContractVO;
import com.sunwayworld.emc.module.erp.bean.contract.PlanContractCancelReq;
import com.sunwayworld.emc.module.erp.bean.contract.PushAuditUserContractReq;
import com.sunwayworld.emc.module.erp.constant.ErpConstant;
import com.sunwayworld.emc.module.erp.service.ErpContractService;
import com.sunwayworld.emc.module.erp.service.ErpFileService;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcContractBean;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcSplitContractBean;
import com.sunwayworld.emc.module.mdm.contract.service.EmcContractService;
import com.sunwayworld.emc.module.mdm.contract.service.EmcSplitContractService;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpBean;
import com.sunwayworld.emc.module.mdm.emp.emp.service.EmcEmpService;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.executor.ExecutorHelper;
import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.io.file.FileRepresentation;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnParameterDTO;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.cloud.CoreFileCloudService;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallRequestBean;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallResponseBean;
import com.sunwayworld.module.sys.remotecall.service.CoreRemoteCallRequestService;
import com.sunwayworld.module.sys.remotecall.service.CoreRemoteCallResponseService;

/**
 * @author zhengxy@sunwayworld.com 2023年11月06日
 */
@Repository
public class ErpContractServiceImpl implements ErpContractService {

    private static final Logger logger = LogManager.getLogger(ErpContractServiceImpl.class);

    @Autowired
    private EmcContractService contractService;

    @Autowired
    private CoreFileCloudService fileCloudService;

    @Autowired
    private CoreFileService coreFileService;

    @Autowired
    private CoreRemoteCallRequestService remoteCallRequestService;

    @Autowired
    private CoreRemoteCallResponseService remoteCallResponseService;

    @Autowired
    private CoreOrgService orgService;

    @Autowired
    private EmcEmpService emcEmpService;

    @Autowired
    private CoreUserService coreUserService;

    @Autowired
    private ErpFileService erpFileService;

    @Autowired
    private EmcSplitContractService splitContractService;

    @Autowired
    private PfProjectService projectService;

    @Override
    public RestResult<Boolean> pushPlanContract(MidPlanContractVO planContract) {
        logger.info("[Erp-Push-Plan-Contract]=======================");
        String requestJson = JSON.toJSONString(planContract);
        logger.info(requestJson);

        String merchantId = planContract.getMerchantId();
        if (Objects.isNull(merchantId)) {
            RestResult<Boolean> restResult = new RestResult<>("150", "该计划合同所属商户未定义");
            createRemoteCall("pushPlanContract", requestJson, JSON.toJSONString(restResult));

            return restResult;
        }

        List<MidPlanContractToServerVO> servers = planContract.getServers();
        if (Objects.isNull(servers) || servers.isEmpty()) {
            RestResult<Boolean> restResult = new RestResult<>("151", "该计划合同服务方信息未定义");
            createRemoteCall("pushPlanContract", requestJson, JSON.toJSONString(restResult));

            return restResult;
        }

        setUserLogin(merchantId);
        if (StringUtils.isEmpty(LocalContextHelper.getLoginUser().getId())) {
            RestResult<Boolean> restResult = new RestResult<>("152", "该商户默认的报价员未在LIMS中维护");
            createRemoteCall("pushPlanContract", requestJson, JSON.toJSONString(restResult));

            return restResult;
        }

        // 判断推送的计划合同之前是否已经推过
        List<EmcContractBean> existedContractList = contractService.getDao().selectListByOneColumnValue(planContract.getPlanContractGbId(), "erpGbId", Collections.singletonList("id"));
        if (!existedContractList.isEmpty()) {
            // 如果合同已存在，但是合同没有下过单，允许重复推送
            List<Long> existedContractIdList = existedContractList.stream().map(EmcContractBean::getId).collect(Collectors.toList());
            List<EmcSplitContractBean> existedSplitContractList = splitContractService.getDao().selectListByOneColumnValues(existedContractIdList, "contractId", Arrays.asList("id", "contractId"));
            List<Long> existedSplitContractIdList = existedSplitContractList.stream().map(EmcSplitContractBean::getId).collect(Collectors.toList());
            List<EmcProjectBean> existedProjectList = projectService.getDao().selectListByOneColumnValues(existedSplitContractIdList, "splitContractId", Arrays.asList("id", "splitContractId"));

            if (!existedProjectList.isEmpty()) {
                RestResult<Boolean> restResult = new RestResult<>("153", "该计划合同在LIMS中已存在，请勿重复推送");
                createRemoteCall("pushPlanContract", requestJson, JSON.toJSONString(restResult));

                return restResult;
            }

            // 删除已经存在的合同和拆分子合同
            contractService.getDao().deleteByIdList(existedContractIdList);
            splitContractService.getDao().deleteByIdList(existedSplitContractIdList);
        }

        EmcContractBean newContract = new EmcContractBean();
        newContract.setId(ApplicationContextHelper.getNextIdentity());
        newContract.setContractNo(planContract.getPlanContractNumber());
        newContract.setErpGbId(planContract.getPlanContractGbId());

        List<MidPlanContractToDelegateCompanyVO> delegateCompanies = planContract.getDelegateCompanies();
        if (!delegateCompanies.isEmpty()) {
            MidPlanContractToDelegateCompanyVO delegateCompany = delegateCompanies.get(0);
            newContract.setExt$Item("ENTRUSTORGNAME", delegateCompany.getDelegateCompany());
            newContract.setExt$Item("ENTRUSTORGADDRESS", delegateCompany.getDelegateCompanyAddr());
            newContract.setExt$Item("ENTRUSTORGPHONE", delegateCompany.getPhoneNum());
            newContract.setExt$Item("ENTRUSTOR", delegateCompany.getContactName());
        }

        MidPlanContractToServerVO basicServer = servers.get(0);
        newContract.setExt$Item("CONTRACTORNAME", planContract.getCompanyName());
        newContract.setExt$Item("CONTRACTORPHONE", basicServer.getServerPhone());
        MidAddressDTO serverAddr = basicServer.getServerAddr();
        if (Objects.nonNull(serverAddr)) {
            newContract.setExt$Item("CONTRACTORADDRESS", serverAddr.getAddress());
        }
        newContract.setSalerName(basicServer.getServerContact());
        newContract.setSalerPhone(basicServer.getServerPhone());

        newContract.setExt$Item("contractCategory", ErpConstant.PLAN_CONTRACT_CATEGORY);
        newContract.setExt$Item("orgId", LocalContextHelper.getLoginUser().getExt$Item("parentOrgId"));

        contractService.getDao().insert(newContract);

        // 拆分子合同
        createSplitContract(planContract, newContract);

        // 下载并保存合同附件
        downloadAndSaveContractAttach(planContract, newContract.getId());

        RestResult<Boolean> restResult = new RestResult<>("0", "success");
        createRemoteCall("pushPlanContract", requestJson, JSON.toJSONString(restResult));

        return restResult;
    }

    @Override
    public RestResult<Boolean> pushPlanContractChange(MidPlanContractChangeApplyDTO contractChange) {
        logger.info("[Erp-Push-Plan-Contract-Change]=======================");
        String requestJson = JSON.toJSONString(contractChange);
        logger.info(requestJson);

        Long planContractGbId = contractChange.getPlanContractGbId();
        List<EmcContractBean> planContractList = contractService.getDao().selectListByOneColumnValue(planContractGbId, "erpGbId");
        if (planContractList.isEmpty()) {
            RestResult<Boolean> restResult = new RestResult<>("154", "该计划合同在LIMS中不存在");
            createRemoteCall("rePushPlanContractChange", requestJson, JSON.toJSONString(restResult));

            return restResult;
        }

        List<MidPlanContractToUserContractDTO> erpSplitContractList = contractChange.getSplit();
        boolean invalid = erpSplitContractList.stream().anyMatch(sc -> StringUtils.isEmpty(sc.getBusinessOpportunityName())
                || StringUtils.isEmpty(sc.getProductionLineName())
                || StringUtils.isEmpty(sc.getIndustryName()));
        if (invalid) {
            RestResult<Boolean> restResult = new RestResult<>("155", "该计划合同下的拆分子合同数据不合法");
            createRemoteCall("rePushPlanContractChange", requestJson, JSON.toJSONString(restResult));

            return restResult;
        }

        // erp 要删除的子合同，需要判断是否已在lims中创建了项目
        List<String> needDeleteSplitContractIdList = erpSplitContractList.stream().filter(sc -> !ObjectUtils.isEmpty(sc.getPlanContractBindItemGbId()) && ObjectUtils.equals(sc.getContractStatus(), 4)).map(MidPlanContractToUserContractDTO::getPlanContractBindItemGbId).map(String::valueOf).collect(Collectors.toList());

        Long contractId = planContractList.get(0).getId();
        List<EmcSplitContractBean> splitContractList = splitContractService.getDao().selectListByOneColumnValue(contractId, "contractId");
        List<EmcSplitContractBean> deleteSplitContractList = splitContractList.stream().filter(sc -> needDeleteSplitContractIdList.contains(sc.getErpGbId())).collect(Collectors.toList());
        List<Long> deleteSplitContractIdList = deleteSplitContractList.stream().map(EmcSplitContractBean::getId).collect(Collectors.toList());
        List<EmcProjectBean> existedProjectList = projectService.getDao().selectListByOneColumnValues(deleteSplitContractIdList, "splitContractId", Arrays.asList("id", "splitContractId"));
        if (!existedProjectList.isEmpty()) {
            existedProjectList.forEach(project -> {
                project.setBeforeStatus(project.getStatus());
                project.setStatus(EmcBizConstant.PROJECT_STATUS_CANCEL);
                project.setCancelReason("ERP拆分子合同作废");
            });
            projectService.getDao().update(existedProjectList, "beforeStatus", "status", "cancelReason");
        }

        setUserLogin(contractChange.getMerchantId());

        // 找出需要更新的子合同
        List<EmcSplitContractBean> existedSplitContractList = splitContractList.stream().filter(sc -> !ObjectUtils.isEmpty(sc.getErpGbId())).collect(Collectors.toList());
        existedSplitContractList.forEach(esc -> erpSplitContractList.stream().filter(sc -> StringUtils.equals(esc.getErpGbId(), String.valueOf(sc.getPlanContractBindItemGbId()))).findFirst().ifPresent(sc -> {
            esc.setPrice(sc.getPrice());
            esc.setBusinessOpportunityGbId(String.valueOf(sc.getBusinessOpportunityGbId()));
            esc.setBusinessOpportunityName(sc.getBusinessOpportunityName());
            esc.setIndustryGbId(String.valueOf(sc.getIndustryGbId()));
            esc.setIndustryName(sc.getIndustryName());
            esc.setProductionLineGbId(String.valueOf(sc.getProductionLineGbId()));
            esc.setProductionLineName(sc.getProductionLineName());
            esc.setStatus(String.valueOf(sc.getContractStatus()));
        }));
        splitContractService.getDao().update(existedSplitContractList, "price", "businessOpportunityGbId", "businessOpportunityName",
                "industryGbId", "industryName", "productionLineGbId", "productionLineName", "status");

        // 找出新增的拆分子合同
        List<String> existedErpGbIdList = existedSplitContractList.stream().map(EmcSplitContractBean::getErpGbId).collect(Collectors.toList());
        List<MidPlanContractToUserContractDTO> newErpSplitContractList = erpSplitContractList.stream().filter(sc -> ObjectUtils.isEmpty(sc.getPlanContractBindItemGbId()) || !existedErpGbIdList.contains(String.valueOf(sc.getPlanContractBindItemGbId()))).collect(Collectors.toList());
        if (!newErpSplitContractList.isEmpty()) {
            List<EmcSplitContractBean> insertSplitContractList = new ArrayList<>(newErpSplitContractList.size());
            newErpSplitContractList.forEach(sc -> {
                EmcSplitContractBean newSplitContract = new EmcSplitContractBean();
                newSplitContract.setId(ApplicationContextHelper.getNextIdentity());
                newSplitContract.setContractId(contractId);
                newSplitContract.setPrice(sc.getPrice());
                newSplitContract.setBusinessOpportunityGbId(String.valueOf(sc.getBusinessOpportunityGbId()));
                newSplitContract.setBusinessOpportunityName(sc.getBusinessOpportunityName());
                newSplitContract.setIndustryGbId(String.valueOf(sc.getIndustryGbId()));
                newSplitContract.setIndustryName(sc.getIndustryName());
                newSplitContract.setProductionLineGbId(String.valueOf(sc.getProductionLineGbId()));
                newSplitContract.setProductionLineName(sc.getProductionLineName());
                newSplitContract.setStatus("0"); // 此时还未获取拆分子合同的ID，不能用于下单
                insertSplitContractList.add(newSplitContract);
            });

            splitContractService.getDao().insert(insertSplitContractList);
        }

        // 创建异步延时线程池，延时去获取新增拆分子合同的ID
        delayFetchSplitContractGbId(contractId, planContractGbId, existedErpGbIdList);

        RestResult<Boolean> restResult = new RestResult<>("0", "success");
        createRemoteCall("rePushPlanContractChange", requestJson, JSON.toJSONString(restResult));

        return restResult;
    }

    @Override
    public RestResult<Boolean> pushAuditUserContract(PushAuditUserContractReq auditUserContractReq) {
        logger.info("[Erp-Push-Audit-User-Contract]=======================");
        String requestJson = JSON.toJSONString(auditUserContractReq);
        logger.info(requestJson);

        String merchantId = auditUserContractReq.getMerchantId();
        if (Objects.isNull(merchantId)) {
            RestResult<Boolean> restResult = new RestResult<>("108", "该合同订单所属商户未定义");
            createRemoteCall("pushAuditUserContract", requestJson, JSON.toJSONString(restResult));

            return restResult;
        }

        List<String> orderGbIds = auditUserContractReq.getOrderGbIds();
        if (Objects.isNull(orderGbIds) || orderGbIds.isEmpty()) {
            RestResult<Boolean> restResult = new RestResult<>("109", "该合同下的订单为空");
            createRemoteCall("pushAuditUserContract", requestJson, JSON.toJSONString(restResult));

            return restResult;
        }

        List<EmcProjectBean> erpProjectList = projectService.getDao().selectListByOneColumnValues(orderGbIds, "erpGbId");
        if (erpProjectList.isEmpty()) {
            RestResult<Boolean> restResult = new RestResult<>("110", "该订单在LIMS中不存在");
            createRemoteCall("pushAuditUserContract", requestJson, JSON.toJSONString(restResult));

            return restResult;
        }

        // 如果项目不在方案审核，提示状态异常
        boolean invalidStatus = erpProjectList.stream().anyMatch(project -> !EmcBizConstant.PROJECT_STATUS_SCHEMEAUDIT.equals(project.getStatus()));
        if (invalidStatus) {
            RestResult<Boolean> restResult = new RestResult<>("111", "该订单在LIMS中状态异常");
            createRemoteCall("pushAuditUserContract", requestJson, JSON.toJSONString(restResult));

            return restResult;
        }

        setUserLogin(merchantId);
        Integer auditStatus = auditUserContractReq.getAuditStatus();
        List<Long> erpProjectIdList = erpProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());

        if (UserContractReviewAuditStatus.PASSED.getCode() == auditStatus) {
            // 将项目从方案审核提交
            try {
                CoreBpmnParameterDTO bpmnParameter = new CoreBpmnParameterDTO();
                bpmnParameter.setComment(ErpConstant.ERP_AUDIT_PASS);
                projectService.completeTask(erpProjectIdList, bpmnParameter);
            } catch (Exception e) {
                logger.error(e.getMessage());
                RestResult<Boolean> restResult = new RestResult<>("112", "LIMS中维护的报价员没有审核操作权限");
                createRemoteCall("pushAuditUserContract", requestJson, JSON.toJSONString(restResult));

                return restResult;
            }

            // 生成主业务数据
            projectService.createProjectBpData(erpProjectList);
        } else if (UserContractReviewAuditStatus.REJECTED.getCode() == auditStatus) {
            // 将项目从方案审核退回
            try {
                CoreBpmnParameterDTO bpmnParameter = new CoreBpmnParameterDTO();
                bpmnParameter.setComment(String.format(ErpConstant.ERP_AUDIT_REJECT, auditUserContractReq.getRejectReason()));

                projectService.rejectTask(erpProjectIdList, bpmnParameter);
            } catch (Exception e) {
                logger.error(e.getMessage());
                RestResult<Boolean> restResult = new RestResult<>("112", "LIMS中维护的报价员没有审核操作权限");
                createRemoteCall("pushAuditUserContract", requestJson, JSON.toJSONString(restResult));

                return restResult;
            }

            EmcProjectBean updateProject = new EmcProjectBean();
            updateProject.setStatus(EmcBizConstant.PROJECT_STATUS_CREATE);

            projectService.getDao().updateByIds(updateProject, erpProjectIdList, "STATUS");
            ProjectNodeUtils.updateNodes(erpProjectIdList);
        }

        RestResult<Boolean> restResult = new RestResult<>("0", "success");
        createRemoteCall("pushAuditUserContract", requestJson, JSON.toJSONString(restResult));

        return restResult;
    }

    @Override
    public RestResult<Boolean> pushPlanContractCancel(PlanContractCancelReq planContractCancelReq) {
        logger.info("[Erp-Push-Contract-Cancel]=======================");
        String requestJson = JSON.toJSONString(planContractCancelReq);
        logger.info(requestJson);

        String merchantId = planContractCancelReq.getMerchantId();
        if (Objects.isNull(merchantId)) {
            RestResult<Boolean> restResult = new RestResult<>("108", "该合同订单所属商户未定义");
            createRemoteCall("pushPlanContractCancel", requestJson, JSON.toJSONString(restResult));

            return restResult;
        }

        Long planContractGbId = planContractCancelReq.getPlanContractGbId();
        List<EmcContractBean> planContractList = contractService.getDao().selectListByOneColumnValue(planContractGbId, "erpGbId");
        if (planContractList.isEmpty()) {
            RestResult<Boolean> restResult = new RestResult<>("154", "该计划合同在LIMS中不存在");
            createRemoteCall("pushPlanContractCancel", requestJson, JSON.toJSONString(restResult));

            return restResult;
        }

        setUserLogin(merchantId);
        if (StringUtils.isEmpty(LocalContextHelper.getLoginUser().getId())) {
            RestResult<Boolean> restResult = new RestResult<>("152", "该商户默认的报价员未在LIMS中维护");
            createRemoteCall("pushPlanContractCancel", requestJson, JSON.toJSONString(restResult));

            return restResult;
        }

        // 查询该合同下的拆分合同是否创建过任务
        List<Long> contractIdList = planContractList.stream().map(EmcContractBean::getId).collect(Collectors.toList());
        List<EmcSplitContractBean> splitContractList = splitContractService.getDao().selectListByOneColumnValues(contractIdList, "contractId", Collections.singletonList("id"));
        List<Long> splitContractIdList = splitContractList.stream().map(EmcSplitContractBean::getId).collect(Collectors.toList());
        List<EmcProjectBean> existedProjectList = projectService.getDao().selectListByOneColumnValues(splitContractIdList, "splitContractId", Arrays.asList("id", "splitContractId", "status"));
        existedProjectList.forEach(project -> {
            project.setBeforeStatus(project.getStatus());
            project.setStatus(EmcBizConstant.PROJECT_STATUS_CANCEL);
            project.setCancelReason("计划合同终止");
        });
        projectService.getDao().update(existedProjectList, "beforeStatus", "status", "cancelReason");

        // 拆分子合同作废
        splitContractList.forEach(sc -> sc.setStatus("4"));
        splitContractService.getDao().update(splitContractList, "status");

        // 更新合同状态, 2-作废
        planContractList.forEach(c -> c.setStatus("2"));
        contractService.getDao().update(planContractList, "status");

        // 下载计划合同作废的附件
        List<AttachVO> attachList = planContractCancelReq.getAttach();
        if (Objects.nonNull(attachList) && !attachList.isEmpty()) {
            EmcContractBean planContract = planContractList.get(0);
            downloadAttach(attachList, planContract.getId());
        }

        RestResult<Boolean> restResult = new RestResult<>("0", "success");
        createRemoteCall("pushPlanContractCancel", requestJson, JSON.toJSONString(restResult));

        return restResult;
    }

    @Override
    public CoreOrgBean getMerchantOrg() {
        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");

        return orgService.selectById(parentOrgId);
    }

    /**
     * 创建拆分子合同
     * @param planContract ERP计划合同
     * @param contract LIMST合同
     */
    private void createSplitContract(MidPlanContractVO planContract, EmcContractBean contract) {
        MidPlanContractToSplitVO split = planContract.getSplit();
        if (Objects.isNull(split)) {
            return;
        }

        List<MidPlanContractToContractVO> childContractList = split.getContracts();
        List<EmcSplitContractBean> splitContractList = new ArrayList<>(childContractList.size());

        childContractList.forEach(childContract -> {
            EmcSplitContractBean splitContract = new EmcSplitContractBean();
            splitContract.setId(ApplicationContextHelper.getNextIdentity());
            splitContract.setContractId(contract.getId());
            splitContract.setBusinessOpportunityGbId(childContract.getBusinessOpportunityGbId() + "");
            splitContract.setBusinessOpportunityName(childContract.getBusinessOpportunityName());
            splitContract.setProductionLineGbId(childContract.getProductionLineGbId() + "");
            splitContract.setProductionLineName(childContract.getProductionLineName());
            splitContract.setIndustryGbId(childContract.getIndustryGbId() + "");
            splitContract.setIndustryName(childContract.getIndustryName());
            splitContract.setErpGbId(childContract.getContractGbId() + "");
            splitContract.setPrice(childContract.getPrice());
            splitContract.setStatus(childContract.getContractStatus() + "");
            splitContractList.add(splitContract);
        });

        splitContractService.getDao().insert(splitContractList);
    }

    /**
     * 下载并保存合同附件
     * @param planContract ERP计划合同
     * @param contractId LIMS合同ID
     */
    private void downloadAndSaveContractAttach(MidPlanContractVO planContract, Long contractId) {
        MidPlanContractToAttachVO contractAttach = planContract.getAttach();
        if (Objects.isNull(contractAttach)) {
            logger.info("计划合同{}没有附件信息", planContract.getPlanContractNumber());
            return;
        }

        List<AttachVO> attachList = contractAttach.getAttach();
        downloadAttach(attachList, contractId);
    }

    @Override
    public void downloadAttach(List<AttachVO> attachList, Long contractId) {
        List<CoreFileBean> insertFileList = new ArrayList<>(attachList.size());
        attachList.forEach(attach -> {
            File tempFile = FileHelper.createTempFile(attach.getName(), 5000L);
            // 使用POST请求下载文件
            erpFileService.downloadFile(attach.getUrl(), tempFile);

            CoreFileBean coreFile = new CoreFileBean();
            coreFile.setId(ApplicationContextHelper.getNextIdentity());
            coreFile.setTargetId("T_EMC_CONTRACT$" + contractId);
            coreFile.setName(attach.getName());
            coreFile.setFileExt(FileUtils.getFileExtension(attach.getName()));
            coreFile.setScope(FileScope.export.name());
            coreFile.setTimes(0);
            coreFile.setDownloadable(Constant.YES);
            coreFile.setCreatedTime(LocalDateTime.now());
            coreFile.setBizCategory("");
            coreFile.setSize(tempFile.length());
            coreFile.setMd5Name(EncryptUtils.MD5Encrypt(coreFile.getId() + "$" + attach.getName()));

            fileCloudService.archive(FileRepresentation.of(coreFile), tempFile);
            insertFileList.add(coreFile);
        });

        coreFileService.getDao().insert(insertFileList);
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

    /**
     * 设置登录用户，默认为报价员对应的用户
     * @param merchantId 商户ID
     */
    private void setUserLogin(String merchantId) {
        // 设置当前登录用户，用于记录后续的操作
        List<CoreOrgBean> companyOrgList = orgService.getDao().selectListByOneColumnValue("company", "ORGCATEGORY");
        companyOrgList.stream().filter(org -> StringUtils.equals(merchantId + "", org.getExt$Item("erpMerchantId"))).findFirst().ifPresent(org -> {
            EmcEmpBean erpEmp = emcEmpService.selectFirstByFilter(SearchFilter.instance().match("orgId", org.getId()).filter(MatchPattern.SEQ).match("QUOTER", Constant.YES).filter(MatchPattern.SEQ));
            if (Objects.nonNull(erpEmp)) {
                CoreUserBean coreUser = coreUserService.selectById(erpEmp.getId());
                LocalContextHelper.setUserLogin(new LoginUser(coreUser.getId(), coreUser.getPassword()));
            }
        });
    }

    private String getErpServiceUrl() {
        CoreOrgBean merchantOrg = getMerchantOrg();
        String merchantId = merchantOrg.getExt$Item("erpMerchantId");
        String constantKey = StringUtils.isEmpty(merchantId) ? "ERP_SERVICE_URL" : "ERP_SERVICE_URL_" + merchantId;
        return Optional.ofNullable(ApplicationContextHelper.getConstantValue(constantKey)).orElse(ErpConstant.ERP_BASE_URL);
    }

    private void delayFetchSplitContractGbId(Long contractId, Long planContractGbId, List<String> existedErpGbIdList) {
        String url = getErpServiceUrl() + ErpConstant.FETCH_PLAN_CONTRACT_DETAIL_INTERFACE;
        logger.info("获取计划合同详情地址 -> {}", url);
        MidPlanContractDetailReq planContractDetailReq = new MidPlanContractDetailReq();
        planContractDetailReq.setPlanContractGbId(planContractGbId);
        String reqJson = JSON.toJSONString(planContractDetailReq);
        logger.info("获取计划合同详情请求参数 -> {}", reqJson);

        // 延时1分钟去ERP获取新增的拆分子合同ID
        ExecutorHelper.submit(new TimerTask() {
            @Override
            public void run() {
                String result = HttpClientManager.getInstance().sendHttpPost(url, reqJson);
                logger.info("获取计划合同详情返回结果 -> {}", result);
                createRemoteCall("fetchPlanContractDetail", reqJson, result);

                RestResult<MidPlanContractVO> restResult = JSON.parseObject(result, new TypeReference<RestResult<MidPlanContractVO>>(){});

                if (StringUtils.equals(restResult.getResultCode(), ErpConstant.SUCCESS_CODE)
                        && !ObjectUtils.isEmpty(restResult.getResultData())) {

                    MidPlanContractVO planContract = restResult.getResultData();
                    MidPlanContractToSplitVO splitVo = planContract.getSplit();
                    if (Objects.nonNull(splitVo)) {
                        List<EmcSplitContractBean> splitContractList = splitContractService.getDao().selectListByOneColumnValue(contractId, "contractId");
                        List<EmcSplitContractBean> deleteSplitContractList = splitContractList.stream().filter(sc -> !existedErpGbIdList.contains(sc.getErpGbId())).collect(Collectors.toList());

                        List<MidPlanContractToContractVO> erpSplitContractList = splitVo.getContracts();
                        List<MidPlanContractToContractVO> newErpSplitContractList = erpSplitContractList.stream().filter(sc -> !existedErpGbIdList.contains(String.valueOf(sc.getContractGbId())) && sc.getContractStatus() != 4).collect(Collectors.toList());

                        List<EmcSplitContractBean> insertSplitContractList = new ArrayList<>(newErpSplitContractList.size());
                        newErpSplitContractList.forEach(sc -> {
                            EmcSplitContractBean newSplitContract = new EmcSplitContractBean();
                            newSplitContract.setId(ApplicationContextHelper.getNextIdentity());
                            newSplitContract.setContractId(contractId);
                            newSplitContract.setPrice(sc.getPrice());
                            newSplitContract.setErpGbId(String.valueOf(sc.getContractGbId()));
                            newSplitContract.setBusinessOpportunityGbId(String.valueOf(sc.getBusinessOpportunityGbId()));
                            newSplitContract.setBusinessOpportunityName(sc.getBusinessOpportunityName());
                            newSplitContract.setIndustryGbId(String.valueOf(sc.getIndustryGbId()));
                            newSplitContract.setIndustryName(sc.getIndustryName());
                            newSplitContract.setProductionLineGbId(String.valueOf(sc.getProductionLineGbId()));
                            newSplitContract.setProductionLineName(sc.getProductionLineName());
                            newSplitContract.setStatus(String.valueOf(sc.getContractStatus()));
                            insertSplitContractList.add(newSplitContract);
                        });

                        // 删除之前新增的
                        splitContractService.getDao().deleteBy(deleteSplitContractList, "id");
                        splitContractService.getDao().insert(insertSplitContractList);
                    }
                }
            }
        }, 60000);
    }

}
