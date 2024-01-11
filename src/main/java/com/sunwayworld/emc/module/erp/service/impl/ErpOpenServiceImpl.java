package com.sunwayworld.emc.module.erp.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.alibaba.fastjson.JSON;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcFolderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderItemGroupChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjOrderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjSchemeChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjectChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcFolderChangeService;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcOrderChangeService;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcOrderItemGroupChangeService;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcProjOrderChangeService;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcProjSchemeChangeService;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcProjectChangeService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjOrderService;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjSchemeService;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportSendInfoBean;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportSendInfoService;
import com.sunwayworld.emc.module.erp.bean.ConfirmOrderChangeReq;
import com.sunwayworld.emc.module.erp.bean.ContractCustomTemplateInfo;
import com.sunwayworld.emc.module.erp.bean.EnvOrderExtraInfoEntity;
import com.sunwayworld.emc.module.erp.bean.EnvOrderSampleExtraInfoEntity;
import com.sunwayworld.emc.module.erp.bean.EnvSampleSkuSamplingTypeEntity;
import com.sunwayworld.emc.module.erp.bean.ErpProjectCompareVO;
import com.sunwayworld.emc.module.erp.bean.HJOrderExtraInfoModelExt;
import com.sunwayworld.emc.module.erp.bean.MidCustomTemplateVO;
import com.sunwayworld.emc.module.erp.bean.MidEnumVO;
import com.sunwayworld.emc.module.erp.bean.MidFieldRelationVO;
import com.sunwayworld.emc.module.erp.bean.MidFieldVO;
import com.sunwayworld.emc.module.erp.bean.MidOrderAdditionalReportDTO;
import com.sunwayworld.emc.module.erp.bean.MidOrderDetailVO;
import com.sunwayworld.emc.module.erp.bean.MidSampleDetailVO;
import com.sunwayworld.emc.module.erp.bean.MidSkuDetailVo;
import com.sunwayworld.emc.module.erp.bean.MidUserContractVO;
import com.sunwayworld.emc.module.erp.bean.OrderChangeDetailResp;
import com.sunwayworld.emc.module.erp.bean.OrderChangeDiffResp;
import com.sunwayworld.emc.module.erp.bean.OrderChangeSampleInfo;
import com.sunwayworld.emc.module.erp.bean.OrderReviewReq;
import com.sunwayworld.emc.module.erp.bean.RestResult;
import com.sunwayworld.emc.module.erp.bean.TemplateFieldsInfo;
import com.sunwayworld.emc.module.erp.bean.contract.AttachVO;
import com.sunwayworld.emc.module.erp.constant.ErpConstant;
import com.sunwayworld.emc.module.erp.service.ErpCompareService;
import com.sunwayworld.emc.module.erp.service.ErpContractService;
import com.sunwayworld.emc.module.erp.service.ErpOpenService;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean.EmcBizCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.service.EmcBizCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.PfCapacityItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorSchemeService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorTmplService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcContractBean;
import com.sunwayworld.emc.module.mdm.contract.service.EmcContractService;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpBean;
import com.sunwayworld.emc.module.mdm.emp.emp.service.EmcEmpService;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.UnsupportedException;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnParameterDTO;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.service.CoreCodeService;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallRequestBean;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallResponseBean;
import com.sunwayworld.module.sys.remotecall.service.CoreRemoteCallRequestService;
import com.sunwayworld.module.sys.remotecall.service.CoreRemoteCallResponseService;

/**
 * @author zhengxy@sunwayworld.com 2023年08月11日
 */
@Repository
public class ErpOpenServiceImpl implements ErpOpenService {

    private static final Logger logger = LogManager.getLogger(ErpOpenServiceImpl.class);

    @Autowired
    private CoreOrgService orgService;

    @Autowired
    private EmcProjectService projectService;

    @Autowired
    private EmcContractService contractService;

    @Autowired
    private EmcFolderService folderService;

    @Autowired
    private EmcOrderService orderService;

    @Autowired
    private EmcMonitorTmplService monitorTmplService;

    @Autowired
    private EmcMonitorSchemeService monitorSchemeService;

    @Autowired
    private EmcOrderItemGroupService orderItemGroupService;

    @Autowired
    private EmcOrderItemService orderItemService;

    @Autowired
    private EmcProjSchemeService projSchemeService;

    @Autowired
    private EmcProjOrderService projOrderService;

    @Autowired
    private CoreRemoteCallRequestService remoteCallRequestService;

    @Autowired
    private CoreRemoteCallResponseService remoteCallResponseService;

    @Autowired
    private EmcBizCategoryService bizCategoryService;

    @Autowired
    private EmcEmpService emcEmpService;

    @Autowired
    private CoreUserService coreUserService;

    @Autowired
    private EmcProjectChangeService projectChangeService;

    @Autowired
    private EmcProjSchemeChangeService projSchemeChangeService;

    @Autowired
    private EmcProjOrderChangeService projOrderChangeService;

    @Autowired
    private EmcFolderChangeService folderChangeService;

    @Autowired
    private EmcOrderChangeService orderChangeService;

    @Autowired
    private EmcOrderItemGroupChangeService orderItemGroupChangeService;

    @Autowired
    private ErpCompareService compareService;

    @Autowired
    private EmcReportSendInfoService reportSendInfoService;

    @Autowired
    private PfCapacityItemGroupService capacityItemGroupService;

    @Autowired
    private EmcGroupItemService groupItemService;

    @Autowired
    private CoreCodeService coreCodeService;

    @Autowired
    private ErpContractService erpContractService;

    @Override
    @Transactional
    public RestResult<Boolean> pushOrder(MidOrderDetailVO orderDetail) {
        logger.info("[Erp-Push-Order]=======================");
        String requestJson = JSON.toJSONString(orderDetail);
        logger.info(requestJson);

        CoreRemoteCallRequestBean callRequest = new CoreRemoteCallRequestBean();
        callRequest.setId(ApplicationContextHelper.getNextIdentity());
        callRequest.setConfigId("pushContactOrder");
        callRequest.setRequestData(requestJson);
        callRequest.setStatus(Constant.YES);
        callRequest.setRequestDateTime(LocalDateTime.now());
        remoteCallRequestService.getDao().insert(callRequest);

        CoreRemoteCallResponseBean callResponse = new CoreRemoteCallResponseBean();
        callResponse.setId(ApplicationContextHelper.getNextIdentity());
        callResponse.setRequestId(callRequest.getId());
        callResponse.setResponseDateTime(LocalDateTime.now());

        // 设置登录用户
        setUserLogin(orderDetail.getOrderExtraInfoModelExt().getOrderExtraInfo().getMerchantId() + "", orderDetail.getSaleGbId() + "");
        if (StringUtils.isEmpty(LocalContextHelper.getLoginUser().getId())) {
            RestResult<Boolean> restResult = new RestResult<>("105", "该订单中的跟单员未在LIMS中维护");
            callResponse.setResponseData(JSON.toJSONString(restResult));
            callResponse.setStatus(Constant.YES);
            remoteCallResponseService.getDao().insert(callResponse);

            return restResult;
        }

        Long gbId = orderDetail.getGbId();
        List<EmcProjectBean> existedProjectList = projectService.getDao().selectListByOneColumnValue(gbId, "erpGbId", Arrays.asList("id", "projNo", "processStatus", "sample", "taskDate", "registDate", "PROJCREATEDTIME"));
        List<EmcProjectBean> approveProjectList = existedProjectList.stream().filter(project -> StringUtils.equalsIgnoreCase(project.getProcessStatus(), "approve")).collect(Collectors.toList());
        if (!approveProjectList.isEmpty()) {
            RestResult<Boolean> restResult = new RestResult<>("100", "该订单在LIMS中已存在，请勿重复推送");
            callResponse.setResponseData(JSON.toJSONString(restResult));
            callResponse.setStatus(Constant.YES);
            remoteCallResponseService.getDao().insert(callResponse);

            return restResult;
        }

        // 如果项目在项目登记，则直接删除掉原项目
        List<EmcProjectBean> draftProjectList = existedProjectList.stream().filter(project -> StringUtils.startsWithIgnoreCase(project.getProcessStatus(), "draft")).collect(Collectors.toList());
        if (!draftProjectList.isEmpty()) {
            deleteProject(draftProjectList);
        }

        Map<Long, Pair<String, String>> contractValueMap = parseDynamicFieldValue(orderDetail.getUserContractValue(), orderDetail.getCustomTemplateView());
        Map<Long, Pair<String, String>> orderValueMap = parseDynamicFieldValue(orderDetail.getOrderValue(), orderDetail.getCustomTemplateView());

        EmcProjectBean originalProject = !draftProjectList.isEmpty() ? draftProjectList.get(0) : null;
        EmcProjectBean newProject = createProject(orderDetail, orderValueMap, originalProject);
        EmcContractBean newContract = createContract(orderDetail, newProject);

        newProject.setExt$Item("contractId", newContract.getId() + "");
        newProject.setExt$Item("contractNo", newContract.getContractNo());
        // 新建任务和合同
        projectService.getDao().insert(newProject);
        contractService.getDao().insert(newContract);

        // 采样任务
        if (Constant.YES.equals(newProject.getSample())) {
            createProjectScheme(newProject, orderDetail);
        } else {
            createOrder(newProject, orderDetail);
        }

        // 创建报告发送信息
        createReportSendInfo(newProject, orderDetail);

        // 提交项目到方案审核
        CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
        parameter.setComment("接收合同订单");
        projectService.startProcess(Collections.singletonList(newProject.getId()), parameter);

        RestResult<Boolean> restResult = new RestResult<>("0", "success");
        callResponse.setResponseData(JSON.toJSONString(restResult));
        callResponse.setStatus(Constant.YES);
        remoteCallResponseService.getDao().insert(callResponse);

        // 下载合同附件
        List<AttachVO> attaches = orderDetail.getUserContractAttaches();
        if (Objects.nonNull(attaches) && !attaches.isEmpty()) {
            erpContractService.downloadAttach(attaches, newContract.getId());
        }

        return restResult;
    }

