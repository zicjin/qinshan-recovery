package com.sunwayworld.emc.module.erp.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.erp.bean.EnvSkuCostExtraModel;
import com.sunwayworld.emc.module.erp.bean.EnvSkuSamplingCostModel;
import com.sunwayworld.emc.module.erp.bean.MidSkuApplyPostDTO;
import com.sunwayworld.emc.module.erp.bean.MidSkuQuoteApplyPostDTOExt;
import com.sunwayworld.emc.module.erp.bean.RestResult;
import com.sunwayworld.emc.module.erp.constant.ErpConstant;
import com.sunwayworld.emc.module.erp.service.ErpSkuService;
import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.bean.EmcMonitorTmplGroupBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.service.EmcMonitorTmplGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcTestCapacityBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcTestCapacityService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeCostBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeSamplingCostBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorSchemeCostService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorSchemeSamplingCostService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorSchemeService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorTmplService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodOrderCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodOrderCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
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
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.sys.remotecall.service.CoreRemoteCallRequestService;

/**
 * @author zhengxy@sunwayworld.com 2023年07月25日
 */
@Repository
public class ErpSkuServiceImpl implements ErpSkuService {

    private static final Logger logger = LogManager.getLogger(ErpSkuServiceImpl.class);

    @Autowired
    private EmcTestMethodService testMethodService;

    @Autowired
    private EmcMonitorTmplGroupService monitorTmplGroupService;

    @Autowired
    private EmcMonitorTmplService monitorTmplService;

    @Autowired
    private EmcMonitorSchemeService monitorSchemeService;

    @Autowired
    private EmcMonitorSchemeCostService monitorSchemeCostService;

    @Autowired
    private EmcMonitorSchemeSamplingCostService samplingCostService;

    @Autowired
    private CoreOrgService coreOrgService;

    @Autowired
    private EmcEmpService empService;

    @Autowired
    private EmcTestMethodOrderCategoryService testMethodOrderCategoryService;

    @Autowired
    private CoreRemoteCallRequestService remoteCallRequestService;

    @Autowired
    private EmcCapacityItemGroupService capacityItemGroupService;

    @Autowired
    private EmcTestCapacityService testCapacityService;

