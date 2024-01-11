package com.sunwayworld.emc.module.bp.subpackage.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjSchemeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjOrderService;
import com.sunwayworld.emc.module.bp.subpackage.bean.EmcSubpackageBean;
import com.sunwayworld.emc.module.bp.subpackage.bean.EmcSubpackageTaskBean;
import com.sunwayworld.emc.module.bp.subpackage.dao.EmcSubpackageDao;
import com.sunwayworld.emc.module.bp.subpackage.service.EmcSubpackageService;
import com.sunwayworld.emc.module.bp.subpackage.service.EmcSubpackageTaskService;
import com.sunwayworld.emc.module.bp.subpackage.validator.EmcAddToSubpackageValidator;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityContractBean;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityMainBean;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityRequestDTO;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityResponseDTO;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityContractService;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityMainService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.exception.NetworkException;
import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;

/**
* @author zhengxy@sunwayworld.com 2022年09月05日
*/
@Repository
public class EmcSubpackageServiceImpl implements EmcSubpackageService {

    private static final Logger logger = LogManager.getLogger(EmcSubpackageServiceImpl.class);

    @Autowired
    private EmcProjOrderService projOrderService;

    @Autowired
    private EmcProjSchemeService projSchemeService;

    @Autowired
    private EmcSubpackageTaskService subpackageTaskService;

    @Autowired
    private EmcOrderItemGroupService orderItemGroupService;

    @Autowired
    private CrmOpportunityContractService contractService;

    @Autowired
    private CrmOpportunityMainService opportunityMainService;

    @Autowired
    private CoreUserService coreUserService;

    @Autowired
    private CoreOrgService coreOrgService;

    @Autowired
    private EmcOrderService orderService;

    @Autowired
    private EmcFolderService folderService;

    @Autowired
    private EmcOrderTaskService orderTaskService;


    @Autowired
    private EmcSubpackageDao emcSubpackageDao;


    @Override
    @SuppressWarnings("unchecked")
    public EmcSubpackageDao getDao() {
        return emcSubpackageDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        EmcSubpackageBean emcSubpackageBean = wrapperBean.parseUnique(EmcSubpackageBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcSubpackageBean.setId(id);
        getDao().insert(emcSubpackageBean);
        return id;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void createPackage(RestJsonWrapperBean wrapper) {
        List<EmcProjectBean> projectList = wrapper.parse(EmcProjectBean.class, 0);
        if (projectList.isEmpty()) {
            return;
        }

        EmcProjectBean curProject = projectList.get(0);
        boolean sampling = Constant.YES.equals(curProject.getSample());

        EmcSubpackageBean subpackage = new EmcSubpackageBean();
        subpackage.setId(ApplicationContextHelper.getNextIdentity());
        subpackage.setSubpackageNo(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        subpackage.setProjId(curProject.getId());
        subpackage.setProjNo(curProject.getProjNo());
        subpackage.setContractNo(curProject.getExt$Item("contractNo"));
        subpackage.setSubpackageType("create");
        subpackage.setOrgId(LocalContextHelper.getLoginUser().getExt$Item("parentOrgId"));
        subpackage.setStatus(Constant.NO);

        List<EmcSubpackageTaskBean> subpackageTaskList = new ArrayList<>();

        if (sampling) {
            subpackage.setSampling(Constant.YES);
            List<EmcProjSchemeBean> schemeList = wrapper.parse(EmcProjSchemeBean.class, 1);
            List<EmcProjOrderBean> projOrderList = wrapper.parse(EmcProjOrderBean.class, 2);

            List<Long> schemeIdList = schemeList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList());
            List<Long> itemGroupIdList = projOrderList.stream().map(EmcProjOrderBean::getItemGroupId).distinct().collect(Collectors.toList());

            List<EmcProjOrderBean> sourceProjOrderList = projOrderService.selectListByFilter(SearchFilter.instance().match("projSchemeId", schemeIdList).filter(MatchPattern.OR).match("itemGroupId", itemGroupIdList).filter(MatchPattern.OR));
            sourceProjOrderList.forEach(projOrder -> {
                EmcSubpackageTaskBean task = new EmcSubpackageTaskBean();
                task.setId(ApplicationContextHelper.getNextIdentity());
                task.setProjId(subpackage.getProjId());
                task.setSubpackageId(subpackage.getId());
                task.setSubpackageNo(subpackage.getSubpackageNo());
                task.setProjSchemeId(projOrder.getProjSchemeId());
                task.setSampleNum(projOrder.getSampleNum());
                task.setTestId(projOrder.getTestId());
                task.setTestName(projOrder.getExt$Item("testName"));
                task.setMethodId(projOrder.getMethodId());
                task.setTestMethodId(NumberUtils.parseLong(projOrder.getExt$Item("testMethodId")));
                task.setItemGroup(projOrder.getItemGroup());
                task.setItemGroupId(projOrder.getItemGroupId());
                schemeList.stream().filter(scheme -> ObjectUtils.equals(scheme.getId(), projOrder.getProjSchemeId())).findFirst().ifPresent(scheme -> {
                    task.setCycle(scheme.getCycle());
                    task.setFrequency(scheme.getFrequency());
                    task.setFolderNo(scheme.getFolderNo());
                });
                subpackageTaskList.add(task);
            });

            getDao().insert(subpackage);
            subpackageTaskService.getDao().insert(subpackageTaskList);

            sourceProjOrderList.forEach(o -> {
                o.setSubcontractorId(subpackage.getId());
                o.setSubPackageNo(subpackage.getSubpackageNo());
            });
            schemeList.forEach(s ->{
                s.setExt$Item("subpackageflag","1");
            });
            projOrderService.getDao().update(sourceProjOrderList, "SUBCONTRACTORID", "SUBPACKAGENO");
            projSchemeService.getDao().update(schemeList,"subpackageflag");
            return;
        }

        subpackage.setSampling(Constant.NO);
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class, 1);
        List<EmcOrderItemGroupBean> orderItemGroupList = wrapper.parse(EmcOrderItemGroupBean.class, 2);
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<Long> itemGroupIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());

        List<EmcOrderItemGroupBean> sourceItemGroupList = orderItemGroupService.selectListByFilter(SearchFilter.instance().match("orderId", orderIdList).filter(MatchPattern.OR).match("itemGroupId", itemGroupIdList).filter(MatchPattern.OR));
        sourceItemGroupList.forEach(group -> {
            EmcSubpackageTaskBean task = new EmcSubpackageTaskBean();
            task.setId(ApplicationContextHelper.getNextIdentity());
            task.setProjId(subpackage.getProjId());
            task.setSubpackageId(subpackage.getId());
            task.setSubpackageNo(subpackage.getSubpackageNo());
            task.setOrderId(group.getOrderId());
            task.setTestId(group.getTestId());
            task.setTestName(group.getTestName());
            task.setTestMethodId(group.getTestMethodId());
            task.setMethodId(group.getMethodId());
            task.setItemGroup(group.getItemGroup());
            task.setItemGroupId(group.getItemGroupId());

            orderList.stream().filter(order -> ObjectUtils.equals(group.getOrderId(), order.getId())).findFirst().ifPresent(order -> task.setOrderNo(order.getOrderNo()));
            subpackageTaskList.add(task);
        });

        sourceItemGroupList.forEach(group -> {
            group.setSubcontractorId(subpackage.getId());
            group.setSubPackageNo(subpackage.getSubpackageNo());
        });
        orderList.forEach(o ->{
            o.setExt$Item("subpackageflag","1");
        });
        getDao().insert(subpackage);
        subpackageTaskService.getDao().insert(subpackageTaskList);

        orderService.getDao().update(orderList,"subpackageflag");

        orderItemGroupService.getDao().update(sourceItemGroupList, "SUBCONTRACTORID", "SUBPACKAGENO");
    }