    @Override
    @Transactional
    public RestResult<Boolean> pushOrderChange(OrderChangeDetailResp orderChangeDetail) {
        logger.info("[Erp-Push-Order-Change]=======================");
        String requestJson = JSON.toJSONString(orderChangeDetail);
        logger.info(requestJson);

        // 远程调用数据记录
        CoreRemoteCallRequestBean callRequest = new CoreRemoteCallRequestBean();
        callRequest.setId(ApplicationContextHelper.getNextIdentity());
        callRequest.setConfigId("pushOrderChange");
        callRequest.setRequestData(requestJson);
        callRequest.setStatus(Constant.YES);
        callRequest.setRequestDateTime(LocalDateTime.now());
        remoteCallRequestService.getDao().insert(callRequest);

        CoreRemoteCallResponseBean callResponse = new CoreRemoteCallResponseBean();
        callResponse.setId(ApplicationContextHelper.getNextIdentity());
        callResponse.setRequestId(callRequest.getId());
        callResponse.setResponseDateTime(LocalDateTime.now());

        // 变更后的详情
        OrderChangeDiffResp target = orderChangeDetail.getSource();
        Long orderGbId = target.getOrderGbId();

        List<EmcProjectBean> projectList = projectService.getDao().selectListByOneColumnValue(orderGbId, "erpGbId");
        if (projectList.isEmpty()) {
            RestResult<Boolean> restResult = new RestResult<>("101", "该订单在LIMS中不存在");
            callResponse.setResponseData(JSON.toJSONString(restResult));
            callResponse.setStatus(Constant.YES);
            remoteCallResponseService.getDao().insert(callResponse);

            return restResult;
        }

        // 获取商户ID，根据商户ID反查orgId
        Integer merchantId = orderChangeDetail.getCustomTemplateView().getMerchantCustomTemplateVO().getMerchantId();
        setUserLogin(merchantId + "", orderChangeDetail.getActEngineerGbId() + "");

        EmcProjectBean originalProject = projectList.get(0);
        EmcProjectChangeBean projectChange = createProjectChange(orderChangeDetail, originalProject);
        // 采样任务
        if (Constant.YES.equals(projectChange.getSample())) {
            createProjectSchemeChange(originalProject, projectChange, target);
        } else {
            createOrderChange(originalProject, projectChange, target);
        }

        projectChangeService.getDao().insert(projectChange);

        // 提交到项目变更审核
        CoreBpmnParameterDTO bpmnParameter = new CoreBpmnParameterDTO();
        bpmnParameter.setComment("ERP订单变更");
        projectChangeService.startProcess(Collections.singletonList(projectChange.getId()), bpmnParameter);

        // 操作完后，移除当前登录用户
        LocalContextHelper.removeUserLogin();

        RestResult<Boolean> restResult = new RestResult<>("0", "success");
        callResponse.setResponseData(JSON.toJSONString(restResult));
        callResponse.setStatus(Constant.YES);
        remoteCallResponseService.getDao().insert(callResponse);

        return restResult;
    }

    @Override
    public RestResult<Boolean> pushOrderChangeConfirm(ConfirmOrderChangeReq confirmOrderChangeReq) {
        logger.info("[Erp-Push-Order-Change-Confirm]=======================");
        String requestJson = JSON.toJSONString(confirmOrderChangeReq);
        logger.info(requestJson);

        // 远程调用数据记录
        CoreRemoteCallRequestBean callRequest = new CoreRemoteCallRequestBean();
        callRequest.setId(ApplicationContextHelper.getNextIdentity());
        callRequest.setConfigId("pushOrderChangeConfirm");
        callRequest.setRequestData(requestJson);
        callRequest.setStatus(Constant.YES);
        callRequest.setRequestDateTime(LocalDateTime.now());
        remoteCallRequestService.getDao().insert(callRequest);

        CoreRemoteCallResponseBean callResponse = new CoreRemoteCallResponseBean();
        callResponse.setId(ApplicationContextHelper.getNextIdentity());
        callResponse.setRequestId(callRequest.getId());
        callResponse.setResponseDateTime(LocalDateTime.now());

        // ERP推送订单变更审核结果
        Long gbId = confirmOrderChangeReq.getGbId();
        List<EmcProjectChangeBean> projectChangeList = projectChangeService.getDao().selectListByOneColumnValue(gbId, "erpGbId");
        if (projectChangeList.isEmpty()) {
            RestResult<Boolean> restResult = new RestResult<>("102", "该订单变更在LIMS中不存在");
            callResponse.setResponseData(JSON.toJSONString(restResult));
            callResponse.setStatus(Constant.YES);
            remoteCallResponseService.getDao().insert(callResponse);

            return restResult;
        }

        // 待审核的订单
        List<EmcProjectChangeBean> approveProjectChangeList = projectChangeList.stream().filter(project -> StringUtils.equals(project.getProcessStatus(), "approve")).collect(Collectors.toList());
        if (approveProjectChangeList.isEmpty()) {
            RestResult<Boolean> restResult = new RestResult<>("103", "该订单变更已被审核");
            callResponse.setResponseData(JSON.toJSONString(restResult));
            callResponse.setStatus(Constant.YES);
            remoteCallResponseService.getDao().insert(callResponse);

            return restResult;
        }

        EmcProjectChangeBean targetProjectChange = approveProjectChangeList.get(0);
        Integer discountReviewStatus = targetProjectChange.getDiscountReviewStatus();
        if (Objects.isNull(discountReviewStatus) || discountReviewStatus != ErpConstant.ORDER_CHANGE_DISCOUNT_REVIEW_PASSED) {
            RestResult<Boolean> restResult = new RestResult<>("104", "该订单变更折扣审核未通过");
            callResponse.setResponseData(JSON.toJSONString(restResult));
            callResponse.setStatus(Constant.YES);
            remoteCallResponseService.getDao().insert(callResponse);

            return restResult;
        }

        CoreBpmnParameterDTO parameterDto = new CoreBpmnParameterDTO();
        parameterDto.setComment(confirmOrderChangeReq.getRemark());

        // 设置登录用户
        setUserLogin(confirmOrderChangeReq.getMerchantId(), confirmOrderChangeReq.getOperatorGbId() + "");

        // 订单变更审核通过
        if (Objects.nonNull(confirmOrderChangeReq.getType()) && confirmOrderChangeReq.getType() == 1) {
            projectChangeService.completeTask(Collections.singletonList(targetProjectChange.getId()), parameterDto);
            projectChangeService.changeProject(Collections.singletonList(targetProjectChange));
        } else {
            // 订单变更审核被驳回
            projectChangeService.rejectTask(Collections.singletonList(targetProjectChange.getId()), parameterDto);
        }

        // 操作完后，移除当前登录用户
        LocalContextHelper.removeUserLogin();

        RestResult<Boolean> restResult = new RestResult<>("0", "success");
        callResponse.setResponseData(JSON.toJSONString(restResult));
        callResponse.setStatus(Constant.YES);
        remoteCallResponseService.getDao().insert(callResponse);

        return restResult;
    }

    @Override
    public RestResult<Boolean> pushOrderPriceChangeConfirm(OrderReviewReq orderReviewReq) {
        logger.info("[Erp-Push-Order-Price-Change-Confirm]=======================");
        String requestJson = JSON.toJSONString(orderReviewReq);
        logger.info(requestJson);

        // 远程调用数据记录
        CoreRemoteCallRequestBean callRequest = new CoreRemoteCallRequestBean();
        callRequest.setId(ApplicationContextHelper.getNextIdentity());
        callRequest.setConfigId("pushOrderPriceChangeConfirm");
        callRequest.setRequestData(requestJson);
        callRequest.setStatus(Constant.YES);
        callRequest.setRequestDateTime(LocalDateTime.now());
        remoteCallRequestService.getDao().insert(callRequest);

        CoreRemoteCallResponseBean callResponse = new CoreRemoteCallResponseBean();
        callResponse.setId(ApplicationContextHelper.getNextIdentity());
        callResponse.setRequestId(callRequest.getId());
        callResponse.setResponseDateTime(LocalDateTime.now());

        // ERP推送的订单变更折扣审核
        Long applyGbId = orderReviewReq.getApplyGbId();
        List<EmcProjectChangeBean> projectChangeList = projectChangeService.getDao().selectListByOneColumnValue(applyGbId, "erpGbId");
        if (projectChangeList.isEmpty()) {
            RestResult<Boolean> restResult = new RestResult<>("102", "该订单变更在LIMS中不存在");
            callResponse.setResponseData(JSON.toJSONString(restResult));
            callResponse.setStatus(Constant.YES);
            remoteCallResponseService.getDao().insert(callResponse);

            return restResult;
        }

        // 待审核的订单
        List<EmcProjectChangeBean> approveProjectChangeList = projectChangeList.stream().filter(project -> StringUtils.equals(project.getProcessStatus(), "approve")).collect(Collectors.toList());
        if (approveProjectChangeList.isEmpty()) {
            RestResult<Boolean> restResult = new RestResult<>("103", "该订单变更已被审核");
            callResponse.setResponseData(JSON.toJSONString(restResult));
            callResponse.setStatus(Constant.YES);
            remoteCallResponseService.getDao().insert(callResponse);

            return restResult;
        }

        EmcProjectChangeBean targetProjectChange = approveProjectChangeList.get(0);
        targetProjectChange.setDiscountReviewStatus(orderReviewReq.getReviewType() ? ErpConstant.ORDER_CHANGE_DISCOUNT_REVIEW_PASSED : ErpConstant.ORDER_CHANGE_DISCOUNT_REVIEW_REJECTED);

        projectChangeService.getDao().update(targetProjectChange, "discountReviewStatus");

        RestResult<Boolean> restResult = new RestResult<>("0", "success");
        callResponse.setResponseData(JSON.toJSONString(restResult));
        callResponse.setStatus(Constant.YES);
        remoteCallResponseService.getDao().insert(callResponse);

        return restResult;
    }