    @Override
    public void pushSku(RestJsonWrapperBean wrapper) {
        List<EmcMonitorSchemeBean> schemeList = wrapper.parse(EmcMonitorSchemeBean.class);
        // 只能推送default下的，其他方案都是来自于default
        List<EmcMonitorSchemeBean> validSchemeList = schemeList.stream().filter(scheme -> StringUtils.equals(scheme.getSchemeName(), "default")).collect(Collectors.toList());
        if (validSchemeList.isEmpty()) {
            return;
        }

        List<Long> schemeIdList = validSchemeList.stream().map(EmcMonitorSchemeBean::getId).distinct().collect(Collectors.toList());
        validSchemeList = monitorSchemeService.selectListByFilter(SearchFilter.instance().match("id", schemeIdList).filter(MatchPattern.OR));

        // 查询测试资质
        Map<Long, Integer> qualificationMap = queryQualification(validSchemeList);

        // 查询ERP产品分类
        String monitorTmplId = validSchemeList.get(0).getMonitorTmplId();
        EmcMonitorTmplBean monitorTmpl = monitorTmplService.selectById(monitorTmplId);
        EmcMonitorTmplGroupBean monitorTmplGroup = monitorTmplGroupService.selectById(monitorTmpl.getMonitorTmplGroupId());
        long pCategory = Optional.ofNullable(monitorTmplGroup.getErpCategoryCode()).orElse(ErpConstant.DEFAULT_PRODUCT_CATEGORY_CODE);

        List<Long> testMethodIdList = validSchemeList.stream().map(EmcMonitorSchemeBean::getTestMethodId).distinct().collect(Collectors.toList());
        List<EmcTestMethodBean> testMethodList = testMethodService.getDao().selectListByOneColumnValues(testMethodIdList, "id", Arrays.asList("id", "remark", "hours"));

        List<EmcTestMethodOrderCategoryBean> testContainerList = testMethodOrderCategoryService.selectListByFilter(SearchFilter.instance().match("testMethodId", testMethodIdList).filter(MatchPattern.OR));

        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
        CoreOrgBean parentOrg = coreOrgService.selectById(parentOrgId);
        String merchantId = parentOrg.getExt$Item("erpMerchantId");

        EmcEmpBean quoter = empService.selectFirstByFilter(SearchFilter.instance().match("orgId", parentOrgId).filter(MatchPattern.SEQ)
                .match("quoter", Constant.YES).filter(MatchPattern.SEQ).match("erpGbId", null).filter(MatchPattern.DIFFER));

        List<EmcMonitorSchemeSamplingCostBean> samplingCostList = samplingCostService.getDao().selectListByOneColumnValues(schemeIdList, "monitorSchemeId");
        List<EmcMonitorSchemeBean> updateSchemeList = new ArrayList<>();

        validSchemeList.forEach(scheme -> {
            MidSkuQuoteApplyPostDTOExt dtoExt = new MidSkuQuoteApplyPostDTOExt();
            dtoExt.setMerchantId(merchantId);
            dtoExt.setQualification(qualificationMap.getOrDefault(scheme.getItemGroupId(), ErpConstant.NO_QUALIFICATION));
            if (Objects.nonNull(quoter)) {
                dtoExt.setSaleGbId(quoter.getErpGbId());
                dtoExt.setSaleGbIds(Collections.singletonList(quoter.getErpGbId()));
            }
            dtoExt.setLimsQuoteId(scheme.getId().toString());
            dtoExt.setQuoteProviderType(1);
            dtoExt.setDetectionObject(scheme.getItemGroup());
            dtoExt.setQuoteNote("无");

            EmcTestMethodOrderCategoryBean container = testContainerList.stream().filter(c -> ObjectUtils.equals(scheme.getTestMethodId(), c.getTestMethodId()) && StringUtils.equals(scheme.getMonitorTmplId(), c.getMonitorTmplId())).findFirst().orElse(null);
            String containerCategoryName = ObjectUtils.isEmpty(container) ? "" : container.getExt$Item("CONTAINERCATEGORYNAME");
            String storeCondition = ObjectUtils.isEmpty(container) ? "" : container.getExt$Item("STORECONDITION");
            String containerDesc = String.join(";", StringUtils.isEmpty(containerCategoryName) ? "" : containerCategoryName, StringUtils.isEmpty(storeCondition) ? "" : storeCondition);
            dtoExt.setSample(containerDesc);

            MidSkuApplyPostDTO sku = new MidSkuApplyPostDTO();
            sku.setName(scheme.getItemGroup());
            sku.setAliasName(sku.getName());
            sku.setStandardNumber(scheme.getExt$Item("methodName"));
            sku.setStandardName(scheme.getExt$Item("testMethodBasis"));
            sku.setSkuType(0); // 单项SKU
            sku.setbCyanideAssess(false);
            sku.setMerchantId(merchantId);
            sku.setProductLineGbId(getErpProductLineGbId());
            sku.setIndustryGbId(null);
            sku.setbCategory(getErpBizCategory());
            sku.setpCategory(pCategory);
            sku.setpCategoryCodes(Collections.singletonList(sku.getpCategory()));
            sku.setStandardType(0);
            sku.setQuoteUnit(2);
            sku.setLimsId(scheme.getId().toString());
            sku.setDetectionObject(scheme.getItemGroup());
            MidSkuApplyPostDTO.SkuStandardDetermineDto skuStandardDetermineDto = new MidSkuApplyPostDTO().new SkuStandardDetermineDto();
            sku.setSkuStandardDetermine(skuStandardDetermineDto);
            sku.setRisk("无");
            sku.setChapter("无");
            if (NumberUtils.isNumber(scheme.getSkuGbId())) {
                sku.setGbId(Long.valueOf(scheme.getSkuGbId()));
            }

            testMethodList.stream().filter(tm -> ObjectUtils.equals(tm.getId(), scheme.getTestMethodId())).findFirst().ifPresent(tm -> {
                sku.setNotes(tm.getRemark());
                sku.setItemDesc(tm.getRemark());
                Double hours = tm.getHours();
                if (!ObjectUtils.isEmpty(hours)) {
                    sku.setSampleRetentionPeriod(hours + "小时");
                }
            });

            dtoExt.setSku(sku);
            dtoExt.setSampleRetentionPeriod(sku.getSampleRetentionPeriod());
            dtoExt.setPeriod(scheme.getPeriod());
            dtoExt.setUrgentPeriod(scheme.getPeriod());
            dtoExt.setExtraUrgentPeriod(scheme.getPeriod());

            // 价格需要转换成分
            if (Objects.nonNull(scheme.getTechPrice())) {
                long convertedTechPrice = scheme.getTechPrice() * 100;
                dtoExt.setPrice(convertedTechPrice);
                dtoExt.setUrgentPrice(convertedTechPrice);
                dtoExt.setExtraUrgentPrice(convertedTechPrice);
            }

            if (Objects.nonNull(scheme.getSalePrice())) {
                long convertedSalePrice = scheme.getSalePrice() * 100;
                dtoExt.setOrgFixPrice(convertedSalePrice);
                dtoExt.setUrgentOrgFixPrice(convertedSalePrice);
                dtoExt.setExtraUrgentOrgFixPrice(convertedSalePrice);
            }

            // 采样费用
            List<EmcMonitorSchemeSamplingCostBean> curCostList = samplingCostList.stream().filter(cost -> ObjectUtils.equals(cost.getMonitorSchemeId(), scheme.getId())).collect(Collectors.toList());
            List<EnvSkuSamplingCostModel> skuSamplingCosts = new ArrayList<>(curCostList.size());
            curCostList.forEach(cost -> {
                EnvSkuSamplingCostModel samplingCostModel = new EnvSkuSamplingCostModel();
                samplingCostModel.setDefault(Constant.YES.equals(cost.getDefaultCost()));
                samplingCostModel.setSamplingType(cost.getSamplingType());
                if (Objects.nonNull(cost.getSamplingCost())) {
                    samplingCostModel.setSamplingCost(cost.getSamplingCost() * 100);
                }
                samplingCostModel.setMerchantId(merchantId);
                skuSamplingCosts.add(samplingCostModel);
            });
            dtoExt.setEnvSkuSamplingCostModels(skuSamplingCosts);

            String jsonStr = JSONObject.toJSONString(dtoExt);
            jsonStr = jsonStr.replaceAll("default", "isDefault");
            logger.info("推送单项SKU到ERP请求参数 --> {}", jsonStr);

            String result = remoteCallRequestService.processSyncRequest("pushSingleSku", jsonStr);
            logger.info("推送单项SKU到ERP返回结果 --> {}", result);
            RestResult<EmcMonitorSchemeBean> restResult = JSON.parseObject(result, new TypeReference<RestResult<EmcMonitorSchemeBean>>(){});

            if (StringUtils.equals(restResult.getResultCode(), ErpConstant.SUCCESS_CODE)
                    && !ObjectUtils.isEmpty(restResult.getResultData())
                    && !StringUtils.isEmpty(restResult.getResultData().getSkuGbId())) {

                scheme.setSkuGbId(restResult.getResultData().getSkuGbId());
                scheme.setSkuQuoteGbId(restResult.getResultData().getSkuQuoteGbId());
                updateSchemeList.add(scheme);
            }
        });

        if (!updateSchemeList.isEmpty()) {
            // 只更新当前基地的分析项组
            List<Long> itemGroupIdList = updateSchemeList.stream().map(EmcMonitorSchemeBean::getItemGroupId).collect(Collectors.toList());
            List<EmcMonitorSchemeBean> relatedSchemeList = monitorSchemeService.getDao().selectListByOneColumnValues(itemGroupIdList, "itemGroupId");
            updateSchemeList.forEach(updateScheme -> relatedSchemeList.stream().filter(relatedScheme -> ObjectUtils.equals(updateScheme.getItemGroupId(), relatedScheme.getItemGroupId())).forEach(relatedScheme -> {
                relatedScheme.setSkuGbId(updateScheme.getSkuGbId());
                relatedScheme.setSkuQuoteGbId(updateScheme.getSkuQuoteGbId());
            }));
            monitorSchemeService.getDao().update(relatedSchemeList, "skuGbId", "skuQuoteGbId");
        }
    }

