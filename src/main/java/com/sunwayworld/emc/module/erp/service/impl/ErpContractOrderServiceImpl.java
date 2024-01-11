package com.sunwayworld.emc.module.erp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectOtherFeeBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectOtherFeeService;
import com.sunwayworld.emc.module.bp.project.project.service.PfProjectService;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjOrderService;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjSchemeService;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportSendInfoBean;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportSendInfoService;
import com.sunwayworld.emc.module.erp.bean.MidCustomTemplateVO;
import com.sunwayworld.emc.module.erp.bean.MidEnumVO;
import com.sunwayworld.emc.module.erp.bean.MidFieldRelationVO;
import com.sunwayworld.emc.module.erp.bean.MidFieldVO;
import com.sunwayworld.emc.module.erp.bean.MidModuleVO;
import com.sunwayworld.emc.module.erp.bean.RestResult;
import com.sunwayworld.emc.module.erp.bean.contract.MidAddressDTO;
import com.sunwayworld.emc.module.erp.bean.contract.MidOrderAdditionalReportDTO;
import com.sunwayworld.emc.module.erp.bean.contract.MidOrderDTOExt;
import com.sunwayworld.emc.module.erp.bean.contract.MidPlanContractCreateResultDTO;
import com.sunwayworld.emc.module.erp.bean.contract.MidPlanContractToContractPkgDTO;
import com.sunwayworld.emc.module.erp.bean.contract.MidPlanContractToDelegateCompanyDTO;
import com.sunwayworld.emc.module.erp.bean.contract.MidPlanContractToUserContractDTOExt;
import com.sunwayworld.emc.module.erp.bean.contract.MidSampleDTOExt;
import com.sunwayworld.emc.module.erp.bean.contract.MidSamplingTypeDTO;
import com.sunwayworld.emc.module.erp.bean.contract.PlanContractOrderConfirm;
import com.sunwayworld.emc.module.erp.bean.contract.SaveContractOrderOtherFeeReq;
import com.sunwayworld.emc.module.erp.bean.contract.SaveContractOriSkuInfo;
import com.sunwayworld.emc.module.erp.bean.contract.SaveContractSampleSkuInfo;
import com.sunwayworld.emc.module.erp.constant.ErpConstant;
import com.sunwayworld.emc.module.erp.service.ErpContractOrderService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeCostBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorSchemeCostService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorSchemeService;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcContractBean;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcSplitContractBean;
import com.sunwayworld.emc.module.mdm.contract.service.EmcContractService;
import com.sunwayworld.emc.module.mdm.contract.service.EmcSplitContractService;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpBean;
import com.sunwayworld.emc.module.mdm.emp.emp.service.EmcEmpService;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.utils.BeanUtils;
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
 * @author zhengxy@sunwayworld.com 2023年11月09日
 */
@Repository
public class ErpContractOrderServiceImpl implements ErpContractOrderService {

    private static final Logger logger = LoggerFactory.getLogger(ErpContractOrderServiceImpl.class);

    @Autowired
    private PfProjectService projectService;

    @Autowired
    private EmcContractService contractService;

    @Autowired
    private EmcSplitContractService splitContractService;

    @Autowired
    private CoreOrgService coreOrgService;

    @Autowired
    private EmcProjSchemeService projSchemeService;

    @Autowired
    private EmcProjOrderService projOrderService;

    @Autowired
    private EmcMonitorSchemeService monitorSchemeService;

    @Autowired
    private EmcMonitorSchemeCostService monitorSchemeCostService;

    @Autowired
    private EmcOrderService orderService;

    @Autowired
    private EmcOrderItemGroupService orderItemGroupService;

    @Autowired
    private CoreRemoteCallRequestService remoteCallRequestService;

    @Autowired
    private CoreRemoteCallResponseService remoteCallResponseService;

    @Autowired
    private EmcEmpService emcEmpService;

    @Autowired
    private EmcProjectOtherFeeService otherFeeService;

    @Autowired
    private EmcReportSendInfoService reportSendInfoService;

    @Autowired
    private CoreUserService coreUserService;

    @Autowired
    private CoreCodeService coreCodeService;

    @Override
    public void pushPlanContractOrder(EmcProjectBean project) {
        boolean bindPlanContract = NumberUtils.isNumber(project.getExt$Item("contractId"))
                && !ObjectUtils.isEmpty(project.getSplitContractId());
        if (!bindPlanContract) {
            logger.warn("项目【{}】未关联计划合同，无法推送到ERP", project.getProjNo());
            return;
        }

        MidPlanContractToUserContractDTOExt contractDtoExt = buildContract(project);

        logger.info("开始构建计划合同订单信息 -> {}", project.getProjNo());
        boolean sampling = Constant.YES.equals(project.getSample());
        MidOrderDTOExt orderDtoExt = sampling ? buildSamplingOrder(project, contractDtoExt.getMerchantId(), contractDtoExt.getIndustryGbId()) : buildNonSamplingOrder(project, contractDtoExt.getMerchantId(), contractDtoExt.getIndustryGbId());
        contractDtoExt.setOrder(orderDtoExt);

        String jsonStr = JSONObject.toJSONString(contractDtoExt);
        logger.info("推送计划合同订单到ERP --> {}", jsonStr);

        String result = remoteCallRequestService.processSyncRequest("pushPlanContractOrder", jsonStr);
        logger.info("推送计划合同订单到ERP返回结果 --> {}", result);
        RestResult<MidPlanContractCreateResultDTO> restResult = JSON.parseObject(result, new TypeReference<RestResult<MidPlanContractCreateResultDTO>>(){});

        if (StringUtils.equals(restResult.getResultCode(), ErpConstant.SUCCESS_CODE)
                && !ObjectUtils.isEmpty(restResult.getResultData())) {
            // 订单推送成功，更新GBID
            MidPlanContractCreateResultDTO createResult = restResult.getResultData();
            List<MidPlanContractCreateResultDTO.OrdersBean> orders = createResult.getOrders();
            if (Objects.nonNull(orders) && !orders.isEmpty()) {
                MidPlanContractCreateResultDTO.OrdersBean resultOrder = orders.get(0);
                project.setErpGbId(resultOrder.getOrderGbId());
                project.setErpOrderNumber(resultOrder.getOrderNumber());

                projectService.getDao().update(project, "erpGbId", "erpOrderNumber");
            }

            EmcSplitContractBean updateSplitContract = new EmcSplitContractBean();
            updateSplitContract.setId(project.getSplitContractId());
            updateSplitContract.setErpContractGbId(String.valueOf(createResult.getContractGbId()));
            updateSplitContract.setErpContractNumber(createResult.getContractNumber());
            updateSplitContract.setStatus("2"); // 表示已下单

            splitContractService.getDao().update(updateSplitContract, "erpContractGbId", "erpContractNumber", "status");

            return;
        }

        // 如果推送失败，直接抛出异常，让项目回退
        throw new UnexpectedException(restResult.getMessage());
    }