    private EmcProjectChangeBean createProjectChange(OrderChangeDetailResp orderChangeDetail, EmcProjectBean project) {
        // 创建项目变更
        EmcProjectChangeBean projectChange = new EmcProjectChangeBean();
        BeanUtils.copyProperties(project, projectChange);
        projectChange.setProcessStatus(null);
        projectChange.setId(ApplicationContextHelper.getNextIdentity());
        projectChange.setChangeNo(ApplicationContextHelper.getNextSequence(projectChangeService.getDao().getTable()));
        projectChange.setErpOrderNumber(project.getErpOrderNumber());

        Map<String, Pair<String, String>> orderValueMap = parseDynamicFieldValue2(orderChangeDetail.getTarget().getOrderValue(), orderChangeDetail.getCustomTemplateView());
        // ERP 动态配置字段解析
        projectChange.setProjName(Optional.ofNullable(parseValueByFieldName2(ErpConstant.PROJECT_NAME_FIELD_NAME, orderValueMap)).orElse(orderChangeDetail.getTarget().getReportTitle()));
        projectChange.setTestOrgName(parseValueByFieldName2(ErpConstant.TEST_ORG_FIELD_NAMES[0], orderValueMap));
        projectChange.setTestOrgAddress(parseValueByFieldName2(ErpConstant.TEST_ORG_FIELD_NAMES[1], orderValueMap));
        projectChange.setTestOrgContact(parseValueByFieldName2(ErpConstant.TEST_ORG_FIELD_NAMES[2], orderValueMap));
        projectChange.setTestOrgPhone(parseValueByFieldName2(ErpConstant.TEST_ORG_FIELD_NAMES[3], orderValueMap));
        projectChange.setReportAptitude(parseValueByFieldName2(ErpConstant.REPORT_APTITUDE, orderValueMap));
        projectChange.setReportUse(parseValueByFieldName2(ErpConstant.REPORT_USE, orderValueMap));
        projectChange.setPretreatment(parseValueByFieldName2(ErpConstant.PRETREATMENT, orderValueMap));
        projectChange.setNeedQcReport(parseValueByFieldName2(ErpConstant.NEED_QC_REPORT, orderValueMap));
        projectChange.setNeedSplitReport(parseValueByFieldName2(ErpConstant.NEED_SPLIT_REPORT, orderValueMap));
        projectChange.setSubcontractSupplier(parseValueByFieldName2(ErpConstant.SUBCONTRACT_SUPPLIER, orderValueMap));

        /*projectChange.setReportEnName(parseValueByFieldName2(ErpConstant.REPORT_EN_NAME, orderValueMap));
        projectChange.setReportCnName(parseValueByFieldName2(ErpConstant.REPORT_CN_NAME, orderValueMap));
        projectChange.setReportEnAddress(parseValueByFieldName2(ErpConstant.REPORT_EN_ADDRESS, orderValueMap));
        projectChange.setReportCnAddress(parseValueByFieldName2(ErpConstant.REPORT_CN_ADDRESS, orderValueMap));
        projectChange.setReportPostAddress(parseValueByFieldName2(ErpConstant.REPORT_POST_ADDRESS, orderValueMap))*/

        projectChange.setReportCnName(orderChangeDetail.getSource().getReportTitle());
        projectChange.setReportCnAddress(orderChangeDetail.getSource().getReportCompanyAddr().getDetail());
        projectChange.setReportEnName(orderChangeDetail.getSource().getEnReportTitle());
        projectChange.setReportEnAddress(orderChangeDetail.getSource().getEnReportCompanyAddr());
        projectChange.setAddressee(orderChangeDetail.getSource().getRecipient());
        projectChange.setRecipientPhone(orderChangeDetail.getSource().getRecipientPhone());

        projectChange.setTestingLab(parseValueByFieldName2(ErpConstant.TESTING_LAB, orderValueMap));
        projectChange.setTestMethodAssigner(parseValueByFieldName2(ErpConstant.TEST_METHOD_ASSIGNER, orderValueMap));
        projectChange.setMergeReport(parseValueByFieldName2(ErpConstant.MERGE_REPORT, orderValueMap));;

        return projectChange;
    }

    private void setUserLogin(String merchantId, String erpUserId) {
        // 设置当前登录用户，用于记录后续的操作
        List<CoreOrgBean> companyOrgList = orgService.getDao().selectListByOneColumnValue("company", "ORGCATEGORY");
        companyOrgList.stream().filter(org -> StringUtils.equals(merchantId + "", org.getExt$Item("erpMerchantId"))).findFirst().ifPresent(org -> {
            EmcEmpBean erpEmp = emcEmpService.selectFirstByFilter(SearchFilter.instance().match("erpGbId", erpUserId).filter(MatchPattern.SEQ).match("orgId", org.getId()).filter(MatchPattern.SEQ));
            if (Objects.nonNull(erpEmp)) {
                CoreUserBean coreUser = coreUserService.selectById(erpEmp.getId());
                LocalContextHelper.setUserLogin(new LoginUser(coreUser.getId(), coreUser.getPassword()));
            }
        });
    }

    /**
     * 根据订单信息创建项目
     * @param orderDetail 订单详情
     * @return
     */
    private EmcProjectBean createProject(MidOrderDetailVO orderDetail, Map<Long, Pair<String, String>> orderValueMap, EmcProjectBean originalProject) {
        EmcProjectBean newProject = new EmcProjectBean();
        newProject.setId(ApplicationContextHelper.getNextIdentity());
        newProject.setEntrustOrgName(orderDetail.getDelegateCompany());
        newProject.setEntrustOrgAddress(orderDetail.getDelegateCompanyAddr());
        newProject.setEntrustOrgContact(orderDetail.getContact());
        newProject.setEntrustOrgPhone(orderDetail.getPhoneNum());
        newProject.setRemark(orderDetail.getRemarks());
        newProject.setErpGbId(orderDetail.getGbId());
        newProject.setErpReviewId(orderDetail.getReviewId());

        String serverPhone = orderDetail.getUserContractViewModel().getServerPhone();
        newProject.setExt$Item("bidderName", orderDetail.getEngineerName() + (StringUtils.isEmpty(serverPhone) ? "" : "/" + serverPhone));
        newProject.setErpOrderNumber(orderDetail.getOrderNumber());
        newProject.setExt$Item("contractNo", orderDetail.getUserContractViewModel().getUserContractNumber());

        // 下拉选择直接取值
        newProject.setPeriodType(String.valueOf(orderDetail.getPeriodType()));
        newProject.setPeriod(String.valueOf(orderDetail.getActualPeriod()));
        newProject.setReportAptitude(String.valueOf(orderDetail.getQualification()));
        newProject.setNeedReport(Objects.nonNull(orderDetail.getQualification()) ? Constant.YES : Constant.NO);

        HJOrderExtraInfoModelExt orderExtraInfoModelExt = orderDetail.getOrderExtraInfoModelExt();
        if (Objects.nonNull(orderExtraInfoModelExt) && Objects.nonNull(orderExtraInfoModelExt.getOrderExtraInfo())) {
            EnvOrderExtraInfoEntity orderExtraInfo = orderExtraInfoModelExt.getOrderExtraInfo();
            newProject.setExt$Item("reportType", String.valueOf(orderExtraInfo.getReportType()));
            newProject.setReportFee(String.valueOf(orderExtraInfo.getReportFee()));
            newProject.setSecurityLevel(String.valueOf(orderExtraInfo.getSecurityLevel()));
        }

        // ERP 动态配置字段解析
        newProject.setProjName(Optional.ofNullable(parseValueByFieldName(ErpConstant.PROJECT_NAME_FIELD_NAME, orderValueMap)).orElse(orderDetail.getReportTitle()));
        newProject.setTestOrgName(parseValueByFieldName(ErpConstant.TEST_ORG_FIELD_NAMES[0], orderValueMap));
        newProject.setTestOrgAddress(parseValueByFieldName(ErpConstant.TEST_ORG_FIELD_NAMES[1], orderValueMap));
        newProject.setTestOrgContact(parseValueByFieldName(ErpConstant.TEST_ORG_FIELD_NAMES[2], orderValueMap));
        newProject.setTestOrgPhone(parseValueByFieldName(ErpConstant.TEST_ORG_FIELD_NAMES[3], orderValueMap));
        newProject.setSubcontractSupplier(parseValueByFieldName(ErpConstant.SUBCONTRACT_SUPPLIER, orderValueMap));

        // 需要根据值来匹配系统编码
        List<String> codeCategoryList = Arrays.asList("reportUse", "pretreatmentRemark", "testMethodAssigner", "reportTitle");
        List<CoreCodeBean> codeList = coreCodeService.getDao().selectListByOneColumnValues(codeCategoryList, "codeCategoryId");
        newProject.setReportUse(matchCodeName(parseValueByFieldName(ErpConstant.REPORT_USE, orderValueMap), "reportUse", codeList));
        newProject.setPretreatment(matchCodeName(parseValueByFieldName(ErpConstant.PRETREATMENT, orderValueMap), "pretreatmentRemark", codeList));
        newProject.setReportTitle(matchCodeName(parseValueByFieldName(ErpConstant.REPORT_TITLE, orderValueMap), "reportTitle", codeList));
        newProject.setTestMethodAssigner(matchCodeName(parseValueByFieldName(ErpConstant.TEST_METHOD_ASSIGNER, orderValueMap), "testMethodAssigner", codeList));

        // 将ERP的是否转换成是否下拉选项值
        newProject.setNeedQcReport(convertErpYesOrNo(parseValueByFieldName(ErpConstant.NEED_QC_REPORT, orderValueMap)));
        newProject.setNeedSplitReport(convertErpYesOrNo(parseValueByFieldName(ErpConstant.NEED_SPLIT_REPORT, orderValueMap)));
        newProject.setMergeReport(convertErpYesOrNo(parseValueByFieldName(ErpConstant.MERGE_REPORT, orderValueMap)));
        newProject.setExt$Item("subpackage", convertErpYesOrNo(parseValueByFieldName(ErpConstant.NEED_SUBPACKAGE, orderValueMap)));

        // 当需要质控报告时，解析质控报告类型
        Map<Long, Pair<String, String>> relatedFieldValueMap = parseDynamicRelatedFieldValue(orderDetail.getOrderValue(), orderDetail.getCustomTemplateView());
        newProject.setQcReportType(parseValueByFieldName(ErpConstant.QC_REPORT_TYPE, relatedFieldValueMap));

        newProject.setReportCnName(orderDetail.getReportTitle());
        newProject.setReportCnAddress(orderDetail.getReportCompanyAddr());
        newProject.setReportEnName(orderDetail.getEnReportTitle());
        newProject.setReportEnAddress(orderDetail.getEnReportCompanyAddr());
        newProject.setReportPostAddress(orderDetail.getReportSendAddr());
        newProject.setAddressee(orderDetail.getRecipient());
        newProject.setRecipientPhone(orderDetail.getRecipientPhone());

        newProject.setTestingLab(parseValueByFieldName(ErpConstant.TESTING_LAB, orderValueMap));

        // 根据ERP传过来的项目类型名称解析LIMS的业务类型
        String bizCategoryName = parseValueByFieldName(ErpConstant.BIZ_CATEGORY_FIELD_NAME, orderValueMap);
        List<EmcBizCategoryBean> bizCategoryList = bizCategoryService.getDao().selectListByOneColumnValue(bizCategoryName, "bizCategoryName");
        if (!bizCategoryList.isEmpty()) {
            EmcBizCategoryBean bizCategory = bizCategoryList.get(0);
            newProject.setBizCategoryId(bizCategory.getId());
            newProject.setBizCategoryCode(bizCategory.getBizCategoryId());
        }

        // 获取ERP采样类型，1：采样，2：送样
        Integer samplingType = orderDetail.getOrderExtraInfoModelExt().getOrderExtraInfo().getSamplingType();
        newProject.setSample(samplingType == 1 ? Constant.YES : Constant.NO);

        // 获取商户ID，根据商户ID反查orgId
        Integer merchantId = orderDetail.getOrderExtraInfoModelExt().getOrderExtraInfo().getMerchantId();
        List<CoreOrgBean> companyOrgList = orgService.getDao().selectListByOneColumnValue("company", "ORGCATEGORY");
        companyOrgList.stream().filter(org -> StringUtils.equals(merchantId + "", org.getExt$Item("erpMerchantId"))).findFirst().ifPresent(org -> {
            newProject.setOrgId(org.getId());
            newProject.setOrgName(org.getOrgName());

            // 项目创建人取ERP订单中的跟单员
            String saleName = orderDetail.getSaleName();
            newProject.setCreatedByName(saleName);

            Long saleGbId = orderDetail.getSaleGbId();
            EmcEmpBean erpEmp = emcEmpService.selectFirstByFilter(SearchFilter.instance().match("erpGbId", saleGbId).filter(MatchPattern.SEQ).match("orgId", org.getId()).filter(MatchPattern.SEQ));
            if (Objects.nonNull(erpEmp)) {
                newProject.setCreatedById(erpEmp.getId());
                CoreUserBean coreUser = coreUserService.selectById(erpEmp.getId());
                LocalContextHelper.setUserLogin(new LoginUser(coreUser.getId(), coreUser.getPassword()));
            }

            // 如果原项目存在，则项目编号信息还是保留为原来的
            if (!ObjectUtils.isEmpty(originalProject)) {
                newProject.setProjNo(originalProject.getProjNo());
                newProject.setTaskDate(originalProject.getTaskDate());
                newProject.setRegistDate(originalProject.getRegistDate());
                newProject.setProjCreatedTime(originalProject.getProjCreatedTime());
            } else {
                // 订单时间
                LocalDateTime taskDate = ObjectUtils.isEmpty(orderDetail.getStartExperimentTime()) ? LocalDateTime.now() : Instant.ofEpochMilli(orderDetail.getStartExperimentTime()).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
                // 生成项目编号
                String orgCode = Optional.ofNullable(org.getExt$Item("orgShortName")).orElse("XX");
                int nowYear = LocalDate.now().getYear();
                int year = Integer.parseInt(Optional.ofNullable(org.getExt$Item("quayear")).orElse(nowYear + "")) ;
                int month = taskDate.getMonthValue();
                String contractNo = orgCode + EmcBizConstant.NUMFORENG[nowYear-year] + EmcBizConstant.NUMFORENG[month-1];
                String nextSeqNo = "001";
                EmcProjectBean maxNoProject = projectService.selectFirstByFilter(SearchFilter.instance().match("projNo", contractNo).filter(MatchPattern.SB), Order.desc("projNo"));
                if (maxNoProject != null && !StringUtils.isEmpty(maxNoProject.getProjNo())) {
                    String maxSeqNo = maxNoProject.getProjNo().substring(contractNo.length());
                    int nextSeqNoValue = NumberUtils.isNumber(maxSeqNo) ? NumberUtils.parseInt(maxSeqNo) + 1 : 1;
                    nextSeqNo = StringUtils.leftPad(nextSeqNoValue + "", 3);
                }
                newProject.setProjNo(contractNo + nextSeqNo);
                newProject.setTaskDate(taskDate);
                newProject.setRegistDate(LocalDateTime.now());
                newProject.setProjCreatedTime(LocalDateTime.now());
            }
        });

        return newProject;
    }