    @Override
    public void pushPackageSku(RestJsonWrapperBean wrapper) {
        List<EmcMonitorSchemeCostBean> costList = wrapper.parse(EmcMonitorSchemeCostBean.class);
        List<String> monitorTmplIdList = costList.stream().map(EmcMonitorSchemeCostBean::getMonitorTmplId).distinct().collect(Collectors.toList());
        List<String> schemeNameList = costList.stream().map(EmcMonitorSchemeCostBean::getSchemeName).distinct().collect(Collectors.toList());

        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
        CoreOrgBean parentOrg = coreOrgService.selectById(parentOrgId);
        String merchantId = parentOrg.getExt$Item("erpMerchantId");

        EmcEmpBean quoter = empService.selectFirstByFilter(SearchFilter.instance().match("orgId", parentOrgId).filter(MatchPattern.SEQ)
                .match("quoter", Constant.YES).filter(MatchPattern.SEQ).match("erpGbId", null).filter(MatchPattern.DIFFER));

        List<EmcMonitorSchemeBean> schemeList = monitorSchemeService.selectListByFilter(SearchFilter.instance().match("monitorTmplId", monitorTmplIdList).filter(MatchPattern.OR).match("schemeName", schemeNameList).filter(MatchPattern.OR));
        List<Long> itemGroupIdList = schemeList.stream().map(EmcMonitorSchemeBean::getItemGroupId).distinct().collect(Collectors.toList());

        List<EmcMonitorSchemeBean> defaultSchemeList = monitorSchemeService.selectListByFilter(SearchFilter.instance().match("monitorTmplId", monitorTmplIdList).filter(MatchPattern.OR).match("schemeName", "default").filter(MatchPattern.SEQ).match("itemGroupId", itemGroupIdList).filter(MatchPattern.OR));

        // 查询ERP产品分类
        String monitorTmplId = defaultSchemeList.get(0).getMonitorTmplId();
        EmcMonitorTmplBean monitorTmpl = monitorTmplService.selectById(monitorTmplId);
        EmcMonitorTmplGroupBean monitorTmplGroup = monitorTmplGroupService.selectById(monitorTmpl.getMonitorTmplGroupId());
        long pCategory = Optional.ofNullable(monitorTmplGroup.getErpCategoryCode()).orElse(ErpConstant.DEFAULT_PRODUCT_CATEGORY_CODE);

        List<EmcMonitorSchemeCostBean> updateCostList = new ArrayList<>();

        costList.forEach(cost -> {
            MidSkuQuoteApplyPostDTOExt dtoExt = new MidSkuQuoteApplyPostDTOExt();
            dtoExt.setMerchantId(merchantId);
            dtoExt.setQualification(ErpConstant.CMA_QUALIFICATION); // 默认CMA资质
            dtoExt.setSaleGbId(quoter.getErpGbId());
            dtoExt.setSaleGbIds(Collections.singletonList(quoter.getErpGbId()));
            dtoExt.setLimsQuoteId(cost.getId().toString());
            dtoExt.setQuoteProviderType(1);
            dtoExt.setDetectionObject(cost.getSchemeName());
            dtoExt.setQuoteNote("无");
            dtoExt.setSample("/");

            MidSkuApplyPostDTO sku = new MidSkuApplyPostDTO();
            sku.setName(cost.getSchemeName());
            sku.setAliasName(cost.getSchemeName());
            sku.setSkuType(1); // 套餐SKU
            sku.setMerchantId(merchantId);
            if (NumberUtils.isNumber(cost.getSkuGbId())) {
                sku.setGbId(Long.valueOf(cost.getSkuGbId()));
            }
            sku.setQuoteUnit(2);
            sku.setStandardType(0);
            sku.setStandardNumber("/");
            sku.setStandardName("/");
            sku.setbCyanideAssess(false);
            sku.setProductLineGbId(getErpProductLineGbId());
            sku.setIndustryGbId(null);
            sku.setbCategory(getErpBizCategory());
            sku.setpCategory(pCategory);
            sku.setpCategoryCodes(Collections.singletonList(sku.getpCategory()));
            sku.setSkuPurposeId(2L);

            EnvSkuCostExtraModel costExtraModel = new EnvSkuCostExtraModel();
            costExtraModel.setMerchantId(merchantId);
            if (NumberUtils.isNumber(cost.getSamplingFeeType())) {
                costExtraModel.setSamplingFeeType(Integer.valueOf(cost.getSamplingFeeType()));
            }
            if (NumberUtils.isNumber(cost.getAnalyzeFeeType())) {
                costExtraModel.setAnalyzeFeeType(Integer.valueOf(cost.getAnalyzeFeeType()));
            }
            if (NumberUtils.isNumber(cost.getAnalyzeFeeFactor())) {
                costExtraModel.setAnalyzeFeeFactor(Integer.valueOf(cost.getAnalyzeFeeFactor()));
            }
            if (NumberUtils.isNumber(cost.getAnalyzeFeeMaximum())) {
                costExtraModel.setAnalyzeFeeMaximum(Integer.valueOf(cost.getAnalyzeFeeMaximum()));
            }

            List<MidSkuApplyPostDTO> singleSkuList = new ArrayList<>();

            List<EmcMonitorSchemeBean> curSchemeList = schemeList.stream().filter(scheme -> StringUtils.equals(cost.getMonitorTmplId(), scheme.getMonitorTmplId()) && StringUtils.equals(cost.getSchemeName(), scheme.getSchemeName())).collect(Collectors.toList());
            List<Long> curItemGroupIdList = curSchemeList.stream().map(EmcMonitorSchemeBean::getItemGroupId).distinct().collect(Collectors.toList());
            List<EmcMonitorSchemeBean> curDefaultSchemeList = defaultSchemeList.stream().filter(ds -> StringUtils.equals(cost.getMonitorTmplId(), ds.getMonitorTmplId()) && curItemGroupIdList.contains(ds.getItemGroupId())).collect(Collectors.toList());

            curDefaultSchemeList.forEach(ds -> {
                MidSkuApplyPostDTO singleDto = new MidSkuApplyPostDTO();
                singleDto.setName(ds.getItemGroup());
                singleDto.setStandardNumber(ds.getExt$Item("methodName"));
                singleDto.setStandardName(ds.getExt$Item("testMethodBasis"));
                singleDto.setSkuType(0);
                singleDto.setbCyanideAssess(false);
                if (NumberUtils.isNumber(ds.getSkuGbId())) {
                    singleDto.setGbId(Long.valueOf(ds.getSkuGbId()));
                }
                singleDto.setMerchantId(merchantId);

                singleDto.setProductLineGbId(getErpProductLineGbId());
                singleDto.setIndustryGbId(null);
                singleDto.setbCategory(getErpBizCategory());
                singleDto.setpCategory(pCategory);
                singleDto.setpCategoryCodes(Collections.singletonList(singleDto.getpCategory()));
                singleDto.setStandardType(0);
                singleDto.setQuoteUnit(2);
                singleDto.setLimsId(ds.getId().toString());
                singleDto.setDetectionObject(cost.getSchemeName());
                MidSkuApplyPostDTO.SkuStandardDetermineDto skuStandardDetermineDto = new MidSkuApplyPostDTO().new SkuStandardDetermineDto();
                singleDto.setSkuStandardDetermine(skuStandardDetermineDto);
                singleDto.setRisk("无");
                singleDto.setChapter("无");
                singleDto.setSkuPurposeId(2L);

                singleSkuList.add(singleDto);
            });
            sku.setSingleSkuList(singleSkuList);

            List<Long> singleGbIdList = singleSkuList.stream().map(MidSkuApplyPostDTO::getGbId).collect(Collectors.toList());
            sku.setSingleGbIds(singleGbIdList);

            dtoExt.setSku(sku);
            dtoExt.setPeriod(cost.getPeriod());
            if (Objects.nonNull(cost.getTechPrice())) {
                dtoExt.setPrice(cost.getTechPrice() * 100);
            }
            if (Objects.nonNull(cost.getSalePrice())) {
                dtoExt.setOrgFixPrice(cost.getSalePrice() * 100);
            }
            dtoExt.setEnvSkuCostExtraModel(costExtraModel);

            String jsonStr = JSONObject.toJSONString(dtoExt);
            logger.info("推送套餐SKU到ERP请求参数 --> {}", jsonStr);

            String result = remoteCallRequestService.processSyncRequest("pushPackageSku", jsonStr);
            logger.info("推送套餐SKU到ERP返回结果 --> {}", result);
            RestResult<EmcMonitorSchemeBean> restResult = JSON.parseObject(result, new TypeReference<RestResult<EmcMonitorSchemeBean>>(){});

            if (StringUtils.equals(restResult.getResultCode(), ErpConstant.SUCCESS_CODE)
                    && !ObjectUtils.isEmpty(restResult.getResultData())
                    && !StringUtils.isEmpty(restResult.getResultData().getSkuGbId())) {

                cost.setSkuGbId(restResult.getResultData().getSkuGbId());
                cost.setSkuQuoteGbId(restResult.getResultData().getSkuQuoteGbId());
                updateCostList.add(cost);
            }
        });

        if (!updateCostList.isEmpty()) {
            // 更新同一监测模板组下方案名称一样的
            List<String> updateSchemeNameList = updateCostList.stream().map(EmcMonitorSchemeCostBean::getSchemeName).collect(Collectors.toList());
            List<EmcMonitorTmplBean> relatedMonitorTmplList = monitorTmplService.selectListByFilter(SearchFilter.instance().match("MONITORTMPLGROUPID", monitorTmplGroup.getId()).filter(MatchPattern.SEQ).match("ORGID", parentOrgId).filter(MatchPattern.SEQ));
            List<String> relatedMonitorTmplIdList = relatedMonitorTmplList.stream().map(EmcMonitorTmplBean::getId).collect(Collectors.toList());
            List<EmcMonitorSchemeCostBean> relatedSchemeCostList = monitorSchemeCostService.selectListByFilter(SearchFilter.instance().match("monitorTmplId", relatedMonitorTmplIdList).filter(MatchPattern.OR).match("schemeName", updateSchemeNameList).filter(MatchPattern.OR));
            updateCostList.forEach(updateCost -> relatedSchemeCostList.stream().filter(relatedCost -> StringUtils.equals(updateCost.getSchemeName(), relatedCost.getSchemeName())).forEach(relatedCost -> {
                relatedCost.setSkuGbId(updateCost.getSkuGbId());
                relatedCost.setSkuQuoteGbId(updateCost.getSkuQuoteGbId());
            }));

            monitorSchemeCostService.getDao().update(relatedSchemeCostList, "skuGbId", "skuQuoteGbId");
        }
    }