    @Override
    public String pushPlanContractOrderToErp(String jsonStr) {
        return HttpClientManager.getInstance().sendHttpPost(getErpServiceUrl() + ErpConstant.PUSH_PLAN_CONTRACT_ORDER_INTERFACE, jsonStr);
    }

    @Override
    public RestResult<Boolean> pushPlanContractOrderConfirm(PlanContractOrderConfirm orderConfirm) {
        logger.info("[Erp-Push-Plan-Contract-Order-Confirm]=======================");
        String requestJson = JSON.toJSONString(orderConfirm);
        logger.info(requestJson);

        String orderGbId = orderConfirm.getOrderGbId();
        List<EmcProjectBean> targetProjectList = projectService.getDao().selectListByOneColumnValue(orderGbId, "erpGbId");
        if (targetProjectList.isEmpty()) {
            RestResult<Boolean> restResult = new RestResult<>("160", "该计划合同订单在LIMS中不存在");
            createRemoteCall("pushPlanContractOrderConfirm", requestJson, JSON.toJSONString(restResult));

            return restResult;
        }

        List<EmcProjectBean> validProjectList = targetProjectList.stream().filter(p -> StringUtils.equals(p.getStatus(), "erpConfirm")).collect(Collectors.toList());
        if (validProjectList.isEmpty()) {
            RestResult<Boolean> restResult = new RestResult<>("161", "该计划合同订单状态存在异常");
            createRemoteCall("pushPlanContractOrderConfirm", requestJson, JSON.toJSONString(restResult));

            return restResult;
        }

        // 生成业务数据
        String orgId = validProjectList.get(0).getOrgId();
        EmcEmpBean saleEmp = emcEmpService.selectFirstByFilter(SearchFilter.instance().match("orgId", orgId).filter(MatchPattern.SEQ).match("QUOTER", Constant.YES).filter(MatchPattern.SEQ));
        Long defaultSaleGbId = Objects.nonNull(saleEmp) ? saleEmp.getErpGbId() : null;

        setUserLogin(defaultSaleGbId + "");
        logger.info("设置默认报价员为登录用户 -> {}", defaultSaleGbId);
        projectService.createProjectBpData(validProjectList);

        RestResult<Boolean> restResult = new RestResult<>("0", "success");
        createRemoteCall("pushPlanContractOrderConfirm", requestJson, JSON.toJSONString(restResult));

        return restResult;
    }

    @Override
    public List<MidSamplingTypeDTO> fetchErpSamplingType(String merchantId) {
        String url = getErpServiceUrl() + String.format(ErpConstant.FETCH_SAMPLING_TYPE_INTERFACE, merchantId);
        logger.info("查询样品类型 -> {}", url);
        String result = HttpClientManager.getInstance().sendHttpGet(url);
        RestResult<List<MidSamplingTypeDTO>> restResult = JSON.parseObject(result, new TypeReference<RestResult<List<MidSamplingTypeDTO>>>(){});

        if (StringUtils.equals(restResult.getResultCode(), ErpConstant.SUCCESS_CODE)
                && !ObjectUtils.isEmpty(restResult.getResultData())) {

            return restResult.getResultData();
        }

        logger.error("获取ERP样品类型数据失败 -> {}", result);

        return Collections.emptyList();
    }

    @Override
    public JSONObject fetchErpIndustryTree() {
        String url = getErpServiceUrl() + ErpConstant.FETCH_INDUSTRY_TREE_INTERFACE;
        logger.info("获取行业群数据树地址 -> {}", url);
        String result = HttpClientManager.getInstance().sendHttpPost(url, null);
        logger.info("获取行业群数据树返回结果 -> {}", result);

        // RestResult<JSONObject> restResult = JSON.parseObject(result, new TypeReference<RestResult<JSONObject>>(){});
        // 采用上面的解析方式时，出现过类型错误的情况，实际解析的类型与传参类型不一致

        JSONObject resultObject = JSON.parseObject(result, JSONObject.class);
        String resultCode = resultObject.getString("resultCode");
        if (ErpConstant.SUCCESS_CODE.equals(resultCode)) {
            return (JSONObject) resultObject.get("resultData");
        }

        logger.error("获取行业群数据树失败");

        return new JSONObject();
    }

    @Override
    public CoreOrgBean getMerchantOrg() {
        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");

        return coreOrgService.selectById(parentOrgId);
    }

    @Override
    public Long getErpUserId() {
        String loginUserId = LocalContextHelper.getLoginUserId();
        EmcEmpBean emp = emcEmpService.selectById(loginUserId);

        return emp.getErpGbId();
    }

    @Override
    public Long getDefaultSaleGbId() {
        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
        EmcEmpBean saleEmp = emcEmpService.selectFirstByFilter(SearchFilter.instance().match("orgId", parentOrgId).filter(MatchPattern.SEQ).match("QUOTER", Constant.YES).filter(MatchPattern.SEQ));

        return Objects.nonNull(saleEmp) ? saleEmp.getErpGbId() : null;
    }

    private String getErpServiceUrl() {
        CoreOrgBean merchantOrg = getMerchantOrg();
        String merchantId = merchantOrg.getExt$Item("erpMerchantId");
        String constantKey = StringUtils.isEmpty(merchantId) ? "ERP_SERVICE_URL" : "ERP_SERVICE_URL_" + merchantId;
        return Optional.ofNullable(ApplicationContextHelper.getConstantValue(constantKey)).orElse(ErpConstant.ERP_BASE_URL);
    }