    /**
     * 转换ERP的是否值
     * @param value 要转换的值
     * @return
     */
    private String convertErpYesOrNo(String value) {
        return ErpConstant.ENUM_YES_VALUE.equals(value) ? Constant.YES : Constant.NO;
    }

    /**
     * 匹配系统编码名称，返回系统编码CODE
     * @param value 要匹配的值
     * @param codeCategory 编码分类
     * @param codeList 编码列表
     * @return 系统编码值
     */
    private String matchCodeName(String value, String codeCategory, List<CoreCodeBean> codeList) {
        CoreCodeBean targetCode = codeList.stream().filter(code -> StringUtils.equals(code.getCodeCategoryId(), codeCategory)
                && StringUtils.equals(code.getCodeName(), value)).findFirst().orElseGet(CoreCodeBean::new);

        return targetCode.getCode();
    }

    /**
     * 根据订单详情创建合同
     * @param orderDetail 订单详情
     * @return
     */
    private EmcContractBean createContract(MidOrderDetailVO orderDetail, EmcProjectBean project) {
        EmcContractBean newContract = new EmcContractBean();
        MidUserContractVO userContract = orderDetail.getUserContractViewModel();
        newContract.setId(ApplicationContextHelper.getNextIdentity());
        newContract.setContractNo(userContract.getUserContractNumber());

        // 委托信息
        newContract.setExt$Item("entrustor", userContract.getContactName());
        newContract.setExt$Item("ENTRUSTORGNAME", userContract.getDelegateCompany());
        newContract.setExt$Item("ENTRUSTORGADDRESS", userContract.getDelegateCompanyAddr());
        newContract.setExt$Item("ENTRUSTORGPHONE", userContract.getPhoneNum());
        newContract.setExt$Item("SUBPACKAGE", userContract.getbAcceptSubcontract() ? Constant.YES : Constant.NO);

        // 服务信息
        newContract.setExt$Item("CONTRACTORNAME", orderDetail.getOrgName());
        newContract.setExt$Item("CONTRACTORADDRESS", userContract.getServerAddr().getAddress());
        newContract.setExt$Item("CONTRACTORPHONE", userContract.getServerPhone());

        newContract.setErpGbId(orderDetail.getContractGbId());
        newContract.setExt$Item("orgId", project.getOrgId());
        newContract.setCreatedById(project.getCreatedById());
        newContract.setCreatedByName(project.getCreatedByName());

        newContract.setExt$Item("contractCategory", ErpConstant.NORMAL_CONTRACT_CATEGORY);

        return newContract;
    }

    /**
     * 创建报告发送信息
     * @param project 项目
     * @param orderDetail 订单详情
     * @return
     */
    private void createReportSendInfo(EmcProjectBean project, MidOrderDetailVO orderDetail) {
        EmcReportSendInfoBean reportSendInfo = new EmcReportSendInfoBean();
        reportSendInfo.setId(ApplicationContextHelper.getNextIdentity());
        reportSendInfo.setProjId(project.getId());
        reportSendInfo.setSendEmail(orderDetail.getEmail());

        List<MidOrderAdditionalReportDTO> orderReportsViews = orderDetail.getOrderReportsViews();
        if (Objects.nonNull(orderReportsViews) && !orderReportsViews.isEmpty()) {
            orderReportsViews.forEach(reportView -> {
                if (ErpConstant.CN_REPORT_TYPE == reportView.getReportType()) {
                    reportSendInfo.setNeedCnReport(reportView.getNeedElectronicType() == ErpConstant.NEED_REPORT ? Constant.YES : Constant.NO);
                    reportSendInfo.setCnReportCnt(reportView.getPaperReportCnt());
                }
                if (ErpConstant.EN_REPORT_TYPE == reportView.getReportType()) {
                    reportSendInfo.setNeedEnReport(reportView.getNeedElectronicType() == ErpConstant.NEED_REPORT ? Constant.YES : Constant.NO);
                    reportSendInfo.setEnReportCnt(reportView.getPaperReportCnt());
                }
                if (ErpConstant.CN_EN_REPORT_TYPE == reportView.getReportType()) {
                    reportSendInfo.setNeedCnEnReport(reportView.getNeedElectronicType() == ErpConstant.NEED_REPORT ? Constant.YES : Constant.NO);
                    reportSendInfo.setCnEnReportCnt(reportView.getPaperReportCnt());
                }
            });
        }

        reportSendInfoService.getDao().insert(reportSendInfo);
    }