    @Override
    public void createSkuSamplingCost(RestJsonWrapperBean wrapper) {
        // 同步sku时会一起同步采样费用
    }

    @Override
    public void deleteSkuSamplingCost(RestJsonWrapperBean wrapper) {
        // 同步sku时会一起同步采样费用
    }

    @Override
    public void updateSkuSamplingCost(RestJsonWrapperBean wrapper) {
        // 同步sku时会一起同步采样费用
    }

    @Override
    public String pushSkuToErp(String skuJson) {
        return HttpClientManager.getInstance().sendHttpPost(getErpServiceUrl() + ErpConstant.PUSH_SKU_INTERFACE, skuJson);
    }

    private Map<Long, Integer> queryQualification(List<EmcMonitorSchemeBean> schemeList) {
        Map<Long, Integer> qualificationMap = new HashMap<>(schemeList.size());

        List<Long> capacityItemGroupIdList = schemeList.stream().map(EmcMonitorSchemeBean::getItemGroupId).distinct().collect(Collectors.toList());
        List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.selectListByIds(capacityItemGroupIdList);
        List<Long> testCapacityIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getTestCapacityId).distinct().collect(Collectors.toList());
        List<EmcTestCapacityBean> testCapacityList = testCapacityService.getDao().selectListByOneColumnValues(testCapacityIdList, "id", Arrays.asList("id", "AUTHENTICATION", "APPROVAL"));