    /**
     * 构建合同信息
     * @param project 项目信息
     * @return 合同对象
     */
    private MidPlanContractToUserContractDTOExt buildContract(EmcProjectBean project) {
        CoreOrgBean merchantOrg = getMerchantOrg();
        MidPlanContractToUserContractDTOExt contractDtoExt = new MidPlanContractToUserContractDTOExt();
        contractDtoExt.setbCheck(false);
        contractDtoExt.setMerchantId(merchantOrg.getExt$Item("erpMerchantId"));
        contractDtoExt.setOperatorGbId(getErpUserId());

        String contractId = project.getExt$Item("contractId");
        if (NumberUtils.isNumber(contractId)) {
            EmcContractBean contract = contractService.selectById(NumberUtils.parseLong(contractId));
            contractDtoExt.setPlanContractGbId(contract.getErpGbId());
        }

        Long splitContractId = project.getSplitContractId();
        if (!ObjectUtils.isEmpty(splitContractId)) {
            EmcSplitContractBean splitContract = splitContractService.selectById(splitContractId);
            contractDtoExt.setPlanContractBindItemGbId(NumberUtils.parseLong(splitContract.getErpGbId()));
            contractDtoExt.setBusinessOpportunityGbId(NumberUtils.parseLong(splitContract.getBusinessOpportunityGbId()));
            contractDtoExt.setIndustryGbId(NumberUtils.parseLong(splitContract.getIndustryGbId()));
            contractDtoExt.setProductionLineGbId(NumberUtils.parseLong(splitContract.getProductionLineGbId()));
            contractDtoExt.setPrice(splitContract.getPrice());
        }

        // 商户模板ID
        MidCustomTemplateVO customTemplate = buildCustomTemplate();
        if (Objects.nonNull(customTemplate) && Objects.nonNull(customTemplate.getTemplate())) {
            contractDtoExt.setCustomTemplateGbId(customTemplate.getTemplate().getGbId());
        }

        // 报价信息
        MidPlanContractToContractPkgDTO pkgDto = new MidPlanContractToContractPkgDTO();
        pkgDto.setPkgSource(0);
        pkgDto.setSaleGbId(getDefaultSaleGbId());
        if (NumberUtils.isNumber(merchantOrg.getExt$Item("erpGbId"))) {
            pkgDto.setOrgGbId(NumberUtils.parseLong(merchantOrg.getExt$Item("erpGbId")));
        }
        contractDtoExt.setPkg(pkgDto);

        // 委托方信息
        if (NumberUtils.isNumber(project.getErpContactGbId())) {
            MidPlanContractToDelegateCompanyDTO delegateCompany = new MidPlanContractToDelegateCompanyDTO();
            delegateCompany.setContactGbId(NumberUtils.parseLong(project.getErpContactGbId()));
            delegateCompany.setContactName(project.getEntrustOrgContact());
            delegateCompany.setPhoneNum(project.getEntrustOrgPhone());
            delegateCompany.setEmail(project.getEntrustOrgEmail());
            contractDtoExt.setDelegateCompany(delegateCompany);
        }

        return contractDtoExt;
    }

    /**
     * 构建自定义基础模板，每个商户是不一样的
     * @return 基础模板
     */
    private MidCustomTemplateVO buildCustomTemplate() {
        CoreOrgBean merchantOrg = getMerchantOrg();
        String merchantId = merchantOrg.getExt$Item("erpMerchantId");
        if (StringUtils.isEmpty(merchantId)) {
            return new MidCustomTemplateVO();
        }

        switch (merchantId) {
            case ErpConstant.MERCHANT_5:
                return JSON.parseObject(ErpConstant.CUSTOM_TEMPLATE_5, MidCustomTemplateVO.class);
            case ErpConstant.MERCHANT_34:
                return JSON.parseObject(ErpConstant.CUSTOM_TEMPLATE_34, MidCustomTemplateVO.class);
            case ErpConstant.MERCHANT_35:
                return JSON.parseObject(ErpConstant.CUSTOM_TEMPLATE_35, MidCustomTemplateVO.class);
            case ErpConstant.MERCHANT_36:
                return JSON.parseObject(ErpConstant.CUSTOM_TEMPLATE_36, MidCustomTemplateVO.class);
            default:
                break;
        }

        return new MidCustomTemplateVO();
    }

    /**
     * 构建送样订单
     * @param project 项目信息
     * @param merchantId 商户ID
     * @param industryGbId 行业群ID
     * @return 订单
     */
    private MidOrderDTOExt buildSamplingOrder(EmcProjectBean project, String merchantId, Long industryGbId) {
        MidOrderDTOExt orderDtoExt = new MidOrderDTOExt();
        orderDtoExt.setSamplingType(ErpConstant.ERP_SAMPLING_TYPE);

        // 行业群信息
        buildIndustryInfo(project, orderDtoExt, industryGbId);

        // 其他费用信息
        buildOtherFeeInfo(project, orderDtoExt);

        // 报告信息
        buildReportInfo(project, orderDtoExt);

        // 动态字段信息
        buildDynamicTemplateData(project, orderDtoExt);

        // 获取ERP样品类型
        List<MidSamplingTypeDTO> erpSamplingTypeList = fetchErpSamplingType(merchantId);

        // 样品信息
        List<EmcProjSchemeBean> projSchemeList = projSchemeService.selectListByFilter(SearchFilter.instance().match("projId", project.getId()).filter(MatchPattern.SEQ));
        List<Long> projSchemeIdList = projSchemeList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList());
        List<EmcProjOrderBean> projOrderList = projOrderService.selectListByFilter(SearchFilter.instance().match("projSchemeId", projSchemeIdList).filter(MatchPattern.OR));

        List<String> monitorTmplIdList = projSchemeList.stream().map(EmcProjSchemeBean::getMonitorTmplId).distinct().collect(Collectors.toList());
        List<Long> capacityItemGroupIdList = projOrderList.stream().map(EmcProjOrderBean::getItemGroupId).distinct().collect(Collectors.toList());
        List<String> schemeNameList = projOrderList.stream().map(EmcProjOrderBean::getSchemeName).distinct().collect(Collectors.toList());

        List<EmcMonitorSchemeBean> monitorSchemeList = monitorSchemeService.selectListByFilter(SearchFilter.instance().match("monitorTmplId", monitorTmplIdList).filter(MatchPattern.OR)
                .match("schemeName", schemeNameList).filter(MatchPattern.OR).match("itemGroupId", capacityItemGroupIdList).filter(MatchPattern.OR));

        List<EmcMonitorSchemeCostBean> schemeCostList = monitorSchemeCostService.selectListByFilter(SearchFilter.instance().match("monitorTmplId", monitorTmplIdList).filter(MatchPattern.OR).match("schemeName", schemeNameList).filter(MatchPattern.OR));