    /**
     * 创建送样任务样品
     * @param project 项目
     * @param orderDetail 订单详情
     * @return
     */
    private void createOrder(EmcProjectBean project, MidOrderDetailVO orderDetail) {
        List<MidSampleDetailVO> samples = orderDetail.getSamples();
        if (CollectionUtils.isEmpty(samples)) {
            return;
        }

        List<EnvOrderSampleExtraInfoEntity> envOrderSampleExtraInfoList = orderDetail.getOrderExtraInfoModelExt().getEnvOrderSampleExtraInfoEntity();
        List<String> monitorTmplNameList = envOrderSampleExtraInfoList.stream().map(EnvOrderSampleExtraInfoEntity::getSamplingTypeName).distinct().collect(Collectors.toList());
        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
        List<EmcMonitorTmplBean> monitorTmplList = monitorTmplService.selectListByFilter(SearchFilter.instance().match("orgId", parentOrgId).filter(MatchPattern.SEQ).match("monitorTmplName", monitorTmplNameList).filter(MatchPattern.OR));
        List<String> monitorTmplIdList = monitorTmplList.stream().map(EmcMonitorTmplBean::getId).collect(Collectors.toList());

        List<EmcMonitorSchemeBean> monitorSchemeList = queryMonitorScheme(orderDetail, monitorTmplIdList);
        if (monitorSchemeList.isEmpty()) {
            logger.warn("--> EmcMonitorSchemeBean is empty.");
            return;
        }

        // 查询分析项组对应的分析项
        List<EmcGroupItemBean> groupItemList = queryGroupItemList(monitorSchemeList);

        samples.stream().filter(sample -> ObjectUtils.isEmpty(sample.getCnt())).forEach(sample -> sample.setCnt(1));
        int totalFolderQty = samples.stream().mapToInt(MidSampleDetailVO::getCnt).sum();
        List<String> newFolderNoList = folderService.generateFolderNosByProjId(project.getId(), project.getProjNo(), totalFolderQty);
        AtomicInteger folderIndex = new AtomicInteger();

        List<EnvSampleSkuSamplingTypeEntity> sampleSkuSamplingTypes = orderDetail.getOrderExtraInfoModelExt().getSampleSkuSamplingTypes();

        List<EmcOrderBean> orderList = new ArrayList<>();
        List<EmcFolderBean> folderList = new ArrayList<>();
        List<EmcOrderItemGroupBean> orderItemGroupList = new ArrayList<>();
        List<EmcOrderItemBean> orderItemList = new ArrayList<>();

        samples.forEach(sample -> {
            // 获取当前样品对应的样品类型，如果找不到样品类型，则跳过
            EnvOrderSampleExtraInfoEntity sampleExtraInfo = envOrderSampleExtraInfoList.stream().filter(info -> ObjectUtils.equals(info.getSampleGbId(), sample.getGbId())).findFirst().orElseGet(EnvOrderSampleExtraInfoEntity::new);
            if (StringUtils.isEmpty(sampleExtraInfo.getSamplingTypeName())) {
                return;
            }

            EmcMonitorTmplBean monitorTmpl = monitorTmplList.stream().filter(tmpl -> StringUtils.equals(tmpl.getMonitorTmplName(), sampleExtraInfo.getSamplingTypeName())).findFirst().orElseGet(EmcMonitorTmplBean::new);
            if (StringUtils.isEmpty(monitorTmpl.getMonitorTmplName())) {
                return;
            }

            // 当前样品真实需要检测的SKU
            List<String> validSkuGbIdList = sampleSkuSamplingTypes.stream().filter(type -> ObjectUtils.equals(sample.getGbId(), type.getSampleGbId()) && type.getNeedDetected()).map(EnvSampleSkuSamplingTypeEntity::getSkuGbId).map(skuId -> skuId + "").collect(Collectors.toList());

            // 先获取sku数据，根据sku数据反查监测方案，如果查询不到监测方案，则不创建样品
            List<MidSkuDetailVo> skus = sample.getSkus();
            Map<String, String> skuSchemeNameMap = new HashMap<>();
            skus.forEach(sku -> {
                if (!CollectionUtils.isEmpty(sku.getSingles())) {
                    List<String> singleSkuGbIdList = sku.getSingles().stream().map(MidSkuDetailVo::getGbId).map(String::valueOf).collect(Collectors.toList());
                    singleSkuGbIdList.forEach(gbId -> skuSchemeNameMap.put(gbId, sku.getName()));
                }
            });

            List<EmcMonitorSchemeBean> curSchemeList = monitorSchemeList.stream().filter(scheme -> StringUtils.equals(monitorTmpl.getId(), scheme.getMonitorTmplId())
                    && validSkuGbIdList.contains(scheme.getSkuGbId())).collect(Collectors.toList());
            if (curSchemeList.isEmpty()) {
                return;
            }

            List<String> curCapacityItemGroupIdList = curSchemeList.stream().map(EmcMonitorSchemeBean::getItemGroupId).distinct().map(Objects::toString).collect(Collectors.toList());
            List<EmcGroupItemBean> curGroupItemList = groupItemList.stream().filter(groupItem -> curCapacityItemGroupIdList.contains(groupItem.getExt$Item("capacityItemGroupId"))).collect(Collectors.toList());

            // 取第一个监测方案，用于获取监测模板信息
            EmcMonitorSchemeBean basicScheme = curSchemeList.get(0);

            Integer orderNum = Optional.ofNullable(sample.getCnt()).orElse(1);
            IntStream.range(0, orderNum).forEach(index -> {
                EmcFolderBean newFolder = createFolder(project, sampleExtraInfo, basicScheme, newFolderNoList.get(folderIndex.getAndIncrement()));
                EmcOrderBean order = createOrder(project, newFolder, sample);
                List<EmcOrderItemGroupBean> curOrderItemGroupList = createOrderItemGroup(order, curSchemeList, skuSchemeNameMap, curGroupItemList, orderItemList);

                orderList.add(order);
                folderList.add(newFolder);
                orderItemGroupList.addAll(curOrderItemGroupList);
            });
        });

        if (orderItemList.size() > 100000) {
            logger.warn("常规项目下单{}复制出现问题，数据异常 -> {}", project.getProjNo(), orderItemList.size());
            throw new UnsupportedException("数据异常");
        }

        folderService.getDao().insert(folderList);
        orderService.getDao().insert(orderList);

        orderItemGroupService.getDao().insert(orderItemGroupList);
        orderItemService.getDao().insert(orderItemList);
    }

    /**
     * 创建送样任务点位
     * @param project
     * @param sampleExtraInfo
     * @param basicScheme
     * @return
     */
    private EmcFolderBean createFolder(EmcProjectBean project, EnvOrderSampleExtraInfoEntity sampleExtraInfo, EmcMonitorSchemeBean basicScheme, String folderNo) {
        EmcFolderBean newFolder = new EmcFolderBean();
        newFolder.setId(ApplicationContextHelper.getNextIdentity());
        newFolder.setProjId(project.getId());
        newFolder.setFolderNo(folderNo);
        newFolder.setMonitorTmplId(basicScheme.getMonitorTmplId());
        newFolder.setOrderCategory(basicScheme.getExt$Item("monitorTmplName"));
        newFolder.setMonitorPointName(sampleExtraInfo.getPointName());
        newFolder.setSource(EmcBizConstant.BP_SOURCE_ORDERREGISTRATION);
        newFolder.setTermination(Constant.NO);

        return newFolder;
    }

    /**
     * 创建送样任务样品
     * @param project
     * @param folder
     * @param sample
     * @return
     */
    private EmcOrderBean createOrder(EmcProjectBean project, EmcFolderBean folder, MidSampleDetailVO sample) {
        EmcOrderBean newOrder = new EmcOrderBean();
        newOrder.setId(ApplicationContextHelper.getNextIdentity());
        newOrder.setProjId(project.getId());
        newOrder.setClientOrderName(sample.getSubject());
        newOrder.setFolderId(folder.getId());
        newOrder.setMonitorTmplId(folder.getMonitorTmplId());
        newOrder.setOrderCategory(folder.getOrderCategory());
        newOrder.setOrderNo(folder.getFolderNo() + "A001");
        if (newOrder.getLabelNo() == null) {
            newOrder.setLabelNo(newOrder.getOrderNo());
        }
        newOrder.setSendDate(LocalDate.now());
        newOrder.setSendTime(DateTimeUtils.formatLocalDateTime(LocalDateTime.now(), "HH:mm"));
        newOrder.setOrderNum(EmcConstant.ONE);
        newOrder.setCycle(EmcConstant.ONE);
        newOrder.setFrequency(EmcConstant.ONE);
        newOrder.setOrderNum(EmcConstant.ONE);
        newOrder.setTermination(Constant.NO);
        newOrder.setErpGbId(sample.getGbId());

        return newOrder;
    }

    /**
     * 创建样品分析项组
     * @param order
     * @param monitorSchemeList
     * @return
     */
    private List<EmcOrderItemGroupBean> createOrderItemGroup(EmcOrderBean order, List<EmcMonitorSchemeBean> monitorSchemeList, Map<String, String> skuSchemeNameMap, List<EmcGroupItemBean> groupItemList, List<EmcOrderItemBean> orderItemList) {
        List<EmcOrderItemGroupBean> orderItemGroupList = new ArrayList<>(monitorSchemeList.size());
        monitorSchemeList.forEach(scheme -> {
            EmcOrderItemGroupBean orderItemGroup = new EmcOrderItemGroupBean();
            orderItemGroup.setId(ApplicationContextHelper.getNextIdentity());
            orderItemGroup.setOrderCategory(order.getOrderCategory());
            orderItemGroup.setOrderId(order.getId());
            orderItemGroup.setSchemeName(Optional.ofNullable(skuSchemeNameMap.get(scheme.getSkuGbId())).orElse(scheme.getSchemeName()));
            orderItemGroup.setMonitorTmplId(scheme.getMonitorTmplId());
            orderItemGroup.setTestId(scheme.getTestId());
            orderItemGroup.setTestName(scheme.getExt$Item("testName"));
            orderItemGroup.setMethodId(NumberUtils.parseLong(scheme.getExt$Item("methodId")));
            orderItemGroup.setTestMethodId(scheme.getTestMethodId());
            orderItemGroup.setTestMethodBasis(scheme.getExt$Item("testMethodBasis"));
            orderItemGroup.setMethodName(scheme.getExt$Item("methodName"));
            orderItemGroup.setItemGroupId(scheme.getItemGroupId());
            orderItemGroup.setItemGroup(scheme.getItemGroup());
            orderItemGroup.setQcCategoryId(order.getQcCategoryId());
            orderItemGroup.setQcCategoryCode(order.getQcCategoryCode());
            orderItemGroup.setQcName(order.getQcName());
            orderItemGroupList.add(orderItemGroup);

            groupItemList.stream().filter(groupItem -> StringUtils.equals(groupItem.getExt$Item("capacityItemGroupId"), scheme.getItemGroupId() + "")).forEach(groupItem -> {
                EmcOrderItemBean orderItem = new EmcOrderItemBean();

                orderItem.setId(ApplicationContextHelper.getNextIdentity());
                orderItem.setItemId(groupItem.getItemId());
                orderItem.setOrderId(orderItemGroup.getOrderId());
                orderItem.setOrderItemGroupId(orderItemGroup.getId());
                orderItemList.add(orderItem);
            });
        });

        return orderItemGroupList;
    }

    private List<EmcGroupItemBean> queryGroupItemList(List<EmcMonitorSchemeBean> monitorSchemeList) {
        List<Long> capacityItemGroupIdList = monitorSchemeList.stream().map(EmcMonitorSchemeBean::getItemGroupId).distinct().collect(Collectors.toList());
        List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByIds(capacityItemGroupIdList);
        List<Long> itemGroupIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId).collect(Collectors.toList());
        Map<Long, Long> itemGroupIdMap = capacityItemGroupList.stream().collect(Collectors.toMap(EmcCapacityItemGroupBean::getItemGroupId, EmcCapacityItemGroupBean::getId, (v1, v2) -> v1));

        List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID");
        groupItemList.forEach(groupItem -> groupItem.setExt$Item("capacityItemGroupId", itemGroupIdMap.get(groupItem.getItemGroupId()) + ""));