    @Override
    @ValidateDataWith(EmcAddToSubpackageValidator.class)
    public void addToPackage(RestJsonWrapperBean wrapper) {
        List<EmcSubpackageBean> subpackageList = wrapper.parse(EmcSubpackageBean.class, 0);
        if (subpackageList.isEmpty()) {
            return;
        }

        EmcSubpackageBean subpackage = subpackageList.get(0);
        boolean sampling = Constant.YES.equals(subpackage.getSampling());
        List<EmcSubpackageTaskBean> subpackageTaskList = new ArrayList<>();

        if (sampling) {
            List<EmcProjSchemeBean> schemeList = wrapper.parse(EmcProjSchemeBean.class, 1);
            List<EmcProjOrderBean> projOrderList = wrapper.parse(EmcProjOrderBean.class, 2);

            List<Long> schemeIdList = schemeList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList());
            List<Long> itemGroupIdList = projOrderList.stream().map(EmcProjOrderBean::getItemGroupId).distinct().collect(Collectors.toList());

            List<EmcProjOrderBean> sourceProjOrderList = projOrderService.selectListByFilter(SearchFilter.instance().match("projSchemeId", schemeIdList).filter(MatchPattern.OR).match("itemGroupId", itemGroupIdList).filter(MatchPattern.OR));
            sourceProjOrderList.forEach(projOrder -> {
                EmcSubpackageTaskBean task = new EmcSubpackageTaskBean();
                task.setId(ApplicationContextHelper.getNextIdentity());
                task.setProjId(subpackage.getProjId());
                task.setSubpackageId(subpackage.getId());
                task.setSubpackageNo(subpackage.getSubpackageNo());
                task.setProjSchemeId(projOrder.getProjSchemeId());
                task.setSampleNum(projOrder.getSampleNum());
                task.setTestId(projOrder.getTestId());
                task.setTestName(projOrder.getExt$Item("testName"));
                task.setTestMethodId(NumberUtils.parseLong(projOrder.getExt$Item("testMethodId")));
                task.setItemGroup(projOrder.getItemGroup());
                task.setMethodId(projOrder.getMethodId());
                task.setItemGroupId(projOrder.getItemGroupId());
                schemeList.stream().filter(scheme -> ObjectUtils.equals(scheme.getId(), projOrder.getProjSchemeId())).findFirst().ifPresent(scheme -> {
                    task.setCycle(scheme.getCycle());
                    task.setFrequency(scheme.getFrequency());
                    task.setFolderNo(scheme.getFolderNo());
                });
                subpackageTaskList.add(task);
                projOrder.setSubcontractorId(subpackage.getId());
                projOrder.setSubPackageNo(subpackage.getSubpackageNo());
            });
            schemeList.forEach(s ->{
                s.setExt$Item("subpackageflag","1");
            });
            projOrderService.getDao().update(sourceProjOrderList,"subcontractorId","subPackageNo");
            projSchemeService.getDao().update(schemeList,"subpackageflag");
            subpackageTaskService.getDao().insert(subpackageTaskList);
            return;
        }

        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class, 1);
        List<EmcOrderItemGroupBean> orderItemGroupList = wrapper.parse(EmcOrderItemGroupBean.class, 2);
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<Long> itemGroupIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());

        List<EmcOrderItemGroupBean> sourceItemGroupList = orderItemGroupService.selectListByFilter(SearchFilter.instance().match("orderId", orderIdList).filter(MatchPattern.OR).match("itemGroupId", itemGroupIdList).filter(MatchPattern.OR));
        sourceItemGroupList.forEach(group -> {
            EmcSubpackageTaskBean task = new EmcSubpackageTaskBean();
            task.setId(ApplicationContextHelper.getNextIdentity());
            task.setProjId(subpackage.getProjId());
            task.setSubpackageId(subpackage.getId());
            task.setSubpackageNo(subpackage.getSubpackageNo());
            task.setOrderId(group.getOrderId());
            task.setTestId(group.getTestId());
            task.setTestName(group.getTestName());
            task.setMethodId(group.getMethodId());
            task.setTestMethodId(group.getTestMethodId());
            task.setItemGroup(group.getItemGroup());
            task.setItemGroupId(group.getItemGroupId());

            orderList.stream().filter(order -> ObjectUtils.equals(group.getOrderId(), order.getId())).findFirst().ifPresent(order -> task.setOrderNo(order.getOrderNo()));
            subpackageTaskList.add(task);
            group.setSubPackageNo(subpackage.getSubpackageNo());
            group.setSubcontractorId(subpackage.getId());

        });
        orderList.forEach(o ->{
            o.setExt$Item("subpackageflag","1");
        });
        orderItemGroupService.getDao().update(sourceItemGroupList,"subcontractorId","subPackageNo");

        orderService.getDao().update(orderList,"subpackageflag");

        subpackageTaskService.getDao().insert(subpackageTaskList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void assignPackage(RestJsonWrapperBean wrapper) {
        //String targetOrgId = wrapper.getParamValue("targetOrgId");
        //String targetOrgName = wrapper.getParamValue("targetOrgName");
        List<EmcSubpackageBean> subpackageList = wrapper.parse(EmcSubpackageBean.class, 0);
        List<Long> subpackageIdList = subpackageList.stream().map(EmcSubpackageBean::getId).collect(Collectors.toList());

        List<EmcSubpackageTaskBean> subpackageTaskList = subpackageTaskService.getDao().selectListByOneColumnValues(subpackageIdList, "subpackageId");

        List<EmcSubpackageBean> targetSubpackageList = new ArrayList<>(subpackageList.size());
        List<EmcSubpackageTaskBean> targetSubpackageTaskList = new ArrayList<>(subpackageTaskList.size());

        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
        CoreOrgBean parentOrg = coreOrgService.selectById(parentOrgId);
        String parentOrgName = parentOrg.getOrgName();

        subpackageList.forEach(s -> {
            EmcSubpackageBean subpackage = new EmcSubpackageBean();
            BeanUtils.copyProperties(s, subpackage);
            subpackage.setId(ApplicationContextHelper.getNextIdentity());
            subpackage.setSubpackageNo(ApplicationContextHelper.getNextSequence(getDao().getTable()));
            subpackage.setSubpackageType("accepted");
            //subpackage.setOrgId(targetOrgId);
            subpackage.setOrgId(s.getInnerSubcontractorId());
            subpackage.setSampling(s.getSampling());
            subpackage.setStatus(Constant.NO);
            subpackage.setRelatedSubpackageId(s.getId());
            subpackage.setEntrustOrgId(parentOrgId);
            subpackage.setEntrustOrgName(parentOrgName);

            targetSubpackageList.add(subpackage);

            subpackageTaskList.stream().filter(task -> ObjectUtils.equals(s.getId(), task.getSubpackageId())).forEach(task -> {
                EmcSubpackageTaskBean subpackageTask = new EmcSubpackageTaskBean();
                BeanUtils.copyProperties(task, subpackageTask, EmcConstant.CREATED_COLUMNS);

                subpackageTask.setId(ApplicationContextHelper.getNextIdentity());
                subpackageTask.setSubpackageId(subpackage.getId());
                subpackageTask.setSubpackageNo(subpackage.getSubpackageNo());
                subpackageTask.setProjId(null);

                targetSubpackageTaskList.add(subpackageTask);
            });
        });

        subpackageList.forEach(s -> {
            s.setStatus(Constant.YES);
            //s.setEntrustOrgId(targetOrgId);
            //s.setEntrustOrgName(targetOrgName);
            s.setEntrustOrgId(s.getInnerSubcontractorId());
            s.setEntrustOrgName(s.getInnerSubcontractorName());
        });

        getDao().insert(targetSubpackageList);
        subpackageTaskService.getDao().insert(targetSubpackageTaskList);

        getDao().update(subpackageList, "status", "entrustOrgId", "entrustOrgName");
    }

    @Override
    public void createContract(RestJsonWrapperBean wrapper) {
        List<EmcSubpackageBean> subpackageList = wrapper.parse(EmcSubpackageBean.class);

        List<Long> relatedSubpackageIdList = subpackageList.stream().map(EmcSubpackageBean::getRelatedSubpackageId).distinct().collect(Collectors.toList());
        List<EmcSubpackageBean> relatedSubpackageList = getDao().selectListByIds(relatedSubpackageIdList);

        List<String> userIdList = relatedSubpackageList.stream().map(EmcSubpackageBean::getCreatedById).distinct().collect(Collectors.toList());
        List<CoreUserBean> coreUserList = coreUserService.selectListByIds(userIdList);
        Map<String, CoreUserBean> coreUserMap = coreUserList.stream().collect(Collectors.toMap(CoreUserBean::getId, Function.identity()));

        List<String> contractNoList = relatedSubpackageList.stream().map(EmcSubpackageBean::getContractNo).distinct().collect(Collectors.toList());
        List<CrmOpportunityContractBean> contractList = contractService.getDao().selectListByOneColumnValues(contractNoList, "contractNo", Arrays.asList("id", "contractNo", "opportunityId"));
        List<String> opportunityIdList = contractList.stream().map(CrmOpportunityContractBean::getOpportunityId).distinct().collect(Collectors.toList());

        List<CrmOpportunityMainBean> opportunityMainList = opportunityMainService.selectListByIds(opportunityIdList);

        List<CrmOpportunityMainBean> newOpportunityMainList = new ArrayList<>(subpackageList.size());
        subpackageList.forEach(subpackage -> {
            CrmOpportunityMainBean newOpportunityMain = new CrmOpportunityMainBean();
            newOpportunityMain.setDrawdate(LocalDate.now());
            newOpportunityMain.setCustomerName(subpackage.getEntrustOrgName());
            newOpportunityMain.setCorpId(subpackage.getEntrustOrgId());
            newOpportunityMain.setDeptId(LocalContextHelper.getLoginOrgId());
            newOpportunityMain.setSampleSource(subpackage.getSampling());
            newOpportunityMain.setBidder(LocalContextHelper.getLoginUserId());
            newOpportunityMain.setBidderName(LocalContextHelper.getLoginUserName());

            EmcSubpackageBean relatedSubpackage = relatedSubpackageList.stream().filter(s -> ObjectUtils.equals(s.getId(), subpackage.getRelatedSubpackageId())).findFirst().orElseGet(EmcSubpackageBean::new);
            newOpportunityMain.setCustomerContact(relatedSubpackage.getCreatedById());
            Optional.ofNullable(coreUserMap.get(relatedSubpackage.getCreatedById())).ifPresent(user -> {
                newOpportunityMain.setBidderMail(user.getEmail());
                newOpportunityMain.setBidderPhone(user.getMobile());
            });

            relatedSubpackageList.stream().filter(res -> ObjectUtils.equals(subpackage.getRelatedSubpackageId(), res.getId())).findFirst().ifPresent(res -> {
                newOpportunityMain.setRemark(res.getRemark());

                String contractNo = res.getContractNo();
                contractList.stream().filter(c -> StringUtils.equals(c.getContractNo(), contractNo)).findFirst().ifPresent(contract -> {
                    String opportunityId = contract.getOpportunityId();
                    opportunityMainList.stream().filter(o -> StringUtils.equals(o.getId(), opportunityId)).findFirst().ifPresent(o -> {
                        newOpportunityMain.setAuthorizationUnitShow(o.getAuthorizationUnitShow());
                        newOpportunityMain.setDrawAreaShow(o.getDrawAreaShow());
                        newOpportunityMain.setInspectedAddShow(o.getInspectedAddShow());
                        newOpportunityMain.setInspectedUnitShow(o.getInspectedUnitShow());
                        newOpportunityMain.setProjectNameShow(o.getProjectNameShow());
                        newOpportunityMain.setEntrustededUnitAddShow(o.getEntrustededUnitAddShow());

                        newOpportunityMain.setAuthorizationUnit(o.getAuthorizationUnit());
                        newOpportunityMain.setInspectedUnit(o.getInspectedUnit());
                        newOpportunityMain.setEntrustededUnitAdd(o.getEntrustededUnitAdd());
                        newOpportunityMain.setInspectedAddr(o.getInspectedAddr());
                        newOpportunityMain.setDrawArea(o.getDrawArea());
                        newOpportunityMain.setInspectedContcts(o.getInspectedContcts());
                        newOpportunityMain.setInspectedPhone(o.getInspectedPhone());
                        newOpportunityMain.setReportContact(o.getReportContact());
                        newOpportunityMain.setReportPhone(o.getReportPhone());

                        newOpportunityMain.setProjectName(o.getProjectName());
                        newOpportunityMain.setReqsubtyp(o.getReqsubtyp());
                        newOpportunityMain.setReportCyle(o.getReportCyle());
                        newOpportunityMain.setImplementationTimes(1);
                        newOpportunityMain.setReportType(o.getReportType());
                        newOpportunityMain.setAuthorizeWay(o.getAuthorizeWay());
                        newOpportunityMain.setGetReport(o.getGetReport());
                        newOpportunityMain.setUrgentFlag(o.getUrgentFlag());
                        newOpportunityMain.setMaintainSecrecy(o.getMaintainSecrecy());
                        newOpportunityMain.setReqCycle(o.getReqCycle());

                        newOpportunityMain.setFastestDays(o.getFastestDays());
                        newOpportunityMain.setSlowestDays(o.getSlowestDays());

                        if (contract.getContractEndDate() != null) {
                            newOpportunityMain.setExt$Item("contractEndDate", DateTimeUtils.formatLocalDate(contract.getContractEndDate()));
                        }

                        newOpportunityMainList.add(newOpportunityMain);
                    });
                });
            });
        });

        try {
            String crmServerUrl = ApplicationContextHelper.getConstantValue("CRM_SERVER_URL");
            String crmCreateContractUrl = ApplicationContextHelper.getConstantValue("CRM_CREATE_SUBPACKAGE_CONTRACT_URL");
            if (StringUtils.isEmpty(crmServerUrl) || StringUtils.isEmpty(crmCreateContractUrl)) {
                throw new NetworkException("创建合同失败，未配置CRM创建合同接口");
            }

            CrmOpportunityRequestDTO<CrmOpportunityMainBean> requestDTO = new CrmOpportunityRequestDTO<>();
            requestDTO.setOperType("insert");
            requestDTO.setData(newOpportunityMainList);

            String requestJson = JSON.toJSONString(requestDTO);
            logger.info("[Create-Subpackage-Contract-Info]=======================");
            logger.info(requestJson);

            String result = HttpClientManager.getInstance().sendHttpPost(crmServerUrl + crmCreateContractUrl, requestJson);
            logger.info("[Create-Subpackage-Contract-Result]======================: {}", result);

            // 如果接口返回的数据是错误的，直接解析则会抛出异常
            CrmOpportunityResponseDTO responseDTO = JSON.parseObject(result, CrmOpportunityResponseDTO.class);
            String crmCreateState = (responseDTO.getCode() == 0) ? Constant.YES : Constant.NO;
            //给创建合同状态
            subpackageList.forEach(s -> {
                s.setStatus(crmCreateState);
                s.setHaveContract("1");
            });
            getDao().update(subpackageList, "status","haveContract");

            if (responseDTO.getCode() != 0) {
                throw new NetworkException("创建合同失败");
            }
        } catch (Exception e) {
            throw new NetworkException("网络请求错误，创建合同失败");
        }
    }

    @Override
    public Page<EmcSubpackageTaskBean> queryTasks(RestJsonWrapperBean wrapper) {
        return subpackageTaskService.selectPagination(wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void createProjPackage(RestJsonWrapperBean wrapper) {
        List<EmcProjectBean> projectList = wrapper.parse(EmcProjectBean.class, 0);
        if (projectList.isEmpty()) {
            return;
        }

        EmcProjectBean curProject = projectList.get(0);
        String subpackageCategory = wrapper.getParamValue("subpackageCategory");

        EmcSubpackageBean subpackage = new EmcSubpackageBean();
        subpackage.setId(ApplicationContextHelper.getNextIdentity());
        subpackage.setSubpackageNo(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        subpackage.setProjId(curProject.getId());
        subpackage.setProjNo(curProject.getProjNo());
        subpackage.setContractNo(curProject.getExt$Item("contractNo"));
        subpackage.setSubpackageType("create");
        subpackage.setOrgId(LocalContextHelper.getLoginUser().getExt$Item("parentOrgId"));
        subpackage.setSubpackageCategory(subpackageCategory);

        List<EmcSubpackageTaskBean> subpackageTaskList = new ArrayList<>();

        if ("beforeSampling".equals(subpackageCategory)) {
            subpackage.setSampling(Constant.YES);
            //项目登记-分包单管理这里需要通过projOrder找到监测方案
            //List<EmcProjSchemeBean> schemeList = wrapper.parse(EmcProjSchemeBean.class, 1);
            List<EmcProjOrderBean> projOrderList = wrapper.parse(EmcProjOrderBean.class);
            List<Long> projSchemeIdList  = projOrderList.stream().map(EmcProjOrderBean :: getProjSchemeId).distinct().collect(Collectors.toList());
            List<EmcProjSchemeBean> schemeList = projSchemeService.getDao().selectListByIds(projSchemeIdList);
            List<Long> schemeIdList = schemeList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList());
            List<Long> itemGroupIdList = projOrderList.stream().map(EmcProjOrderBean::getItemGroupId).distinct().collect(Collectors.toList());

            List<EmcProjOrderBean> sourceProjOrderList = projSchemeIdList.isEmpty() ? Collections.emptyList() : projOrderService.selectListByFilter(SearchFilter.instance().match("projSchemeId", schemeIdList).filter(MatchPattern.OR).match("itemGroupId", itemGroupIdList).filter(MatchPattern.OR));
            sourceProjOrderList.forEach(projOrder -> {
                EmcSubpackageTaskBean task = new EmcSubpackageTaskBean();
                task.setId(ApplicationContextHelper.getNextIdentity());
                task.setProjId(subpackage.getProjId());
                task.setSubpackageId(subpackage.getId());
                task.setSubpackageNo(subpackage.getSubpackageNo());
                task.setProjSchemeId(projOrder.getProjSchemeId());
                task.setSampleNum(projOrder.getSampleNum());
                task.setTestId(projOrder.getTestId());
                task.setTestName(projOrder.getExt$Item("testName"));
                task.setMethodId(projOrder.getMethodId());
                task.setTestMethodId(NumberUtils.parseLong(projOrder.getExt$Item("testMethodId")));
                task.setItemGroup(projOrder.getItemGroup());
                task.setItemGroupId(projOrder.getItemGroupId());
                schemeList.stream().filter(scheme -> ObjectUtils.equals(scheme.getId(), projOrder.getProjSchemeId())).findFirst().ifPresent(scheme -> {
                    task.setCycle(scheme.getCycle());
                    task.setFrequency(scheme.getFrequency());
                    task.setFolderNo(scheme.getFolderNo());
                });
                subpackageTaskList.add(task);
            });

            getDao().insert(subpackage);
            subpackageTaskService.getDao().insert(subpackageTaskList);

            sourceProjOrderList.forEach(o -> {
                o.setSubcontractorId(subpackage.getId());
                o.setSubPackageNo(subpackage.getSubpackageNo());
            });

            projOrderService.getDao().update(sourceProjOrderList, "SUBCONTRACTORID", "SUBPACKAGENO");
            schemeList.forEach(s ->{
                s.setExt$Item("subpackageflag","1");
            });
            projSchemeService.getDao().update(schemeList,"subpackageflag");
            return;
        }

        subpackage.setSampling(Constant.NO);
        //List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class, 1);
        List<EmcOrderItemGroupBean> orderItemGroupList = wrapper.parse(EmcOrderItemGroupBean.class);
        List<Long> orderIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean :: getOrderId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> orderList  = orderService.getDao().selectListByIds(orderIdList);
        List<Long> itemGroupIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());

        List<EmcOrderItemGroupBean> sourceItemGroupList = orderItemGroupService.selectListByFilter(SearchFilter.instance().match("orderId", orderIdList).filter(MatchPattern.OR).match("itemGroupId", itemGroupIdList).filter(MatchPattern.OR));
        sourceItemGroupList.forEach(group -> {
            EmcSubpackageTaskBean task = new EmcSubpackageTaskBean();
            task.setId(ApplicationContextHelper.getNextIdentity());
            task.setProjId(subpackage.getProjId());
            task.setSubpackageId(subpackage.getId());
            task.setSubpackageNo(subpackage.getSubpackageNo());
            task.setOrderId(group.getOrderId());
            task.setTestId(group.getTestId());
            task.setTestName(group.getTestName());
            task.setTestMethodId(group.getTestMethodId());
            task.setMethodId(group.getMethodId());
            task.setItemGroup(group.getItemGroup());
            task.setItemGroupId(group.getItemGroupId());

            orderList.stream().filter(order -> ObjectUtils.equals(group.getOrderId(), order.getId())).findFirst().ifPresent(order -> task.setOrderNo(order.getOrderNo()));
            subpackageTaskList.add(task);
        });

        sourceItemGroupList.forEach(group -> {
            group.setSubcontractorId(subpackage.getId());
            group.setSubPackageNo(subpackage.getSubpackageNo());
        });

        getDao().insert(subpackage);
        subpackageTaskService.getDao().insert(subpackageTaskList);
        orderList.forEach(o ->{
            o.setExt$Item("subpackageflag","1");
        });
        orderService.getDao().update(orderList,"subpackageflag");
        orderItemGroupService.getDao().update(sourceItemGroupList, "SUBCONTRACTORID", "SUBPACKAGENO");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void delete(RestJsonWrapperBean wrapperBean) {
        List<EmcSubpackageBean> emcSubpackageList = wrapperBean.parse(EmcSubpackageBean.class);
        List<Long> emcSubpackageIds = emcSubpackageList.stream().map(EmcSubpackageBean :: getId).distinct().collect(Collectors.toList());
        List<EmcSubpackageBean> emcSubpackageBeans = selectListByIds(emcSubpackageIds);
        List<EmcSubpackageTaskBean> emcSubpackageTaskBeans = subpackageTaskService.getDao().selectListByOneColumnValues(emcSubpackageIds,"subpackageId");
        List<Long> emcSubpackageTaskIds = emcSubpackageTaskBeans.stream().map(EmcSubpackageTaskBean :: getId).distinct().collect(Collectors.toList());
        subpackageTaskService.getDao().deleteByIdList(emcSubpackageTaskIds);
        //采样分包单
        List<EmcSubpackageBean> samplepackageList  = emcSubpackageBeans.stream().filter(s ->StringUtils.equals("1",s.getSampling())).collect(Collectors.toList());
        //送样分包单
        List<EmcSubpackageBean> noSamplepackageList  = emcSubpackageBeans.stream().filter(s ->StringUtils.equals("0",s.getSampling())).collect(Collectors.toList());
        if(!samplepackageList.isEmpty()){
            List<String> sampleSubpackageNoList = samplepackageList.stream().map(EmcSubpackageBean :: getSubpackageNo).distinct().collect(Collectors.toList());
            List<EmcProjOrderBean> projOrderBeans = projOrderService.getDao().selectListByOneColumnValues(sampleSubpackageNoList,"subPackageNo");
            if(!projOrderBeans.isEmpty()){
                List<Long> projSchemeIdList = projOrderBeans.stream().map(EmcProjOrderBean :: getProjSchemeId).distinct().collect(Collectors.toList());
                List<EmcProjSchemeBean> projSchemeBeans = projSchemeService.getDao().selectListByIds(projSchemeIdList);
                projSchemeBeans.forEach(ps ->{
                    ps.setExt$Item("subpackageflag","");
                });
                projOrderBeans.forEach(po ->{
                    po.setSubcontractorId(null);
                    po.setSubPackageNo("");
                });
                projSchemeService.getDao().update(projSchemeBeans,"subpackageflag");
                projOrderService.getDao().update(projOrderBeans,"subPackageNo","subcontractorId");
            }
        }
        if(!noSamplepackageList.isEmpty()){
            List<String> noSampleSubpackageNoList = noSamplepackageList.stream().map(EmcSubpackageBean :: getSubpackageNo).distinct().collect(Collectors.toList());
            List<EmcOrderItemGroupBean> orderItemGroupBeans = orderItemGroupService.getDao().selectListByOneColumnValues(noSampleSubpackageNoList,"subPackageNo");
            if(!orderItemGroupBeans.isEmpty()){
                List<Long> orderIdList = orderItemGroupBeans.stream().map(EmcOrderItemGroupBean :: getOrderId).distinct().collect(Collectors.toList());
                List<EmcOrderBean> orderBeans = orderService.selectListByIds(orderIdList);
                orderBeans.forEach(o ->{
                    o.setExt$Item("subpackageflag","");
                });
                orderItemGroupBeans.forEach(oi ->{
                    oi.setSubPackageNo("");
                    oi.setSubcontractorId(null);
                });
                orderService.getDao().update(orderBeans,"subpackageflag");
                orderItemGroupService.getDao().update(orderItemGroupBeans,"subPackageNo","subcontractorId");
            }
        }
        getDao().deleteByIdList(emcSubpackageIds);

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public String cancelPackage(RestJsonWrapperBean wrapperBean) {
        //撤销分包需要将对方的分包单以及分包的测试的删除
        EmcSubpackageBean subpackageBean = wrapperBean.parseUnique(EmcSubpackageBean.class);
        EmcSubpackageBean subpackage = getDao().selectById(subpackageBean.getId());
        String result ="";
        //已经委托的分包单
        List<EmcSubpackageBean> sendSubpackages = getDao().selectListByOneColumnValue(subpackageBean.getId(),"relatedSubpackageId");
        if(sendSubpackages.size()>0){
            List<String> haveContracts = sendSubpackages.stream().map(EmcSubpackageBean :: getHaveContract).filter(t ->!StringUtils.isEmpty(t))
                    .distinct().collect(Collectors.toList());
            if(haveContracts.isEmpty()){
                List<Long> sendSubpackageIds = sendSubpackages.stream().map(EmcSubpackageBean :: getId).distinct().collect(Collectors.toList());
                List<EmcSubpackageTaskBean> sendSubpackageTasks = subpackageTaskService.getDao().selectListByOneColumnValues(sendSubpackageIds,"subpackageId");
                List<Long> sendSubpackageTaskIds = sendSubpackageTasks.stream().map(EmcSubpackageTaskBean :: getId).distinct().collect(Collectors.toList());
                //删除分包委托单及其测试,并将父分包单分包类型改为未委托
                subpackage.setStatus("0");
                subpackage.setEntrustOrgId("");
                subpackage.setEntrustOrgName("");
                getDao().update(subpackage,"status","entrustOrgId","entrustOrgName");
                getDao().deleteByIdList(sendSubpackageIds);
                subpackageTaskService.getDao().deleteByIdList(sendSubpackageTaskIds);
            }else{
                String haveContract = haveContracts.get(0);
                if(StringUtils.equals(haveContract,"1")){
                    return  "NO";
                }else{
                    List<Long> sendSubpackageIdsTe = sendSubpackages.stream().map(EmcSubpackageBean :: getId).distinct().collect(Collectors.toList());
                    List<EmcSubpackageTaskBean> sendSubpackageTasksTe = subpackageTaskService.getDao().selectListByOneColumnValues(sendSubpackageIdsTe,"subpackageId");
                    List<Long> sendSubpackageTaskIdsTe = sendSubpackageTasksTe.stream().map(EmcSubpackageTaskBean :: getId).distinct().collect(Collectors.toList());
                    //删除分包委托单及其测试,并将父分包单分包类型改为未委托
                    subpackage.setStatus("0");
                    subpackage.setEntrustOrgId("");
                    subpackage.setEntrustOrgName("");
                    getDao().update(subpackage,"status","entrustOrgId","entrustOrgName");
                    getDao().deleteByIdList(sendSubpackageIdsTe);
                    subpackageTaskService.getDao().deleteByIdList(sendSubpackageTaskIdsTe);
                }
            }
        }
        return  result;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deletePackageTask(RestJsonWrapperBean wrapperBean) {
        List<EmcSubpackageTaskBean> subpackageTaskBeans = wrapperBean.parse(EmcSubpackageTaskBean.class);
        List<Long> subpackageTaskIds = subpackageTaskBeans.stream().map(EmcSubpackageTaskBean :: getId).distinct().collect(Collectors.toList());
        List<Long> testMethodIdList = subpackageTaskBeans.stream().map(EmcSubpackageTaskBean :: getTestMethodId).distinct().collect(Collectors.toList());
        List<Long> testIdList = subpackageTaskBeans.stream().map(EmcSubpackageTaskBean :: getTestId).distinct().collect(Collectors.toList());
        List<Long> methodIdList = subpackageTaskBeans.stream().map(EmcSubpackageTaskBean :: getMethodId).distinct().collect(Collectors.toList());

        Long subpackageId = subpackageTaskBeans.get(0).getSubpackageId();
        EmcSubpackageBean subpackageBean = selectById(subpackageId);

        boolean sampling = Constant.YES.equals(subpackageBean.getSampling());
        if(sampling){
            Long projSchemeId = subpackageTaskBeans.get(0).getProjSchemeId();
            EmcProjSchemeBean projSchemeBean = projSchemeService.getDao().selectById(projSchemeId);
            List<EmcProjOrderBean> projOrderBeans = projOrderService.getDao().selectListByOneColumnValue(projSchemeId,"projSchemeId");
            List<EmcProjOrderBean> projOrderTargetBeans =  projOrderBeans.stream().filter(po ->methodIdList.contains(po.getMethodId()))
                    .filter(po ->testIdList.contains(po.getTestId())).filter(po ->!StringUtils.isEmpty(po.getSubPackageNo())).collect(Collectors.toList());
            if(!projOrderTargetBeans.isEmpty()){
                projOrderTargetBeans.forEach(po ->{
                    po.setSubPackageNo("");
                    po.setSubcontractorId(null);
                });
                projOrderService.getDao().update(projOrderTargetBeans,"subPackageNo","subcontractorId");
            }
        }else{
            Long orderId = subpackageTaskBeans.get(0).getOrderId();
            EmcOrderBean orderBean = orderService.selectById(orderId);
            List<EmcOrderItemGroupBean> orderItemGroupBeans = orderItemGroupService.getDao().selectListByOneColumnValue(orderId,"orderId");
            List<EmcOrderItemGroupBean> orderItemGroupTargetBeans = orderItemGroupBeans.stream()
                    .filter(oig ->testMethodIdList.contains(oig.getTestMethodId())).filter(oig ->!StringUtils.isEmpty(oig.getSubPackageNo())).collect(Collectors.toList());
            orderItemGroupTargetBeans.forEach(oig ->{
                oig.setSubcontractorId(null);
                oig.setSubPackageNo("");
            });
            orderItemGroupService.getDao().update(orderItemGroupTargetBeans,"subPackageNo","subcontractorId");
        }
        subpackageTaskService.getDao().deleteByIdList(subpackageTaskIds);

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void completePackage(RestJsonWrapperBean wrapperBean) {
        List<EmcSubpackageBean> subpackageBeans = wrapperBean.parse(EmcSubpackageBean.class);
        if(!subpackageBeans.isEmpty()){
            subpackageBeans.forEach(s -> s.setStatus("2"));
            getDao().update(subpackageBeans,"status");
        }
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void submitToSubpackage(RestJsonWrapperBean wrapperBean) {
        List<EmcSubpackageBean> subpackageBeans = wrapperBean.parse(EmcSubpackageBean.class);
        if(!subpackageBeans.isEmpty()){
            subpackageBeans.forEach(s -> s.setStatus(Constant.NO));
            getDao().update(subpackageBeans,"status");
        }
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void undoSubpackage(RestJsonWrapperBean wrapperBean) {
        List<EmcSubpackageBean> subpackageBeans = wrapperBean.parse(EmcSubpackageBean.class);
        if(!subpackageBeans.isEmpty()){
            subpackageBeans.forEach(s -> s.setStatus(Constant.NO));
            getDao().update(subpackageBeans,"status");
        }

    }


}