        testCapacityList.forEach(capacity -> {
            boolean authentication = Constant.YES.equals(capacity.getExt$Item("AUTHENTICATION"));
            boolean approval = Constant.YES.equals(capacity.getExt$Item("APPROVAL"));
            Integer qualification = getQualification(authentication, approval);

            capacityItemGroupList.stream().filter(cig -> ObjectUtils.equals(cig.getTestCapacityId(), capacity.getId())).forEach(cig -> qualificationMap.put(cig.getId(), qualification));
        });

        return qualificationMap;
    }

    private Integer getQualification(boolean authentication, boolean approval) {
        int qualification = ErpConstant.NO_QUALIFICATION;
        if (authentication) {
            qualification += ErpConstant.CMA_QUALIFICATION;
        }

        if (approval) {
            qualification += ErpConstant.CNAS_QUALIFICATION;
        }

        return qualification;
    }

    private String getMerchantId() {
        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
        CoreOrgBean parentOrg = coreOrgService.selectById(parentOrgId);

        return parentOrg.getExt$Item("erpMerchantId");
    }

    private String getErpServiceUrl() {
        String merchantId = getMerchantId();
        String constantKey = StringUtils.isEmpty(merchantId) ? "ERP_SERVICE_URL" : "ERP_SERVICE_URL_" + merchantId;
        return Optional.ofNullable(ApplicationContextHelper.getConstantValue(constantKey)).orElse(ErpConstant.ERP_BASE_URL);
    }

    private Long getErpProductLineGbId() {
        return NumberUtils.parseLong(Optional.ofNullable(ApplicationContextHelper.getConstantValue("ERP_PRODUCT_LINE_GBID")).orElse(ErpConstant.DEFAULT_PRODUCT_LINE_GBID + ""));
    }

    private Long getErpBizCategory() {
        return NumberUtils.parseLong(Optional.ofNullable(ApplicationContextHelper.getConstantValue("ERP_BIZ_CATEGORY")).orElse(ErpConstant.DEFAULT_BIZ_CATEGORY + ""));
    }
}