        List<MidSampleDTOExt> sampleList = new ArrayList<>(projSchemeList.size());
        projSchemeList.forEach(scheme -> {
            MidSampleDTOExt sampleDtoExt = new MidSampleDTOExt();
            String orderCategory = scheme.getExt$Item("monitortmplname");
            sampleDtoExt.setSamplingTypeName(orderCategory);
            erpSamplingTypeList.stream().filter(type -> StringUtils.equals(orderCategory, type.getTypeName()) && NumberUtils.isNumber(type.getTypeNo())).findFirst().ifPresent(type -> sampleDtoExt.setSamplingTypeNo(NumberUtils.parseLong(type.getTypeNo())));

            sampleDtoExt.setPointName(scheme.getMonitorPointName());
            sampleDtoExt.setSubject(scheme.getMonitorPointName());
            sampleDtoExt.setSamplingFrequency(scheme.getFrequency().intValue());
            sampleDtoExt.setSamplingPeriod(scheme.getCycle().intValue());
            sampleDtoExt.setSamplingPointCount(1);
            sampleDtoExt.setRisks(Collections.singletonList(1));

            List<EmcProjOrderBean> curOrderList = projOrderList.stream().filter(po -> ObjectUtils.equals(scheme.getId(), po.getProjSchemeId())).collect(Collectors.toList());
            Long sampleNum = curOrderList.stream().map(EmcProjOrderBean::getSampleNum).max(Comparator.comparing(Function.identity())).orElse(1L);
            sampleDtoExt.setSamplingNum(sampleNum.intValue());
            sampleDtoExt.setSampleCnt(sampleNum.intValue());

            //
            List<Long> curItemGroupIdList = curOrderList.stream().map(EmcProjOrderBean::getItemGroupId).distinct().collect(Collectors.toList());
            List<EmcMonitorSchemeBean> curMonitorSchemeList = monitorSchemeList.stream().filter(s -> StringUtils.equals(s.getMonitorTmplId(), scheme.getMonitorTmplId()) && curItemGroupIdList.contains(s.getItemGroupId())).collect(Collectors.toList());
            Map<Long, EmcMonitorSchemeBean> curMonitorSchemeMap = curMonitorSchemeList.stream().collect(Collectors.toMap(EmcMonitorSchemeBean::getItemGroupId, Function.identity(), (v1, v2) -> v1));

            // 按方案名称分组
            List<EmcProjOrderBean> defaultOrderList = curOrderList.stream().filter(o -> StringUtils.equals(o.getSchemeName(), "default")).collect(Collectors.toList());
            List<EmcProjOrderBean> packageOrderList = curOrderList.stream().filter(o -> !StringUtils.equals(o.getSchemeName(), "default")).collect(Collectors.toList());

            Map<String, List<EmcProjOrderBean>> packageOrderMap = packageOrderList.stream().collect(Collectors.groupingBy(EmcProjOrderBean::getSchemeName));
            List<SaveContractOriSkuInfo> skuList = new ArrayList<>();
            List<SaveContractSampleSkuInfo> skuInfoList = new ArrayList<>();

            Map<String, String> skuRemarkMap = new HashMap<>();

            // 单项SKU
            defaultOrderList.forEach(projOrder -> Optional.ofNullable(curMonitorSchemeMap.get(projOrder.getItemGroupId())).ifPresent(monitorScheme -> {
                skuList.add(createSingleSku(monitorScheme));
                skuInfoList.add(createSingleSkuInfo(monitorScheme));
                String remark = projOrder.getExt$Item("remark");
                if (!StringUtils.isEmpty(remark)) {
                    skuRemarkMap.put(monitorScheme.getSkuGbId(), remark);
                }
            }));

            // 套餐SKU
            packageOrderMap.forEach((schemeName, schemeOrderList) -> {
                EmcMonitorSchemeCostBean schemeCost = schemeCostList.stream().filter(cost -> StringUtils.equals(cost.getMonitorTmplId(), scheme.getMonitorTmplId()) && StringUtils.equals(cost.getSchemeName(), schemeName)).findFirst().orElseGet(EmcMonitorSchemeCostBean::new);

                SaveContractOriSkuInfo packageSku = createPackageSku(schemeCost);
                SaveContractSampleSkuInfo packageSkuInfo = createPackageSkuInfo(schemeCost);

                List<SaveContractOriSkuInfo> singleSkuList = new ArrayList<>(schemeOrderList.size());
                List<SaveContractSampleSkuInfo> singleSkuInfoList = new ArrayList<>(schemeOrderList.size());

                schemeOrderList.forEach(projOrder -> Optional.ofNullable(curMonitorSchemeMap.get(projOrder.getItemGroupId())).ifPresent(monitorScheme -> {
                    singleSkuList.add(createSingleSku(monitorScheme));
                    singleSkuInfoList.add(createSingleSkuInfo(monitorScheme));
                    String remark = projOrder.getExt$Item("remark");
                    if (!StringUtils.isEmpty(remark)) {
                        skuRemarkMap.put(monitorScheme.getSkuGbId(), remark);
                    }
                }));
                packageSku.setSingles(singleSkuList);
                packageSkuInfo.setSingles(singleSkuInfoList);

                skuList.add(packageSku);
                skuInfoList.add(packageSkuInfo);
            });

            sampleDtoExt.setSkuRemarks(skuRemarkMap);
            sampleDtoExt.setSkus(skuList);
            sampleDtoExt.setSkuInfoList(skuInfoList);
            List<Long> skuGbIdList = skuInfoList.stream().map(SaveContractSampleSkuInfo::getSkuGbId).collect(Collectors.toList());
            sampleDtoExt.setSkuGbIds(skuGbIdList);

            sampleList.add(sampleDtoExt);
        });
        orderDtoExt.setSamples(sampleList);