        return groupItemList;
    }

    /**
     * 创建项目方案
     * @param project
     * @param orderDetail
     */
    private void createProjectScheme(EmcProjectBean project, MidOrderDetailVO orderDetail) {
        List<MidSampleDetailVO> samples = orderDetail.getSamples();
        if (CollectionUtils.isEmpty(samples)) {
            logger.warn("--> MidSampleDetailVO is empty.");
            return;
        }

        List<EnvOrderSampleExtraInfoEntity> envOrderSampleExtraInfoList = orderDetail.getOrderExtraInfoModelExt().getEnvOrderSampleExtraInfoEntity();
        List<String> monitorTmplNameList = envOrderSampleExtraInfoList.stream().map(EnvOrderSampleExtraInfoEntity::getSamplingTypeName).distinct().collect(Collectors.toList());
        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
        List<EmcMonitorTmplBean> monitorTmplList = monitorTmplService.selectListByFilter(SearchFilter.instance().match("orgId", parentOrgId).filter(MatchPattern.SEQ).match("monitorTmplName", monitorTmplNameList).filter(MatchPattern.OR));
        List<String> monitorTmplIdList = monitorTmplList.stream().map(EmcMonitorTmplBean::getId).collect(Collectors.toList());

        List<EmcMonitorSchemeBean> monitorSchemeList = queryMonitorScheme(orderDetail, monitorTmplIdList);
        if (monitorSchemeList.isEmpty()) {
            logger.warn("--> EmcMonitorSchemeBean is empty.");
            return;
        }

        List<EnvSampleSkuSamplingTypeEntity> sampleSkuSamplingTypes = orderDetail.getOrderExtraInfoModelExt().getSampleSkuSamplingTypes();

        List<EmcProjSchemeBean> newProjSchemeList = new ArrayList<>();
        List<EmcProjOrderBean> newProjOrderList = new ArrayList<>();

        // 查询采样科室
        CoreOrgBean samplingDepartment = orgService.selectFirstByFilter(SearchFilter.instance().match("parentId", parentOrgId).filter(MatchPattern.SB)
                .match("ORGCATEGORY", "department").filter(MatchPattern.SEQ)
                .match("SAMPLEDEPARTMENT", Constant.YES).filter(MatchPattern.SEQ));

        // 如果缺失点位数量，则默认赋值1
        samples.stream().filter(sample -> ObjectUtils.isEmpty(sample.getCnt())).forEach(sample -> sample.setCnt(1));
        int totalFolderQty = samples.stream().mapToInt(MidSampleDetailVO::getCnt).sum();
        List<String> newFolderNoList = projSchemeService.generateFolderNosByProjId(project.getId(), project.getProjNo(), totalFolderQty);
        AtomicInteger folderIndex = new AtomicInteger();

        samples.forEach(sample -> {
            // 获取当前样品对应的样品类型，如果找不到样品类型，则跳过
            EnvOrderSampleExtraInfoEntity sampleExtraInfo = envOrderSampleExtraInfoList.stream().filter(info -> ObjectUtils.equals(info.getSampleGbId(), sample.getGbId())).findFirst().orElseGet(EnvOrderSampleExtraInfoEntity::new);
            if (StringUtils.isEmpty(sampleExtraInfo.getSamplingTypeName())) {
                return;
            }

            EmcMonitorTmplBean monitorTmpl = monitorTmplList.stream().filter(tmpl -> StringUtils.equals(tmpl.getMonitorTmplName(), sampleExtraInfo.getSamplingTypeName())).findFirst().orElseGet(EmcMonitorTmplBean::new);
            if (StringUtils.isEmpty(monitorTmpl.getMonitorTmplName())) {
                return;
            }

            // 当前样品真实需要检测的SKU
            List<String> validSkuGbIdList = sampleSkuSamplingTypes.stream().filter(type -> ObjectUtils.equals(sample.getGbId(), type.getSampleGbId()) && type.getNeedDetected()).map(EnvSampleSkuSamplingTypeEntity::getSkuGbId).map(skuId -> skuId + "").collect(Collectors.toList());

            // 先获取sku数据，根据sku数据反查监测方案，如果查询不到监测方案，则不创建监测方案
            List<MidSkuDetailVo> skus = sample.getSkus();

            Map<String, String> skuSchemeNameMap = new HashMap<>();
            skus.forEach(sku -> {
                if (!CollectionUtils.isEmpty(sku.getSingles())) {
                    List<String> singleSkuGbIdList = sku.getSingles().stream().map(MidSkuDetailVo::getGbId).map(String::valueOf).collect(Collectors.toList());
                    singleSkuGbIdList.forEach(gbId -> skuSchemeNameMap.put(gbId, sku.getName()));
                }
            });

            List<EmcMonitorSchemeBean> curSchemeList = monitorSchemeList.stream().filter(scheme -> StringUtils.equals(monitorTmpl.getId(), scheme.getMonitorTmplId())
                    && validSkuGbIdList.contains(scheme.getSkuGbId())).collect(Collectors.toList());
            if (curSchemeList.isEmpty()) {
                return;
            }

            // 获取点位数量
            int folderQty = Optional.ofNullable(sampleExtraInfo.getSamplingPointCount()).orElse(1);
            IntStream.range(0, folderQty).forEach(index -> {
                // 取第一个监测方案，用于获取监测模板信息
                EmcMonitorSchemeBean basicScheme = curSchemeList.get(0);
                EmcProjSchemeBean projScheme = createProjectScheme(project, sample, sampleExtraInfo, basicScheme, newFolderNoList.get(folderIndex.getAndIncrement()));
                if (Objects.nonNull(samplingDepartment)) {
                    projScheme.setSampleOrgId(samplingDepartment.getId());
                }
                projScheme.setErpGbId(sample.getGbId());

                newProjSchemeList.add(projScheme);
                curSchemeList.forEach(scheme -> {
                    EmcProjOrderBean newProjOrder = createProjOrder(projScheme, scheme, skuSchemeNameMap, sampleExtraInfo);
                    newProjOrderList.add(newProjOrder);
                });
            });
        });

        projSchemeService.getDao().insert(newProjSchemeList);
        projOrderService.getDao().insert(newProjOrderList);
    }

    /**
     * 创建项目方案
     * @param project
     * @param sample
     * @param monitorScheme
     * @return
     */
    private EmcProjSchemeBean createProjectScheme(EmcProjectBean project, MidSampleDetailVO sample, EnvOrderSampleExtraInfoEntity sampleExtraInfo, EmcMonitorSchemeBean monitorScheme, String newFolderNo) {
        EmcProjSchemeBean projScheme = new EmcProjSchemeBean();
        projScheme.setId(ApplicationContextHelper.getNextIdentity());
        projScheme.setProjId(project.getId());
        projScheme.setMonitorPointName(Optional.ofNullable(sampleExtraInfo.getPointName()).orElse(sample.getSubject()));
        projScheme.setFolderNo(newFolderNo);
        projScheme.setFrequency(Objects.isNull(sampleExtraInfo.getSamplingFrequency()) ? 1L : (long) sampleExtraInfo.getSamplingFrequency());
        projScheme.setCycle(Objects.isNull(sampleExtraInfo.getSamplingPeriod()) ? 1L : (long) sampleExtraInfo.getSamplingPeriod());
        projScheme.setMonitorTmplId(monitorScheme.getMonitorTmplId());

        return projScheme;
    }

    /**
     * 创建方案测试
     * @param projScheme
     * @param monitorScheme
     * @param sampleExtraInfo
     * @return
     */
    private EmcProjOrderBean createProjOrder(EmcProjSchemeBean projScheme, EmcMonitorSchemeBean monitorScheme, Map<String, String> skuSchemeNameMap, EnvOrderSampleExtraInfoEntity sampleExtraInfo) {
        EmcProjOrderBean newProjOrder = new EmcProjOrderBean();
        newProjOrder.setId(ApplicationContextHelper.getNextIdentity());
        newProjOrder.setProjSchemeId(projScheme.getId());
        newProjOrder.setSampleNum((long) Optional.ofNullable(sampleExtraInfo.getSamplingNum()).orElse(1));
        newProjOrder.setTestId(monitorScheme.getTestId());
        newProjOrder.setItemGroupId(monitorScheme.getItemGroupId());
        newProjOrder.setItemGroup(monitorScheme.getItemGroup());
        // 如果是套餐，则方案名称直接取套餐名称
        newProjOrder.setSchemeName(Optional.ofNullable(skuSchemeNameMap.get(monitorScheme.getSkuGbId())).orElse(monitorScheme.getSchemeName()));
        newProjOrder.setMethodId(NumberUtils.parseLong(monitorScheme.getExt$Item("methodId")));

        return newProjOrder;
    }

    /**
     * 创建送样任务样品
     * @param originalProject 项目
     * @param target 订单详情
     * @return
     */
    private void createOrderChange(EmcProjectBean originalProject, EmcProjectChangeBean projectChange, OrderChangeDiffResp target) {
        List<OrderChangeSampleInfo> samples = target.getSamples();
        if (CollectionUtils.isEmpty(samples)) {
            return;
        }

        List<EmcOrderChangeBean> newOrderList = new ArrayList<>();
        List<EmcFolderChangeBean> newFolderList = new ArrayList<>();
        List<EmcOrderItemGroupChangeBean> newOrderItemGroupList = new ArrayList<>();

        List<EmcMonitorSchemeBean> monitorSchemeList = queryMonitorScheme2(target);

        samples.stream().filter(sample -> ObjectUtils.isEmpty(sample.getSampleCnt())).forEach(sample -> sample.setSampleCnt(1));
        int totalFolderQty = samples.stream().mapToInt(OrderChangeSampleInfo::getSampleCnt).sum();
        List<String> newFolderNoList = folderChangeService.generateFolderNosByProjId(projectChange.getId(), projectChange.getProjNo(), totalFolderQty);
        AtomicInteger folderIndex = new AtomicInteger();

        samples.forEach(sample -> {
            // 先获取sku数据，根据sku数据反查监测方案，如果查询不到监测方案，则不创建样品
            List<MidSkuDetailVo> skus = sample.getSkus();
            List<String> skuGbIdList = skus.stream().map(MidSkuDetailVo::getGbId).map(String::valueOf).distinct().collect(Collectors.toList());
            List<EmcMonitorSchemeBean> curSchemeList = monitorSchemeList.stream().filter(scheme -> skuGbIdList.contains(scheme.getSkuGbId())).collect(Collectors.toList());
            if (curSchemeList.isEmpty()) {
                return;
            }

            // 取第一个监测方案，用于获取监测模板信息
            EmcMonitorSchemeBean basicScheme = curSchemeList.get(0);

            Integer orderNum = Optional.ofNullable(sample.getSampleCnt()).orElse(1);
            IntStream.range(0, orderNum).forEach(index -> {
                EmcFolderChangeBean newFolder = createFolderChange(projectChange, sample, basicScheme, newFolderNoList.get(folderIndex.getAndIncrement()));
                EmcOrderChangeBean order = createOrderChange(projectChange, newFolder, sample);
                List<EmcOrderItemGroupChangeBean> curOrderItemGroupList = createOrderItemGroupChange(order, curSchemeList);

                newOrderList.add(order);
                newFolderList.add(newFolder);
                newOrderItemGroupList.addAll(curOrderItemGroupList);
            });
        });

        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValue(originalProject.getId(), "projId");
        List<EmcFolderBean> folderList = folderService.getDao().selectListByOneColumnValue(originalProject.getId(), "projId");
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "orderId");

        // 与原来的进行比对，并标记新增和删除的数据
        ErpProjectCompareVO compareVo = new ErpProjectCompareVO();
        compareVo.setProject(originalProject);
        compareVo.setProjectChange(projectChange);
        compareVo.setFolderList(folderList);
        compareVo.setOrderList(orderList);
        compareVo.setOrderItemGroupList(orderItemGroupList);
        compareVo.setFolderChangeList(newFolderList);
        compareVo.setOrderChangeList(newOrderList);
        compareVo.setOrderItemGroupChangeList(newOrderItemGroupList);

        compareService.compareOrder(compareVo);
        compareService.compareOrderItemGroup(compareVo);

        folderChangeService.getDao().insert(compareVo.getFolderChangeList());
        orderChangeService.getDao().insert(compareVo.getOrderChangeList());
        orderItemGroupChangeService.getDao().insert(compareVo.getOrderItemGroupChangeList());
    }

    /**
     * 创建送样任务点位
     * @param project
     * @param sample
     * @param basicScheme
     * @return
     */
    private EmcFolderChangeBean createFolderChange(EmcProjectChangeBean project, OrderChangeSampleInfo sample, EmcMonitorSchemeBean basicScheme, String folderNo) {
        EmcFolderChangeBean newFolder = new EmcFolderChangeBean();
        newFolder.setId(ApplicationContextHelper.getNextIdentity());
        newFolder.setProjId(project.getId());
        newFolder.setFolderNo(folderNo);
        newFolder.setMonitorTmplId(basicScheme.getMonitorTmplId());
        newFolder.setOrderCategory(basicScheme.getExt$Item("monitorTmplName"));
        newFolder.setMonitorPointName(sample.getPointName());
        newFolder.setSource(EmcBizConstant.BP_SOURCE_ORDERREGISTRATION);
        newFolder.setTermination(Constant.NO);

        return newFolder;
    }

    /**
     * 创建送样任务样品
     * @param project
     * @param folder
     * @param sample
     * @return
     */
    private EmcOrderChangeBean createOrderChange(EmcProjectChangeBean project, EmcFolderChangeBean folder, OrderChangeSampleInfo sample) {
        EmcOrderChangeBean newOrder = new EmcOrderChangeBean();
        newOrder.setId(ApplicationContextHelper.getNextIdentity());
        newOrder.setProjId(project.getId());
        newOrder.setClientOrderName(sample.getSubject());
        newOrder.setFolderId(folder.getId());
        newOrder.setMonitorTmplId(folder.getMonitorTmplId());
        newOrder.setOrderCategory(folder.getOrderCategory());
        newOrder.setOrderNo(folder.getFolderNo() + "A001");
        if (newOrder.getLabelNo() == null) {
            newOrder.setLabelNo(newOrder.getOrderNo());
        }
        newOrder.setSendDate(LocalDate.now());
        newOrder.setSendTime(DateTimeUtils.formatLocalDateTime(LocalDateTime.now(), "HH:mm"));
        newOrder.setOrderNum(EmcConstant.ONE);
        newOrder.setCycle(EmcConstant.ONE);
        newOrder.setFrequency(EmcConstant.ONE);
        newOrder.setOrderNum(EmcConstant.ONE);
        newOrder.setTermination(Constant.NO);

        if (NumberUtils.isNumber(sample.getGbId())) {
            newOrder.setErpGbId(NumberUtils.parseLong(sample.getGbId()));
        }

        return newOrder;
    }

    /**
     * 创建样品分析项组
     * @param order
     * @param monitorSchemeList
     * @return
     */
    private List<EmcOrderItemGroupChangeBean> createOrderItemGroupChange(EmcOrderChangeBean order, List<EmcMonitorSchemeBean> monitorSchemeList) {
        List<EmcOrderItemGroupChangeBean> orderItemGroupList = new ArrayList<>(monitorSchemeList.size());
        monitorSchemeList.forEach(scheme -> {
            EmcOrderItemGroupChangeBean orderItemGroup = new EmcOrderItemGroupChangeBean();
            orderItemGroup.setId(ApplicationContextHelper.getNextIdentity());
            orderItemGroup.setOrderCategory(order.getOrderCategory());
            orderItemGroup.setOrderId(order.getId());
            orderItemGroup.setSchemeName(scheme.getSchemeName());
            orderItemGroup.setMonitorTmplId(scheme.getMonitorTmplId());
            orderItemGroup.setTestId(scheme.getTestId());
            orderItemGroup.setTestName(scheme.getExt$Item("testName"));
            orderItemGroup.setMethodId(NumberUtils.parseLong(scheme.getExt$Item("methodId")));
            orderItemGroup.setTestMethodId(scheme.getTestMethodId());
            orderItemGroup.setTestMethodBasis(scheme.getExt$Item("testMethodBasis"));
            orderItemGroup.setMethodName(scheme.getExt$Item("methodName"));
            orderItemGroup.setItemGroupId(scheme.getItemGroupId());
            orderItemGroup.setItemGroup(scheme.getItemGroup());
            orderItemGroup.setQcCategoryId(order.getQcCategoryId());
            orderItemGroup.setQcCategoryCode(order.getQcCategoryCode());
            orderItemGroup.setQcName(order.getQcName());
            orderItemGroupList.add(orderItemGroup);
        });

        return orderItemGroupList;
    }


    /**
     * 创建项目变更方案
     * @param projectChange
     * @param orderChange
     */
    private void createProjectSchemeChange(EmcProjectBean originalProject, EmcProjectChangeBean projectChange, OrderChangeDiffResp orderChange) {
        List<OrderChangeSampleInfo> samples = orderChange.getSamples();
        if (CollectionUtils.isEmpty(samples)) {
            logger.warn("--> OrderChangeSampleInfo is empty.");
            return;
        }

        List<EmcMonitorSchemeBean> monitorSchemeList = queryMonitorScheme2(orderChange);
        if (monitorSchemeList.isEmpty()) {
            logger.warn("--> EmcMonitorSchemeBean is empty.");
            return;
        }

        List<EmcProjSchemeChangeBean> newProjSchemeList = new ArrayList<>();
        List<EmcProjOrderChangeBean> newProjOrderList = new ArrayList<>();

        // 查询采样科室
        String parentOrgId = projectChange.getOrgId();
        CoreOrgBean samplingDepartment = orgService.selectFirstByFilter(SearchFilter.instance().match("parentId", parentOrgId).filter(MatchPattern.SB)
                .match("ORGCATEGORY", "department").filter(MatchPattern.SEQ)
                .match("SAMPLEDEPARTMENT", Constant.YES).filter(MatchPattern.SEQ));

        // 如果缺失点位数量，则默认赋值1
        samples.stream().filter(sample -> ObjectUtils.isEmpty(sample.getSampleCnt())).forEach(sample -> sample.setSampleCnt(1));
        int totalFolderQty = samples.stream().mapToInt(OrderChangeSampleInfo::getSampleCnt).sum();
        List<String> newFolderNoList = projSchemeChangeService.generateFolderNosByProjId(projectChange.getId(), projectChange.getProjNo(), totalFolderQty);
        AtomicInteger folderIndex = new AtomicInteger();

        samples.forEach(sample -> {
            // 先获取sku数据，根据sku数据反查监测方案，如果查询不到监测方案，则不创建监测方案
            List<MidSkuDetailVo> skus = sample.getSkus();

            List<String> skuGbIdList = new ArrayList<>();
            Map<String, String> skuSchemeNameMap = new HashMap<>();
            skus.forEach(sku -> {
                if (!CollectionUtils.isEmpty(sku.getSingles())) {
                    List<String> singleSkuGbIdList = sku.getSingles().stream().map(MidSkuDetailVo::getGbId).map(String::valueOf).collect(Collectors.toList());
                    skuGbIdList.addAll(singleSkuGbIdList);
                    singleSkuGbIdList.forEach(gbId -> skuSchemeNameMap.put(gbId, sku.getName()));
                } else {
                    skuGbIdList.add(sku.getGbId() + "");
                }
            });

            List<EmcMonitorSchemeBean> curSchemeList = monitorSchemeList.stream().filter(scheme -> skuGbIdList.contains(scheme.getSkuGbId())).collect(Collectors.toList());
            if (curSchemeList.isEmpty()) {
                return;
            }

            // 获取点位数量
            int folderQty = Optional.ofNullable(sample.getSampleCnt()).orElse(1);
            IntStream.range(0, folderQty).forEach(index -> {
                // 取第一个监测方案，用于获取监测模板信息
                EmcMonitorSchemeBean basicScheme = curSchemeList.get(0);
                EmcProjSchemeChangeBean projScheme = createProjectSchemeChange(projectChange, sample, basicScheme, newFolderNoList.get(folderIndex.getAndIncrement()));
                if (Objects.nonNull(samplingDepartment)) {
                    projScheme.setSampleOrgId(samplingDepartment.getId());
                }
                if (NumberUtils.isNumber(sample.getGbId())) {
                    projScheme.setErpGbId(NumberUtils.parseLong(sample.getGbId()));
                }

                newProjSchemeList.add(projScheme);
                curSchemeList.forEach(scheme -> {
                    EmcProjOrderChangeBean newProjOrder = createProjOrderChange(projScheme, scheme, sample, skuSchemeNameMap);
                    newProjOrderList.add(newProjOrder);
                });
            });
        });

        List<EmcProjSchemeBean> projSchemeList = projSchemeService.getDao().selectListByOneColumnValue(originalProject.getId(), "projId");
        List<Long> projSchemeIdList = projSchemeList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList());
        List<EmcProjOrderBean> projOrderList = projOrderService.getDao().selectListByOneColumnValues(projSchemeIdList, "projSchemeId");

        ErpProjectCompareVO compareVo = new ErpProjectCompareVO();
        compareVo.setProject(originalProject);
        compareVo.setProjectChange(projectChange);
        compareVo.setProjSchemeList(projSchemeList);
        compareVo.setProjOrderList(projOrderList);
        compareVo.setProjSchemeChangeList(newProjSchemeList);
        compareVo.setProjOrderChangeList(newProjOrderList);

        // 与原来的方案数据进行比对，标记出需要新增和删除的
        compareService.compareProjScheme(compareVo);
        compareService.compareProjOrder(compareVo);

        projSchemeChangeService.getDao().insert(compareVo.getProjSchemeChangeList());
        projOrderChangeService.getDao().insert(compareVo.getProjOrderChangeList());
    }

    /**
     * 创建项目方案
     * @param projectChange
     * @param sample
     * @param monitorScheme
     * @return
     */
    private EmcProjSchemeChangeBean createProjectSchemeChange(EmcProjectChangeBean projectChange, OrderChangeSampleInfo sample, EmcMonitorSchemeBean monitorScheme, String newFolderNo) {
        EmcProjSchemeChangeBean projScheme = new EmcProjSchemeChangeBean();
        projScheme.setId(ApplicationContextHelper.getNextIdentity());
        projScheme.setProjId(projectChange.getId());
        projScheme.setMonitorPointName(Optional.ofNullable(sample.getPointName()).orElse(sample.getSubject()));
        projScheme.setFolderNo(newFolderNo);
        projScheme.setFrequency(Objects.isNull(sample.getSamplingFrequency()) ? 1L : (long) sample.getSamplingFrequency());
        projScheme.setCycle(Objects.isNull(sample.getSamplingPeriod()) ? 1L : (long) sample.getSamplingPeriod());
        projScheme.setMonitorTmplId(monitorScheme.getMonitorTmplId());

        return projScheme;
    }

    /**
     * 创建方案测试
     * @param projScheme
     * @param monitorScheme
     * @param sample
     * @return
     */
    private EmcProjOrderChangeBean createProjOrderChange(EmcProjSchemeChangeBean projScheme, EmcMonitorSchemeBean monitorScheme, OrderChangeSampleInfo sample, Map<String, String> skuSchemeNameMap) {
        EmcProjOrderChangeBean newProjOrder = new EmcProjOrderChangeBean();
        newProjOrder.setId(ApplicationContextHelper.getNextIdentity());
        newProjOrder.setProjSchemeId(projScheme.getId());
        newProjOrder.setSampleNum((long) Optional.ofNullable(sample.getSamplingNum()).orElse(1));
        newProjOrder.setTestId(monitorScheme.getTestId());
        newProjOrder.setItemGroupId(monitorScheme.getItemGroupId());
        newProjOrder.setItemGroup(monitorScheme.getItemGroup());
        // 如果是套餐，则方案名称直接取套餐名称
        newProjOrder.setSchemeName(Optional.ofNullable(skuSchemeNameMap.get(monitorScheme.getSkuGbId())).orElse(monitorScheme.getSchemeName()));
        newProjOrder.setMethodId(NumberUtils.parseLong(monitorScheme.getExt$Item("methodId")));

        return newProjOrder;
    }

    /**
     * 根据SKU查询监测方案，监测方案中有SKU的GBID
     * @param orderDetail
     * @return
     */
    private List<EmcMonitorSchemeBean> queryMonitorScheme(MidOrderDetailVO orderDetail, List<String> monitorTmplIdList) {
        List<MidSampleDetailVO> samples = orderDetail.getSamples();
        Set<Long> skuGbIdSet = new HashSet<>();

        samples.forEach(sample -> sample.getSkus().forEach(sku -> {
            // 如果是套餐类型
            if (!CollectionUtils.isEmpty(sku.getSingles())) {
                skuGbIdSet.addAll(sku.getSingles().stream().map(MidSkuDetailVo::getGbId).collect(Collectors.toSet()));
            } else {
                skuGbIdSet.add(sku.getGbId());
            }
        }));

        List<EmcMonitorSchemeBean> monitorSchemeList = monitorSchemeService.selectListByFilter(
                SearchFilter.instance().match("monitorTmplId", monitorTmplIdList).filter(MatchPattern.OR)
                .match("skuGbId", new ArrayList<>(skuGbIdSet)).filter(MatchPattern.OR)
                .match("schemeName", "default").filter(MatchPattern.SEQ));

        return monitorSchemeList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmcMonitorSchemeBean::getId))), ArrayList::new));
    }

    /**
     * 根据SKU查询监测方案，监测方案中有SKU的GBID
     * @param orderChange
     * @return
     */
    private List<EmcMonitorSchemeBean> queryMonitorScheme2(OrderChangeDiffResp orderChange) {
        List<OrderChangeSampleInfo> samples = orderChange.getSamples();
        List<Long> skuGbIdList = new ArrayList<>();

        samples.forEach(sample -> sample.getSkus().forEach(sku -> {
            // 如果是套餐类型
            if (!CollectionUtils.isEmpty(sku.getSingles())) {
                skuGbIdList.addAll(sku.getSingles().stream().map(MidSkuDetailVo::getGbId).collect(Collectors.toList()));
            } else {
                skuGbIdList.add(sku.getGbId());
            }
        }));

        return monitorSchemeService.selectListByFilter(SearchFilter.instance().match("skuGbId", skuGbIdList).filter(MatchPattern.OR));
    }

    /**
     * 解析动态字段
     * @param valueMap
     * @param templateView
     * @return
     */
    private Map<Long, Pair<String, String>> parseDynamicFieldValue(Map<Long, String> valueMap, MidCustomTemplateVO templateView) {
        Map<Long, Pair<String, String>> fieldValueMap = new HashMap<>(valueMap.size());
        List<MidFieldVO> fields = templateView.getFields();
        valueMap.forEach((fieldId, valid) -> fields.stream().filter(f -> ObjectUtils.equals(fieldId, f.getStructId())).findFirst().ifPresent(f -> {
            List<MidEnumVO> enums = f.getEnums();
            if (!CollectionUtils.isEmpty(enums)) {
                enums.stream().filter(e -> StringUtils.equals(valid, e.getId().toString())).findFirst().ifPresent(e -> fieldValueMap.put(fieldId, new Pair<>(f.getName(), e.getName())));
            } else {
                fieldValueMap.put(fieldId, new Pair<>(f.getName(), valid));
            }
        }));

        return fieldValueMap;
    }

    private Map<Long, Pair<String, String>> parseDynamicRelatedFieldValue(Map<Long, String> valueMap, MidCustomTemplateVO templateView) {
        Map<Long, Pair<String, String>> fieldValueMap = new HashMap<>(valueMap.size());
        List<MidFieldVO> fields = templateView.getFields();
        fields.stream().filter(field -> Objects.nonNull(field.getCustomFieldRelationDtos())).forEach(field -> {
            List<MidFieldRelationVO> customFieldRelationList = field.getCustomFieldRelationDtos();
            customFieldRelationList.forEach(relatedField -> {
                String relatedFieldValue = valueMap.get(relatedField.getStructId());
                if (!StringUtils.isEmpty(relatedFieldValue)) {
                    fieldValueMap.put(relatedField.getStructId(), new Pair<>(relatedField.getName(), relatedFieldValue));
                }
            });
        });

        return fieldValueMap;
    }

    /**
     * 解析动态字段
     * @param valueMap
     * @param templateView
     * @return
     */
    private Map<String, Pair<String, String>> parseDynamicFieldValue2(Map<String, String> valueMap, ContractCustomTemplateInfo templateView) {
        Map<String, Pair<String, String>> fieldValueMap = new HashMap<>(valueMap.size());
        List<TemplateFieldsInfo> fields = templateView.getFields();
        valueMap.forEach((fieldId, valid) -> fields.stream().filter(f -> ObjectUtils.equals(fieldId, f.getStructId())).findFirst().ifPresent(f -> {
            List<MidEnumVO> enums = f.getEnums();
            if (!CollectionUtils.isEmpty(enums)) {
                enums.stream().filter(e -> StringUtils.equals(valid, e.getId().toString())).findFirst().ifPresent(e -> fieldValueMap.put(fieldId, new Pair<>(f.getName(), e.getName())));
            } else {
                fieldValueMap.put(fieldId, new Pair<>(f.getName(), valid));
            }
        }));

        return fieldValueMap;
    }

    /**
     * 根据字段名获取动态值
     * @param fieldName
     * @param fieldValueMap
     * @return
     */
    private String parseValueByFieldName(String fieldName, Map<Long, Pair<String, String>> fieldValueMap) {
        return fieldValueMap.values().stream().filter(pair -> StringUtils.contains(pair.getKey(), fieldName)).findFirst().map(Pair::getValue).orElse(null);
    }

    /**
     * 根据字段名获取动态值
     * @param fieldName
     * @param fieldValueMap
     * @return
     */
    private String parseValueByFieldName2(String fieldName, Map<String, Pair<String, String>> fieldValueMap) {
        return fieldValueMap.values().stream().filter(pair -> StringUtils.contains(pair.getKey(), fieldName)).findFirst().map(Pair::getValue).orElse(null);
    }

    /**
     * 删除项目相关数据
     * @param deleteProjectList
     */
    private void deleteProject(List<EmcProjectBean> deleteProjectList) {
        // 删除采样任务监测方案数据
        List<EmcProjectBean> sampleProjectList = deleteProjectList.stream().filter(p -> Constant.YES.equals(p.getSample())).collect(Collectors.toList());
        if (!sampleProjectList.isEmpty()) {
            List<Long> sampleProjectIdList = sampleProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());
            List<EmcProjSchemeBean> projSchemeList = projSchemeService.getDao().selectListByOneColumnValues(sampleProjectIdList, "projId", Collections.singletonList("id"));
            List<Long> projSchemeIdList = projSchemeList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList());
            List<EmcProjOrderBean> projOrderList = projOrderService.getDao().selectListByOneColumnValues(projSchemeIdList, "projSchemeId", Collections.singletonList("id"));
            projSchemeService.getDao().deleteByIdList(projSchemeIdList);
            projOrderService.getDao().deleteBy(projOrderList, "id");
        }

        // 删除送样任务样品数据
        List<EmcProjectBean> nonSampleProjectList = deleteProjectList.stream().filter(p -> !Constant.YES.equals(p.getSample())).collect(Collectors.toList());
        if (!nonSampleProjectList.isEmpty()) {
            List<Long> nonSampleProjectIdList = nonSampleProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());
            List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(nonSampleProjectIdList, "projId", Arrays.asList("id", "folderId"));
            List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
            List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "orderId", Collections.singletonList("id"));
            List<Long> folderIdList = orderList.stream().map(EmcOrderBean::getFolderId).distinct().collect(Collectors.toList());

            folderService.getDao().deleteByIdList(folderIdList);
            orderService.getDao().deleteByIdList(orderIdList);
            orderItemGroupService.getDao().deleteBy(orderItemGroupList, "id");
        }

        // 删除报告发送信息数据
        List<Long> deleteProjectIdList = deleteProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());
        List<EmcReportSendInfoBean> deleteReportSendInfoList = new ArrayList<>(deleteProjectIdList.size());
        deleteProjectIdList.forEach(projId -> {
            EmcReportSendInfoBean deleteSendInfo = new EmcReportSendInfoBean();
            deleteSendInfo.setProjId(projId);
            deleteReportSendInfoList.add(deleteSendInfo);
        });
        reportSendInfoService.getDao().deleteBy(deleteReportSendInfoList, "projId");

        projectService.getDao().deleteBy(deleteProjectList, "id");
    }

}