        return orderDtoExt;
    }

    /**
     * 构建送样订单信息
     * @param project 项目信息
     * @param merchantId 商户ID
     * @param industryGbId 行业群ID
     * @return 订单
     */
    private MidOrderDTOExt buildNonSamplingOrder(EmcProjectBean project, String merchantId, Long industryGbId) {
        MidOrderDTOExt orderDtoExt = new MidOrderDTOExt();
        orderDtoExt.setSamplingType(ErpConstant.ERP_NON_SAMPLING_TYPE);

        // 行业群信息
        buildIndustryInfo(project, orderDtoExt, industryGbId);

        // 其他费用信息
        buildOtherFeeInfo(project, orderDtoExt);

        // 报告信息
        buildReportInfo(project, orderDtoExt);

        // 动态字段信息
        buildDynamicTemplateData(project, orderDtoExt);

        // 获取ERP样品类型
        List<MidSamplingTypeDTO> erpSamplingTypeList = fetchErpSamplingType(merchantId);

        // 样品信息
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValue(project.getId(), "projId");
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "orderId");

        List<String> monitorTmplIdList = orderList.stream().map(EmcOrderBean::getMonitorTmplId).distinct().collect(Collectors.toList());
        List<Long> capacityItemGroupIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
        List<EmcMonitorSchemeBean> monitorSchemeList = monitorSchemeService.selectListByFilter(SearchFilter.instance().match("monitorTmplId", monitorTmplIdList).filter(MatchPattern.OR).match("itemGroupId", capacityItemGroupIdList).filter(MatchPattern.OR));

        List<String> schemeNameList = orderItemGroupList.stream().map(EmcOrderItemGroupBean::getSchemeName).distinct().collect(Collectors.toList());
        List<EmcMonitorSchemeCostBean> schemeCostList = monitorSchemeCostService.selectListByFilter(SearchFilter.instance().match("monitorTmplId", monitorTmplIdList).filter(MatchPattern.OR).match("schemeName", schemeNameList).filter(MatchPattern.OR));

        List<MidSampleDTOExt> sampleList = new ArrayList<>(orderList.size());
        orderList.forEach(order -> {
            MidSampleDTOExt sampleDtoExt = new MidSampleDTOExt();
            sampleDtoExt.setSamplingTypeName(order.getOrderCategory());

            erpSamplingTypeList.stream().filter(type -> StringUtils.equals(order.getOrderCategory(), type.getTypeName()) && NumberUtils.isNumber(type.getTypeNo())).findFirst().ifPresent(type -> sampleDtoExt.setSamplingTypeNo(NumberUtils.parseLong(type.getTypeNo())));

            sampleDtoExt.setPointName(order.getClientOrderName());
            sampleDtoExt.setSubject(order.getClientOrderName());
            sampleDtoExt.setSamplingNum(1);
            sampleDtoExt.setSampleCnt(1);
            // 默认无风险
            sampleDtoExt.setRisks(Collections.singletonList(ErpConstant.NO_RISK));

            List<EmcOrderItemGroupBean> curOrderItemGroupList = orderItemGroupList.stream().filter(group -> ObjectUtils.equals(group.getOrderId(), order.getId())).collect(Collectors.toList());
            //
            List<Long> curItemGroupIdList = curOrderItemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
            List<EmcMonitorSchemeBean> curMonitorSchemeList = monitorSchemeList.stream().filter(s -> StringUtils.equals(s.getMonitorTmplId(), order.getMonitorTmplId()) && curItemGroupIdList.contains(s.getItemGroupId())).collect(Collectors.toList());
            Map<Long, EmcMonitorSchemeBean> curMonitorSchemeMap = curMonitorSchemeList.stream().collect(Collectors.toMap(EmcMonitorSchemeBean::getItemGroupId, Function.identity(), (v1, v2) -> v1));

            // 按方案名称分组
            List<EmcOrderItemGroupBean> defaultOrderList = curOrderItemGroupList.stream().filter(o -> StringUtils.equals(o.getSchemeName(), "default")).collect(Collectors.toList());
            List<EmcOrderItemGroupBean> packageOrderList = curOrderItemGroupList.stream().filter(o -> !StringUtils.equals(o.getSchemeName(), "default")).collect(Collectors.toList());

            Map<String, List<EmcOrderItemGroupBean>> packageOrderMap = packageOrderList.stream().collect(Collectors.groupingBy(EmcOrderItemGroupBean::getSchemeName));
            List<SaveContractOriSkuInfo> skuList = new ArrayList<>();
            List<SaveContractSampleSkuInfo> skuInfoList = new ArrayList<>();

            Map<String, String> skuRemarkMap = new HashMap<>();

            // 单项SKU
            defaultOrderList.forEach(orderItemGroup -> Optional.ofNullable(curMonitorSchemeMap.get(orderItemGroup.getItemGroupId())).ifPresent(monitorScheme -> {
                skuList.add(createSingleSku(monitorScheme));
                skuInfoList.add(createSingleSkuInfo(monitorScheme));

                String remark = orderItemGroup.getExt$Item("remark");
                if (!StringUtils.isEmpty(remark)) {
                    skuRemarkMap.put(monitorScheme.getSkuGbId(), remark);
                }
            }));

            // 套餐SKU
            packageOrderMap.forEach((schemeName, schemeOrderList) -> {
                EmcMonitorSchemeCostBean schemeCost = schemeCostList.stream().filter(cost -> StringUtils.equals(cost.getMonitorTmplId(), order.getMonitorTmplId()) && StringUtils.equals(cost.getSchemeName(), schemeName)).findFirst().orElseGet(EmcMonitorSchemeCostBean::new);

                SaveContractOriSkuInfo packageSku = createPackageSku(schemeCost);
                SaveContractSampleSkuInfo packageSkuInfo = createPackageSkuInfo(schemeCost);

                List<SaveContractOriSkuInfo> singleSkuList = new ArrayList<>(schemeOrderList.size());
                List<SaveContractSampleSkuInfo> singleSkuInfoList = new ArrayList<>(schemeOrderList.size());

                schemeOrderList.forEach(orderItemGroup -> Optional.ofNullable(curMonitorSchemeMap.get(orderItemGroup.getItemGroupId())).ifPresent(monitorScheme -> {
                    singleSkuList.add(createSingleSku(monitorScheme));
                    singleSkuInfoList.add(createSingleSkuInfo(monitorScheme));

                    String remark = orderItemGroup.getExt$Item("remark");
                    if (!StringUtils.isEmpty(remark)) {
                        skuRemarkMap.put(monitorScheme.getSkuGbId(), remark);
                    }
                }));
                packageSku.setSingles(singleSkuList);
                packageSkuInfo.setSingles(singleSkuInfoList);

                skuList.add(packageSku);
                skuInfoList.add(packageSkuInfo);
            });

            sampleDtoExt.setSkuRemarks(skuRemarkMap);
            sampleDtoExt.setSkus(skuList);
            sampleDtoExt.setSkuInfoList(skuInfoList);
            List<Long> skuGbIdList = skuInfoList.stream().map(SaveContractSampleSkuInfo::getSkuGbId).collect(Collectors.toList());
            sampleDtoExt.setSkuGbIds(skuGbIdList);

            sampleList.add(sampleDtoExt);
        });
        orderDtoExt.setSamples(sampleList);

        return orderDtoExt;
    }

    /**
     * 构建行业群相关信息，涉及三层结构
     * @param project 项目信息
     * @param orderDtoExt 订单信息
     * @param industryGbId 计划子合同对应的行业群ID
     */
    private void buildIndustryInfo(EmcProjectBean project, MidOrderDTOExt orderDtoExt, Long industryGbId) {
        logger.info("-> 构建行业群信息");
        CoreOrgBean merchantOrg = getMerchantOrg();
        String merchantId = merchantOrg.getExt$Item("erpMerchantId");
        orderDtoExt.setMerchantContractBusinessSettingsId(ErpConstant.ERP_BIZ_CATEGORY_ID_MAPPING.get(merchantId));

        JSONObject industryTree = fetchErpIndustryTree();

        // 解析行业群数据树三层结构
        JSONArray rootChildNodes = industryTree.getJSONArray("childrenNodes");
        rootChildNodes.forEach(node -> {
            JSONObject rootChild = (JSONObject) node;
            JSONObject firstLevelNode = rootChild.getJSONObject("currentNode");
            JSONArray firstChildNodes = rootChild.getJSONArray("childrenNodes");

            firstChildNodes.forEach(firstNode -> {
                JSONObject firstChild = (JSONObject) firstNode;
                JSONObject secondLevelNode = firstChild.getJSONObject("currentNode");
                JSONArray secondChildNodes = firstChild.getJSONArray("childrenNodes");

                secondChildNodes.forEach(secondNode -> {
                    JSONObject thirdChild = (JSONObject) secondNode;
                    JSONObject thirdLevelNode = thirdChild.getJSONObject("currentNode");
                    String thirdLevelGbId = thirdLevelNode.getString("gbId");

                    if (StringUtils.equals(thirdLevelGbId, String.valueOf(industryGbId))) {
                        orderDtoExt.setParentIndustryCode(firstLevelNode.getString("code"));
                        orderDtoExt.setParentIndustryGbId(firstLevelNode.getString("gbId"));
                        orderDtoExt.setParentIndustryName(firstLevelNode.getString("name"));

                        orderDtoExt.setCurrentIndustryCode(secondLevelNode.getString("code"));
                        orderDtoExt.setCurrentIndustryGbId(secondLevelNode.getString("gbId"));
                        orderDtoExt.setCurrentIndustryName(secondLevelNode.getString("name"));

                        orderDtoExt.setChildIndustryCode(thirdLevelNode.getString("code"));
                        orderDtoExt.setChildIndustryGbId(thirdLevelGbId);
                        orderDtoExt.setChildIndustryName(thirdLevelNode.getString("name"));
                    }
                });
            });
        });
    }

    /**
     * 构建其他费用信息
     * @param project 项目信息
     * @param orderDtoExt 订单信息
     */
    private void buildOtherFeeInfo(EmcProjectBean project, MidOrderDTOExt orderDtoExt) {
        logger.info("-> 构建其他费用信息");
        List<EmcProjectOtherFeeBean> projectOtherFeeList = otherFeeService.getDao().selectListByOneColumnValue(project.getId(), "projId");
        List<EmcProjectOtherFeeBean> validOtherFeeList = projectOtherFeeList.stream().filter(fee -> !ObjectUtils.isEmpty(fee.getFeeType())).collect(Collectors.toList());

        List<SaveContractOrderOtherFeeReq> otherFeeList = new ArrayList<>();
        validOtherFeeList.forEach(fee -> {
            SaveContractOrderOtherFeeReq otherFee = new SaveContractOrderOtherFeeReq();
            otherFee.setFeeType(fee.getFeeType());

            // 费用单价和费用金额存的是元，传参的时候需要转换为分
            if (NumberUtils.isNumber(fee.getFeeSinglePrice())) {
                double convertedVal = NumberUtils.parseDouble(fee.getFeeSinglePrice()) * 100;
                otherFee.setFeeSinglePrice(((Double) convertedVal).longValue());
            }

            if (NumberUtils.isNumber(fee.getAmount())) {
                double convertedVal = NumberUtils.parseDouble(fee.getAmount()) * 100;
                otherFee.setAmount(((Double) convertedVal).longValue());
            }

            otherFee.setFeeCount(fee.getFeeCount());
            otherFee.setFeeName(fee.getFeeName());

            otherFeeList.add(otherFee);
        });

        orderDtoExt.setOtherFeeList(otherFeeList);
    }

    /**
     * 构建报告相关信息
     * @param project 项目信息
     * @param orderDtoExt 订单信息
     */
    private void buildReportInfo(EmcProjectBean project, MidOrderDTOExt orderDtoExt) {
        logger.info("-> 构建报告信息");
        // 保密度
        orderDtoExt.setSecurityLevel(NumberUtils.isNumber(project.getSecurityLevel()) ? NumberUtils.parseInt(project.getSecurityLevel()) : 1);

        orderDtoExt.setDeliverableType(1);
        orderDtoExt.setRemarks(project.getRemark());
        // 默认同委托方，1->同委托方地址，2->其他
        orderDtoExt.setReportCompanyAddrType(1);
        orderDtoExt.setReportTitleType(1);

        // 报告类型，当报告类型为其他时，需要填写报告费用
        String reportType = project.getExt$Item("reportType");
        orderDtoExt.setReportType(NumberUtils.isNumber(reportType) ? NumberUtils.parseInt(reportType) : 1);
        orderDtoExt.setReportFee(NumberUtils.isNumber(project.getReportFee()) ? NumberUtils.parseLong(project.getReportFee()) * 100 : null);

        orderDtoExt.setRecipient(project.getAddressee());
        orderDtoExt.setRecipientPhone(project.getRecipientPhone());

        orderDtoExt.setReportTitle(project.getReportCnName());
        orderDtoExt.setEnReportTitle(project.getReportEnName());
        orderDtoExt.setEnReportCompanyAddr(project.getReportEnAddress());

        MidAddressDTO addressDto = new MidAddressDTO();
        addressDto.setAddress(project.getReportCnAddress());
        addressDto.setDetail(project.getReportCnAddress());
        addressDto.setEnAddress(project.getReportEnAddress());

        orderDtoExt.setReportCompanyAddrModel(addressDto);

        // 报告寄送地址
        MidAddressDTO sendAddress = new MidAddressDTO();
        sendAddress.setAddress(project.getReportPostAddress());
        sendAddress.setDetail(project.getReportPostAddress());
        orderDtoExt.setReportSendAddrModel(sendAddress);

        // 服务周期
        orderDtoExt.setPeriodType(NumberUtils.isNumber(project.getPeriodType()) ? NumberUtils.parseInt(project.getPeriodType()) : 1);
        orderDtoExt.setPeriod(NumberUtils.isNumber(project.getPeriod()) ? NumberUtils.parseInt(project.getPeriod()) : null);
        orderDtoExt.setQualificationType(NumberUtils.isNumber(project.getReportAptitude()) ? NumberUtils.parseInt(project.getReportAptitude()) : 0);

        // 额外报告信息
        List<EmcReportSendInfoBean> sendInfoList = reportSendInfoService.getDao().selectListByOneColumnValue(project.getId(), "projId");
        if (!sendInfoList.isEmpty()) {
            EmcReportSendInfoBean basicSendInfo = sendInfoList.get(0);
            List<MidOrderAdditionalReportDTO> additionalReportList = new ArrayList<>();
            // 中文报告
            if (Constant.YES.equals(basicSendInfo.getNeedCnReport()) || (Objects.nonNull(basicSendInfo.getCnReportCnt()))) {
                MidOrderAdditionalReportDTO additionalReport = new MidOrderAdditionalReportDTO();
                additionalReport.setReportType(ErpConstant.CN_REPORT_TYPE);
                additionalReport.setNeedElectronicType(Constant.YES.equals(basicSendInfo.getNeedCnReport()) ? ErpConstant.NEED_REPORT : ErpConstant.NOT_NEED_REPORT);
                additionalReport.setPaperReportCnt(Optional.ofNullable(basicSendInfo.getCnReportCnt()).orElse(0));
                additionalReportList.add(additionalReport);
            }
            // 英文报告
            if (Constant.YES.equals(basicSendInfo.getNeedEnReport()) || (Objects.nonNull(basicSendInfo.getEnReportCnt()))) {
                MidOrderAdditionalReportDTO additionalReport = new MidOrderAdditionalReportDTO();
                additionalReport.setReportType(ErpConstant.EN_REPORT_TYPE);
                additionalReport.setNeedElectronicType(Constant.YES.equals(basicSendInfo.getNeedEnReport()) ? ErpConstant.NEED_REPORT : ErpConstant.NOT_NEED_REPORT);
                additionalReport.setPaperReportCnt(Optional.ofNullable(basicSendInfo.getEnReportCnt()).orElse(0));
                additionalReportList.add(additionalReport);
            }
            // 中英文报告
            if (Constant.YES.equals(basicSendInfo.getNeedCnEnReport()) || (Objects.nonNull(basicSendInfo.getCnEnReportCnt()))) {
                MidOrderAdditionalReportDTO additionalReport = new MidOrderAdditionalReportDTO();
                additionalReport.setReportType(ErpConstant.CN_EN_REPORT_TYPE);
                additionalReport.setNeedElectronicType(Constant.YES.equals(basicSendInfo.getNeedCnEnReport()) ? ErpConstant.NEED_REPORT : ErpConstant.NOT_NEED_REPORT);
                additionalReport.setPaperReportCnt(Optional.ofNullable(basicSendInfo.getCnEnReportCnt()).orElse(0));
                additionalReportList.add(additionalReport);
            }
            orderDtoExt.setAdditionalReports(additionalReportList);
        }
    }

    /**
     * 构建动态基础模板数据，这块相对比较复杂，取值涉及多种转换
     * 基础模板字段值主要有两种：手动填写的值和下拉选项
     *
     * @param project 项目信息
     * @param orderDtoExt 订单信息
     */
    private void buildDynamicTemplateData(EmcProjectBean project, MidOrderDTOExt orderDtoExt) {
        logger.info("-> 构建动态字段信息 -> {}", JSON.toJSONString(project));
        logger.info("订单信息 -> {}", JSON.toJSONString(orderDtoExt));
        MidCustomTemplateVO customTemplate = buildCustomTemplate();
        List<MidModuleVO> modules = customTemplate.getModules();
        List<MidFieldVO> fields = customTemplate.getFields();
        Map<Long, List<MidFieldVO>> moduleFieldMap = fields.stream().collect(Collectors.groupingBy(MidFieldVO::getCustomFieldModuleGbId));

        // 报告用途、前处理方式备注、检测方法指定方、报告抬头，这些字段需要根据系统编码名称去匹配基础模板中的下拉选项
        List<String> codeCategoryList = Arrays.asList("reportUse", "pretreatmentRemark", "testMethodAssigner", "reportTitle");
        List<CoreCodeBean> codeList = coreCodeService.getDao().selectListByOneColumnValues(codeCategoryList, "codeCategoryId");
        logger.info("系统编码列表 -> {}", JSON.toJSONString(codeList));

        // 订单信息
        MidModuleVO orderModule = modules.stream().filter(module -> ErpConstant.CUSTOM_TEMPLATE_OF_ORDER.equals(module.getName())).findFirst().orElseGet(MidModuleVO::new);
        Optional.ofNullable(moduleFieldMap.get(orderModule.getGbId())).ifPresent(moduleFields -> {
            logger.info(JSON.toJSONString(moduleFields));
            // coreFieldType 为0表示是动态字段，其他值表示有单独的字段，此处只需要处理为0的情况
            List<MidFieldVO> dynamicFieldList = moduleFields.stream().filter(field -> field.getCoreFieldType() == 0).collect(Collectors.toList());
            Map<String, String> orderValueMap = new HashMap<>(dynamicFieldList.size());

            // LIMS 业务类型对应ERP项目类型
            logger.info("LIMS 业务类型对应ERP项目类型");
            dynamicFieldList.stream().filter(field -> ErpConstant.BIZ_CATEGORY_FIELD_NAME.equals(field.getName())).findFirst().ifPresent(field -> {
                List<MidEnumVO> enums = field.getEnums();
                if (Objects.nonNull(enums) && !enums.isEmpty()) {
                    String bizCategoryName = project.getExt$Item("bizCategoryName");
                    enums.stream().filter(e -> StringUtils.equals(e.getName(), bizCategoryName)).findFirst().ifPresent(e -> orderValueMap.put(field.getStructId() + "", e.getId() + ""));
                }
            });

            // 采样方式
            logger.info("采样方式");
            dynamicFieldList.stream().filter(field -> ErpConstant.SAMPLE_FIELD_NAME.equals(field.getName())).findFirst().ifPresent(field -> {
                List<MidEnumVO> enums = field.getEnums();
                if (Objects.nonNull(enums) && !enums.isEmpty()) {
                    boolean sampling = Constant.YES.equals(project.getSample());
                    enums.stream().filter(e -> StringUtils.equals(e.getName(), sampling ? ErpConstant.SAMPLING : ErpConstant.NON_SAMPLING)).findFirst().ifPresent(e -> orderValueMap.put(field.getStructId() + "", e.getId() + ""));
                }
            });

            // 直接取值的字段
            logger.info("直接取值的字段");
            dynamicFieldList.stream().filter(field -> ErpConstant.DYNAMIC_FIELD_MAPPING.keySet().contains(field.getName())).forEach(field -> {
                String propertyName = ErpConstant.DYNAMIC_FIELD_MAPPING.get(field.getName());
                Object originalValue = BeanUtils.getPropertyValue(project, propertyName);
                String propertyValue = String.valueOf(originalValue);
                String structId = String.valueOf(field.getStructId());
                orderValueMap.put(structId, Objects.isNull(originalValue) ? "" : propertyValue);
            });

            // 匹配是否进行转换的字段
            logger.info("匹配是否进行转换的字段");
            dynamicFieldList.stream().filter(field -> ErpConstant.MATCH_YES_OR_NO_DYNAMIC_FIELD_MAPPING.keySet().contains(field.getName())).forEach(field -> {
                String propertyName = ErpConstant.MATCH_YES_OR_NO_DYNAMIC_FIELD_MAPPING.get(field.getName());
                Object originalValue = BeanUtils.getPropertyValue(project, propertyName);
                String propertyValue = String.valueOf(originalValue);
                String structId = String.valueOf(field.getStructId());
                List<MidEnumVO> enums = field.getEnums();
                if (Objects.nonNull(enums) && !enums.isEmpty()) {
                    String matchValue = Constant.YES.equals(propertyValue) ? ErpConstant.YES : ErpConstant.NO;
                    enums.stream().filter(e -> StringUtils.equals(e.getName(), matchValue)).findFirst().ifPresent(e -> orderValueMap.put(structId, String.valueOf(e.getId())));

                    // 处理当结果为是时的额外字段
                    if (Constant.YES.equals(propertyValue)) {
                        List<MidFieldRelationVO> customFieldRelationList = field.getCustomFieldRelationDtos();
                        if (Objects.nonNull(customFieldRelationList) && !customFieldRelationList.isEmpty()) {
                            List<String> relatedPropertyNameList = ErpConstant.MATCH_YES_EXT_FIELD_NAME_MAPPING.get(propertyName);
                            if (Objects.nonNull(relatedPropertyNameList) && !relatedPropertyNameList.isEmpty()) {
                                customFieldRelationList.forEach(relatedField -> {
                                    String extPropertyName = ErpConstant.MATCH_YES_EXT_DYNAMIC_FIELD_MAPPING.get(relatedField.getName());
                                    Object extOriginalValue = BeanUtils.getPropertyValue(project, extPropertyName);
                                    String extPropertyValue = String.valueOf(extOriginalValue);
                                    String extStructId = String.valueOf(relatedField.getStructId());
                                    orderValueMap.put(extStructId, extPropertyValue);
                                });
                            }
                        }
                    }
                }
            });

            // 匹配系统编码名称的字段
            logger.info("匹配系统编码名称的字段");
            dynamicFieldList.stream().filter(field -> ErpConstant.MATCH_CODE_NAME_DYNAMIC_FIELD_MAPPING.keySet().contains(field.getName())).forEach(field -> {
                String propertyName = ErpConstant.MATCH_CODE_NAME_DYNAMIC_FIELD_MAPPING.get(field.getName());
                Object originalValue = BeanUtils.getPropertyValue(project, propertyName);
                String propertyValue = String.valueOf(originalValue);
                String structId = String.valueOf(field.getStructId());
                List<MidEnumVO> enums = field.getEnums();
                if (Objects.nonNull(enums) && !enums.isEmpty()) {
                    // 通过字段值找到编码名称，然后通过编码名称与枚举值进行匹配
                    codeList.stream().filter(code -> StringUtils.startsWithIgnoreCase(code.getCodeCategoryId(), propertyName) && StringUtils.equals(code.getCode(), propertyValue)).findFirst().ifPresent(code -> {
                        enums.stream().filter(e -> StringUtils.equals(e.getName(), code.getCodeName())).findFirst().ifPresent(e -> orderValueMap.put(structId, String.valueOf(e.getId())));
                    });
                }
            });

            orderDtoExt.setOrderValue(orderValueMap);
        });
    }

    /**
     * 创建单项SKU
     * @param monitorScheme 监测方案
     * @return SKU
     */
    private SaveContractOriSkuInfo createSingleSku(EmcMonitorSchemeBean monitorScheme) {
        SaveContractOriSkuInfo sku = new SaveContractOriSkuInfo();
        if (NumberUtils.isNumber(monitorScheme.getSkuGbId())) {
            long skuGbId = NumberUtils.parseLong(monitorScheme.getSkuGbId());
            sku.setGbId(skuGbId);
        }
        sku.setName(monitorScheme.getItemGroup());
        sku.setSkuStandardMethodContent(monitorScheme.getExt$Item("methodName"));

        return sku;
    }

    /**
     * 创建单项SKU信息
     * @param monitorScheme 监测方案
     * @return SKU INFO
     */
    private SaveContractSampleSkuInfo createSingleSkuInfo(EmcMonitorSchemeBean monitorScheme) {
        SaveContractSampleSkuInfo skuInfo = new SaveContractSampleSkuInfo();
        if (NumberUtils.isNumber(monitorScheme.getSkuGbId())) {
            long skuGbId = NumberUtils.parseLong(monitorScheme.getSkuGbId());
            skuInfo.setSkuGbId(skuGbId);
        }
        skuInfo.setSkuName(monitorScheme.getItemGroup());
        skuInfo.setNeedDetected(true);
        skuInfo.setSkuStandardMethodContent(monitorScheme.getExt$Item("methodName"));
        skuInfo.setSkuType(0);

        return skuInfo;
    }

    /**
     * 创建套餐SKU
     * @param schemeCost 监测方案费用
     * @return SKU
     */
    private SaveContractOriSkuInfo createPackageSku(EmcMonitorSchemeCostBean schemeCost) {
        SaveContractOriSkuInfo packageSku = new SaveContractOriSkuInfo();
        if (NumberUtils.isNumber(schemeCost.getSkuGbId())) {
            long skuGbId = NumberUtils.parseLong(schemeCost.getSkuGbId());
            packageSku.setGbId(skuGbId);
        }
        packageSku.setName(schemeCost.getSchemeName());

        return packageSku;
    }

    /**
     * 创建套餐SKU信息
     * @param schemeCost 监测方案费用
     * @return SKU INFO
     */
    private SaveContractSampleSkuInfo createPackageSkuInfo(EmcMonitorSchemeCostBean schemeCost) {
        SaveContractSampleSkuInfo packageSkuInfo = new SaveContractSampleSkuInfo();
        if (NumberUtils.isNumber(schemeCost.getSkuGbId())) {
            long skuGbId = NumberUtils.parseLong(schemeCost.getSkuGbId());
            packageSkuInfo.setSkuGbId(skuGbId);
        }
        packageSkuInfo.setSkuName(schemeCost.getSchemeName());
        packageSkuInfo.setSkuType(1);

        return packageSkuInfo;
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
     * 设置当前登录用户
     * @param erpUserId ERP用户ID
     */
    private void setUserLogin(String erpUserId) {
        // 设置当前登录用户，用于记录后续的操作
        EmcEmpBean erpEmp = emcEmpService.selectFirstByFilter(SearchFilter.instance().match("erpGbId", erpUserId).filter(MatchPattern.SEQ));
        if (Objects.nonNull(erpEmp)) {
            CoreUserBean coreUser = coreUserService.selectById(erpEmp.getId());
            LocalContextHelper.setUserLogin(new LoginUser(coreUser.getId(), coreUser.getPassword()));
        }
    }

}
