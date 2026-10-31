package com.sunwayworld.emc.module.mdm.crm.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import com.google.common.util.concurrent.AtomicDouble;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.utils.EmcNumberUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.service.PfFolderService;
import com.sunwayworld.emc.module.bp.bp.service.PfOrderService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.project.project.service.PfOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjOrderService;
import com.sunwayworld.emc.module.bp.project.projscheme.service.PfProjSchemeService;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean.EmcBizCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.service.EmcBizCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcTestCapacityBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcTestCapacityService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorTmplService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcRelatedTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodOrderCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcRelatedTestMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodOrderCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityContractBean;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityFoldersBean;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityMainBean;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityOrdersBean;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityOrdtaskBean;
import com.sunwayworld.emc.module.mdm.crm.dao.CrmOpportunityContractDao;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityContractService;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityFoldersService;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityMainService;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityOpenService;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityOrdersService;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityOrdtaskService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;

/**
 * @author zhengxy@sunwayworld.com 2021年08月26日
 */
@Repository
public class CrmOpportunityContractServiceImpl implements CrmOpportunityContractService {

    private static final long MAX_FOLDER_SIZE_PER_TIME = 500L;

    @Autowired
    private CrmOpportunityMainService opportunityMainService;

    @Autowired
    private CrmOpportunityOrdersService opportunityOrdersService;

    @Autowired
    private CrmOpportunityOrdtaskService opportunityOrdtaskService;

    @Autowired
    private EmcMonitorTmplService monitorTmplService;

    @Autowired
    @Lazy
    private EmcProjectService projectService;

    @Autowired
    @Lazy
    private PfProjSchemeService projSchemeService;

    @Autowired
    private EmcProjOrderService projOrderService;

    @Autowired
    private EmcBizCategoryService bizCategoryService;

    @Autowired
    private PfOrderService orderService;

    @Autowired
    @Lazy
    private PfFolderService folderService;

    @Autowired
    private PfOrderItemGroupService orderItemGroupService;

    @Autowired
    private EmcCapacityItemGroupService capacityItemGroupService;

    @Autowired
    private EmcTestMethodService testMethodService;

    @Autowired
    private EmcTestCapacityService testCapacityService;

    @Autowired
    private CoreOrgService coreOrgService;

    @Autowired
    private EmcGroupItemService groupItemService;

    @Autowired
    private EmcOrderItemService orderItemService;

    @Autowired
    private EmcTestMethodOrderCategoryService testMethodOrderCategoryService;

    @Autowired
    private EmcRelatedTestMethodService relatedTestMethodService;

    @Autowired
    private CoreFileService coreFileService;

    @Autowired
    @Lazy
    private CrmOpportunityOpenService opportunityOpenService;

    @Autowired
    private CrmOpportunityContractDao crmOpportunityContractDao;


    @Autowired
    @Lazy
    private CrmOpportunityFoldersService opportunityFoldersService;


    @Override
    @SuppressWarnings("unchecked")
    public CrmOpportunityContractDao getDao() {
        return crmOpportunityContractDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public String insert(RestJsonWrapperBean wrapperBean) {
        CrmOpportunityContractBean crmOpportunityContractBean = wrapperBean.parseUnique(CrmOpportunityContractBean.class);
        String id = ApplicationContextHelper.getNextIdentity() + "";
        crmOpportunityContractBean.setId(id);
        getDao().insert(crmOpportunityContractBean);
        return id;
    }

    @Override
    @Transactional
    public void createProject(RestJsonWrapperBean wrapper) {
        String opportunityId = wrapper.getParamValue("opportunityId");
        List<CrmOpportunityOrdersBean> opportunityOrderList = wrapper.parse(CrmOpportunityOrdersBean.class);
        List<CrmOpportunityOrdtaskBean> opportunityOrderTaskList = wrapper.parse(CrmOpportunityOrdtaskBean.class);

        // 如果选择了多个点位，默认每个点位下的所有测试全部下到任务中
        if (opportunityOrderList.size() > 1) {
            List<String> opportunityOrderIdList = opportunityOrderList.stream().map(CrmOpportunityOrdersBean::getId).collect(Collectors.toList());
            opportunityOrderTaskList = opportunityOrdtaskService.getDao().selectListByOneColumnValues(opportunityOrderIdList, "ordersId");
        }

        String opportunityNo = opportunityOrderList.get(0).getOpportunityNo();
        //CrmOpportunityMainBean opportunityMain = opportunityMainService.selectFirstByFilter(SearchFilter.instance().match("opportunityNo", opportunityNo).filter(MatchPattern.SEQ));
        CrmOpportunityMainBean opportunityMain = opportunityMainService.selectFirstByFilter(SearchFilter.instance().match("id", opportunityId).filter(MatchPattern.SEQ));

        List<String> testTypeList = opportunityOrderList.stream().map(CrmOpportunityOrdersBean::getTestType).distinct().collect(Collectors.toList());

        List<EmcMonitorTmplBean> monitorTmplList = monitorTmplService.selectListByFilter(SearchFilter.instance().match("monitorTmplName", testTypeList).filter(MatchPattern.OR)
                .match("orgId", LocalContextHelper.getLoginUser().getExt$Item("parentOrgId")).filter(MatchPattern.EQ));
        Map<String, EmcMonitorTmplBean> monitorTmplMap = monitorTmplList.stream().collect(Collectors.toMap(EmcMonitorTmplBean::getMonitorTmplName, Function.identity(), (v1, v2) -> v1));

        String bizCategoryId = wrapper.getParamValue("bizCategoryId");
        EmcBizCategoryBean bizCategory = bizCategoryService.selectById(NumberUtils.parseLong(bizCategoryId));
        List<CrmOpportunityContractBean> contractList = getDao().selectListByOneColumnValue(opportunityMain.getId(), "opportunityId");

        // 查询项目编号
        List<String> contractNoList = contractList.stream().map(CrmOpportunityContractBean::getContractNo).collect(Collectors.toList());

        RestJsonWrapperBean queryWrapper = new RestJsonWrapperBean();
        queryWrapper.setFilterValue("targetType", "modifyProjectInfo");
        List<EmcProjectBean> existedProjectList = projectService.selectListByFilter(SearchFilter.instance().match("projNo", contractNoList).filter(MatchPattern.SB), queryWrapper);
        List<String> existedProjectNoList = existedProjectList.stream().map(EmcProjectBean::getProjNo).collect(Collectors.toList());
        Map<String, Integer> projectSeqNoMap = new HashMap<>(contractNoList.size());
        contractNoList.forEach(contractNo -> {
            int curMaxSeqNoValue = existedProjectNoList.stream()
                    .filter(projectNo -> StringUtils.startsWith(projectNo, contractNo))
                    .map(projectNo -> projectNo.replace(contractNo, "").replaceAll("[A-Z]", ""))
                    .filter(NumberUtils::isNumber)
                    .mapToInt(NumberUtils::parseInt)
                    .max().orElse(0);
            int nextSeqNoValue = curMaxSeqNoValue + 1;
            projectSeqNoMap.put(contractNo, nextSeqNoValue);
        });

        Map<String, CrmOpportunityContractBean> contractMap = contractList.stream().collect(Collectors.toMap(CrmOpportunityContractBean::getOpportunityId, Function.identity()));

        // 获取前端选择的样品来源，创建任务时以前端传的样品来源为准，采样的合同也可以创建为送样的任务
        String sampleSource = wrapper.getParamValue("sampleSource");
        opportunityMain.setSampleSource(sampleSource);

        List<CrmOpportunityOrdtaskBean> selectedOrderTaskList = new ArrayList<>(opportunityOrderTaskList);
        EmcProjectBean newProject = createProject(opportunityMain, contractMap, projectSeqNoMap, bizCategory);

        // 计算项目开案金额
        //List<CrmOpportunityOrdersBean> allOpportunityOrderList = opportunityOrdersService.getDao().selectListByOneColumnValue(opportunityNo, "opportunityNo");
        List<CrmOpportunityFoldersBean> allFoldersList = opportunityFoldersService.getDao().selectListByOneColumnValue(opportunityId, "opportunityId");
        List<String> allFoldersIdList = allFoldersList.stream().map(CrmOpportunityFoldersBean ::getId).collect(Collectors.toList());

        List<CrmOpportunityOrdersBean> allBeforeOpportunityOrderList = opportunityOrdersService.getDao().selectListByOneColumnValue(opportunityNo, "opportunityNo");
        List<CrmOpportunityOrdersBean> allOpportunityOrderList = allBeforeOpportunityOrderList.stream().filter(ao ->allFoldersIdList.contains(ao.getFoldersId())).collect(Collectors.toList());
        List<EmcProjectBean> createdProjectList = projectService.getDao().selectList(newProject, Collections.singletonList("contractNo"), Arrays.asList("id", "projectAmount"));
        String projectAmount = calcProjectAmount(opportunityMain, createdProjectList, allOpportunityOrderList, opportunityOrderList);
        newProject.setExt$Item("projectAmount", projectAmount);

        opportunityOrderList.sort(Comparator.comparing(CrmOpportunityOrdersBean::getSorter));
        //给上样品类型
        selectedOrderTaskList.forEach(ok ->{
            //测试对应的order,应该只有一个
            List<CrmOpportunityOrdersBean> opportunityOrderSampleSelectList = opportunityOrderList.stream().filter(oo ->ObjectUtils.equals(oo.getId(),ok.getOrdersId())).collect(Collectors.toList());
            ok.setExt$Item("testtype",opportunityOrderSampleSelectList.get(0).getTestType());
        });
        // 采样
        if (StringUtils.equals(Constant.YES, newProject.getSample())) {
            List<EmcProjSchemeBean> schemeList = new ArrayList<>();
            List<EmcProjOrderBean> orderList = new ArrayList<>();
            Map<String, String> maxSeqNoMap = new HashMap<>(opportunityOrderList.size());

            String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
            List<CoreOrgBean> sampleOrgList = coreOrgService.selectListByFilter(SearchFilter.instance().match("id", parentOrgId).filter(MatchPattern.SB)
                    .match("orgCategory", "department").filter(MatchPattern.EQ).match("sampleDepartment", "1").filter(MatchPattern.EQ));
            // 创建关联测试
            Map<String, List<EmcProjOrderBean>> relatedProjOrderMap = createRelatedProjOrder(selectedOrderTaskList);

            // 查询分析项组对应的测试方法，开案时以测试管理最新的方法为基准
            List<Long> capacityItemGroupIdList = selectedOrderTaskList.stream().map(CrmOpportunityOrdtaskBean::getTestGrouporgId).filter(NumberUtils::isNumber).mapToLong(NumberUtils::parseLong).distinct().boxed().collect(Collectors.toList());
            List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByOneColumnValues(capacityItemGroupIdList, "id", Arrays.asList("id", "testMethodId"));
            List<Long> testMethodIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getTestMethodId).distinct().collect(Collectors.toList());
            List<EmcTestMethodBean> testMethodList = testMethodService.getDao().selectListByOneColumnValues(testMethodIdList, "id", Arrays.asList("id", "testId", "methodId"));
            Map<String, EmcTestMethodBean> itemGroupTestMethodMap = new HashMap<>(capacityItemGroupIdList.size());
            capacityItemGroupList.forEach(capacityItemGroup -> testMethodList.stream().filter(testMethod -> ObjectUtils.equals(capacityItemGroup.getTestMethodId(), testMethod.getId()))
                    .findFirst().ifPresent(testMethod -> itemGroupTestMethodMap.put(String.valueOf(capacityItemGroup.getId()), testMethod)));

            // 判断是否有需要采样前分包的测试
            boolean containSubcontract = selectedOrderTaskList.stream().anyMatch(task -> Constant.YES.equals(task.getSubcontract2()));
            String subPackageNo = containSubcontract ? ApplicationContextHelper.getNextSequence(orderItemGroupService.getDao().getTable()) : null;

            Map<String, List<CrmOpportunityOrdersBean>> associatedOrderMap = parseAssociatedPoint(opportunityOrderList);
            associatedOrderMap.forEach((mainOrderId, associatedOrderList) -> {
                CrmOpportunityOrdersBean mainOpportunityOrder = associatedOrderList.stream().filter(order -> StringUtils.equals(order.getId(), mainOrderId)).findFirst().orElseGet(CrmOpportunityOrdersBean::new);
                List<String> opportunityOrderIdList = associatedOrderList.stream().map(CrmOpportunityOrdersBean::getId).collect(Collectors.toList());

                List<CrmOpportunityOrdtaskBean> curTaskList = selectedOrderTaskList.stream().filter(task -> opportunityOrderIdList.contains(task.getOrdersId())).collect(Collectors.toList());

                List<EmcProjOrderBean> relatedProjOrderList = new ArrayList<>();
                opportunityOrderIdList.forEach(orderId -> Optional.ofNullable(relatedProjOrderMap.get(orderId)).ifPresent(relatedProjOrderList::addAll));

                EmcMonitorTmplBean monitorTmpl = Optional.ofNullable(monitorTmplMap.get(mainOpportunityOrder.getTestType())).orElseGet(EmcMonitorTmplBean::new);
                int folderNum = Optional.ofNullable(mainOpportunityOrder.getPotNum()).orElse(1D).intValue();
                String orderCategoryCode = monitorTmpl.getExt$Item("orderCategoryCode");
                List<String> folderNoList = projSchemeService.generateFolderNos(newProject, orderCategoryCode, folderNum);

                convertFolderNo(maxSeqNoMap, orderCategoryCode, folderNoList, folderNum);

                List<String> folderBeforeNameList = splitFolderName(mainOpportunityOrder.getPotName());
                List<String> folderNameList = folderBeforeNameList.stream().filter(f ->!StringUtils.isEmpty(f)).collect(Collectors.toList());
                boolean isIdentical = (folderNum == folderNameList.size());

                // 获取周期频次，取最大的周期和频次
                Long associatedFrequency = associatedOrderList.stream().map(CrmOpportunityOrdersBean::getTestingFrequency).max(Comparator.comparing(Function.identity())).orElse(1L);
                Long associatedCycle = associatedOrderList.stream().map(CrmOpportunityOrdersBean::getTestingDays).max(Comparator.comparing(Function.identity())).orElse(1L);
                String contractpotname = associatedOrderList.get(0).getPotName();
                long curFrequency = Math.max(mainOpportunityOrder.getTestingFrequency(), associatedFrequency);
                long curCycle = Math.max(mainOpportunityOrder.getTestingDays(), associatedCycle);

                IntStream.range(0, folderNum).forEach(folderIndex -> {
                    EmcProjSchemeBean newScheme = new EmcProjSchemeBean();
                    newScheme.setId(ApplicationContextHelper.getNextIdentity());
                    newScheme.setProjId(newProject.getId());
                    newScheme.setFrequency(curFrequency);
                    newScheme.setCycle(curCycle);
                    newScheme.setMonitorTmplId(monitorTmpl.getId());
                    newScheme.setFolderNo(folderNoList.get(folderIndex));
                    newScheme.setMonitorPointName(isIdentical ? folderNameList.get(folderIndex) : mainOpportunityOrder.getPotName() + "-" + (folderIndex + 1));
                    newScheme.setRemark(mainOpportunityOrder.getComments());
                    newScheme.setExt$Item("contractpotname",contractpotname);

                    if (!sampleOrgList.isEmpty()) {
                        newScheme.setSampleOrgId(sampleOrgList.get(0).getId());
                    }

                    schemeList.add(newScheme);

                    List<EmcProjOrderBean> curRelatedProjOrderList = new ArrayList<>();
                    curTaskList.forEach(task -> {
                        CrmOpportunityOrdersBean curOrder = associatedOrderList.stream().filter(order -> StringUtils.equals(task.getOrdersId(), order.getId())).findFirst().orElse(mainOpportunityOrder);

                        EmcProjOrderBean newProjOrder = createProjOrder(newScheme, task, itemGroupTestMethodMap.get(task.getTestGrouporgId()), curOrder, subPackageNo);
                        orderList.add(newProjOrder);
                        // 添加关联测试，如果一个点位下有多个相同的关联测试，只添加一个
                        relatedProjOrderList.stream().filter(relatedOrder -> Arrays.asList(StringUtils.split(relatedOrder.getExt$Item("relatedItemGroupId"), ",")).contains(newProjOrder.getItemGroupId().toString())).forEach(relatedOrder -> {
                            EmcProjOrderBean existedRelatedOrder = curRelatedProjOrderList.stream().filter(existedOrder -> ObjectUtils.equals(existedOrder.getItemGroupId(), relatedOrder.getItemGroupId())).findFirst().orElse(null);
                            if (existedRelatedOrder == null) {
                                EmcProjOrderBean newRelatedOrder = new EmcProjOrderBean();
                                newRelatedOrder.setId(ApplicationContextHelper.getNextIdentity());
                                newRelatedOrder.setProjSchemeId(newProjOrder.getProjSchemeId());
                                newRelatedOrder.setSampleNum(newProjOrder.getSampleNum());
                                newRelatedOrder.setSubPackageName(newProjOrder.getSubPackageName());
                                newRelatedOrder.setItemGroupId(relatedOrder.getItemGroupId());
                                newRelatedOrder.setItemGroup(relatedOrder.getItemGroup());
                                newRelatedOrder.setSchemeName(relatedOrder.getSchemeName());
                                newRelatedOrder.setTestId(relatedOrder.getTestId());
                                newRelatedOrder.setMethodId(relatedOrder.getMethodId());
                                newRelatedOrder.setExt$Item("relatedTest", Constant.YES);
                                curRelatedProjOrderList.add(newRelatedOrder);
                            } else if (newProjOrder.getSampleNum() > existedRelatedOrder.getSampleNum()) {
                                // 取采样数最大的，比如镍和镉的采样数分别为1和2，且都关联水分含量，则水分含量的采样数应为2
                                existedRelatedOrder.setSampleNum(newProjOrder.getSampleNum());
                            }
                        });
                    });
                    orderList.addAll(curRelatedProjOrderList);
                });
            });

            projSchemeService.getDao().insert(schemeList);
            projOrderService.getDao().insert(orderList);
        }

        // 送样
        if (!StringUtils.equals(Constant.YES, newProject.getSample())) {
            List<EmcFolderBean> newFolderList = new ArrayList<>();
            List<EmcOrderBean> newOrderList = new ArrayList<>();
            List<EmcOrderItemGroupBean> newItemGroupList = new ArrayList<>();
            List<EmcOrderItemBean> newOrderItemList = new ArrayList<>();
            Map<String, String> maxSeqNoMap = new HashMap<>(opportunityOrderList.size());
            // 判断是否有需要采样后分包的测试
            boolean containSubcontractBeForeSample = selectedOrderTaskList.stream().anyMatch(task -> Constant.YES.equals(task.getSubcontract()));
            String beforeSampleSubPackageNo = containSubcontractBeForeSample ? ApplicationContextHelper.getNextSequence(orderItemGroupService.getDao().getTable()) : null;

            // 关联测试
            List<EmcOrderItemGroupBean> relatedOrderItemGroupList = createRelatedOrderItemGroup(selectedOrderTaskList);
            //如果样品类型为固体废物，去除水分含量
            List<Long> relatedCapacityItemGroupIdList = relatedOrderItemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());

            List<Long> capacityItemGroupIdList = opportunityOrderTaskList.stream().map(CrmOpportunityOrdtaskBean::getTestGrouporgId).mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());
            capacityItemGroupIdList.addAll(relatedCapacityItemGroupIdList);

            List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.selectListByIds(capacityItemGroupIdList);
            List<Long> testMethodIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getTestMethodId).distinct().collect(Collectors.toList());
            List<EmcTestMethodBean> testMethodList = testMethodService.selectListByFilter(SearchFilter.instance().match("id", testMethodIdList).filter(MatchPattern.OR));
            Map<Long, Long> capacityItemGroupIdMap = capacityItemGroupList.stream().collect(Collectors.toMap(EmcCapacityItemGroupBean::getId, EmcCapacityItemGroupBean::getTestMethodId, (v1, v2) -> v1));
            Map<Long, EmcTestMethodBean> testMethodMap = testMethodList.stream().collect(Collectors.toMap(EmcTestMethodBean::getId, Function.identity()));
            List<Long> capacityIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getTestCapacityId).distinct().collect(Collectors.toList());
            Map<Long, Long> itemGroupCapacityIdMap = capacityItemGroupList.stream().collect(Collectors.toMap(EmcCapacityItemGroupBean::getId, EmcCapacityItemGroupBean::getTestCapacityId, (v1, v2) -> v1));
            List<EmcTestCapacityBean> capacityList = testCapacityService.selectListByIds(capacityIdList);
            Map<Long, EmcTestCapacityBean> capacityMap = capacityList.stream().collect(Collectors.toMap(EmcTestCapacityBean::getId, Function.identity()));

            // 查询容器信息
            List<String> monitorTmplNameList = opportunityOrderList.stream().map(CrmOpportunityOrdersBean::getTestType).distinct().collect(Collectors.toList());
            List<EmcMonitorTmplBean> monitorTmplCList = monitorTmplService.getDao().selectListByOneColumnValues(monitorTmplNameList, "MONITORTMPLNAME");
            List<String> monitorTmplIdList = monitorTmplCList.stream().map(EmcMonitorTmplBean ::getId).collect(Collectors.toList());
            List<EmcTestMethodOrderCategoryBean> testMethodOrderCategoryList = testMethodOrderCategoryService.selectListByFilter(SearchFilter.instance().match("testMethodId", testMethodIdList).filter(MatchPattern.OR).match("monitorTmplId", monitorTmplIdList).filter(MatchPattern.OR));

            // 查询分析项信息
            List<Long> itemGroupIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
            List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "itemGroupId");
            Map<Long, Long> itemGroupIdMap = capacityItemGroupList.stream().collect(Collectors.toMap(EmcCapacityItemGroupBean::getId, EmcCapacityItemGroupBean::getItemGroupId, (v1, v2) -> v1));

            opportunityOrderList.forEach(opportunityOrder -> {
                List<CrmOpportunityOrdtaskBean> curTaskList = selectedOrderTaskList.stream().filter(task -> StringUtils.equals(task.getOrdersId(), opportunityOrder.getId())).collect(Collectors.toList());

                EmcMonitorTmplBean monitorTmpl = Optional.ofNullable(monitorTmplMap.get(opportunityOrder.getTestType())).orElseGet(EmcMonitorTmplBean::new);
                int folderNum = Optional.ofNullable(opportunityOrder.getPotNum()).orElse(1D).intValue();
                String orderCategoryCode = monitorTmpl.getExt$Item("orderCategoryCode");
                List<String> folderNoList = projSchemeService.generateFolderNos(newProject, monitorTmpl.getExt$Item("orderCategoryCode"), folderNum);

                convertFolderNo(maxSeqNoMap, orderCategoryCode, folderNoList, folderNum);

                List<String> folderBeforeNameList = splitFolderName(opportunityOrder.getPotName());
                List<String> folderNameList = folderBeforeNameList.stream().filter(f ->!StringUtils.isEmpty(f)).collect(Collectors.toList());
                boolean isIdentical = (folderNum == folderNameList.size());

                // 说明：这里送样的总样品个数计算用到了周期和频次，对于送样业务而言，周期和频次无实际意义
                long curFrequency = Optional.ofNullable(opportunityOrder.getTestingFrequency()).orElse(1L);
                long curCycle = Optional.ofNullable(opportunityOrder.getTestingDays()).orElse(1L);
                //Long inspectTimes = Optional.ofNullable(opportunityOrder.getNumberOfInspections()).orElse(1L);

                // 样品个数
                long curOrderNum = curTaskList.stream().map(CrmOpportunityOrdtaskBean::getSamplePoints).max(Comparator.comparing(Function.identity())).orElse(1L);
                long orderNum = curFrequency * curCycle * curOrderNum;
                // 每一个频次下的样品数
                long preFreOrderNum = curCycle * curOrderNum;

                IntStream.range(0, folderNum).forEach(folderIndex -> {
                    EmcFolderBean newFolder = createFolder(newProject, folderNoList, folderIndex, monitorTmpl);
                    newFolder.setExt$Item("contractpotname",opportunityOrder.getPotName());
                    newFolderList.add(newFolder);
                    if(orderNum==1L){
                        EmcOrderBean newOrder = createSingleOrder(newProject, newFolder, folderNameList, isIdentical, opportunityOrder, folderIndex, monitorTmpl,1);
                        newOrderList.add(newOrder);

                        List<EmcOrderItemGroupBean> curRelatedOrderItemGroupList = new ArrayList<>();
                        curTaskList.forEach(task -> {
                            EmcOrderItemGroupBean orderItemGroup = createOrderItemGroup(newOrder, task, monitorTmpl, capacityItemGroupIdMap, testMethodMap, itemGroupCapacityIdMap, capacityMap,beforeSampleSubPackageNo);
                            // 容器信息
                            testMethodOrderCategoryList.stream().filter(tmoc -> ObjectUtils.equals(orderItemGroup.getTestMethodId(), tmoc.getTestMethodId())
                                    && StringUtils.equals(orderItemGroup.getMonitorTmplId(), tmoc.getMonitorTmplId())).findFirst().ifPresent(tmoc -> {
                                orderItemGroup.setContainerCategoryId(tmoc.getContainerCategoryId());
                                orderItemGroup.setContainerCategoryCode(tmoc.getExt$Item("containercategorycode"));
                                orderItemGroup.setContainerCategoryName(tmoc.getExt$Item("containercategoryname"));
                                orderItemGroup.setContainerGroup(tmoc.getContainerGroup());
                                orderItemGroup.setFixative(tmoc.getFixative());
                            });

                            newItemGroupList.add(orderItemGroup);

                            // 样品分析项信息
                            Optional.ofNullable(itemGroupIdMap.get(orderItemGroup.getItemGroupId())).ifPresent(itemGroupId -> {
                                groupItemList.stream().filter(item -> ObjectUtils.equals(itemGroupId, item.getItemGroupId())).forEach(item -> {
                                    EmcOrderItemBean orderItem = new EmcOrderItemBean();
                                    orderItem.setId(ApplicationContextHelper.getNextIdentity());
                                    orderItem.setItemId(item.getItemId());
                                    orderItem.setOrderItemGroupId(orderItemGroup.getId());
                                    orderItem.setOrderId(newOrder.getId());
                                    newOrderItemList.add(orderItem);
                                });
                            });

                            // 添加关联测试，如果一个样品下有多个相同的关联测试，只添加一个
                            //如果类型是固废不需要水分含量
                            if(StringUtils.equals(newOrder.getOrderCategory(),PfConstant.SOLID_WASTE_WORD)){
                                List<EmcOrderItemGroupBean> relatedOrderItemGroupMatchList =relatedOrderItemGroupList.stream().filter(relatedOrder ->relatedOrder.getTestId()!=275).collect(Collectors.toList());
                                relatedOrderItemGroupMatchList.stream().filter(relatedOrder -> Arrays.asList(StringUtils.split(relatedOrder.getExt$Item("relatedItemGroupId"), ",")).contains(orderItemGroup.getItemGroupId().toString())).findFirst().ifPresent(relatedOrder -> {
                                    boolean notExisted = curRelatedOrderItemGroupList.stream().noneMatch(existedOrder -> ObjectUtils.equals(existedOrder.getItemGroupId(), relatedOrder.getItemGroupId()));
                                    if (notExisted) {
                                        EmcOrderItemGroupBean newRelatedOrderItemGroup = new EmcOrderItemGroupBean();
                                        newRelatedOrderItemGroup.setId(ApplicationContextHelper.getNextIdentity());
                                        newRelatedOrderItemGroup.setOrderId(newOrder.getId());
                                        newRelatedOrderItemGroup.setItemGroupId(relatedOrder.getItemGroupId());
                                        newRelatedOrderItemGroup.setItemGroup(relatedOrder.getItemGroup());
                                        newRelatedOrderItemGroup.setSchemeName(relatedOrder.getSchemeName());
                                        newRelatedOrderItemGroup.setTestId(relatedOrder.getTestId());
                                        newRelatedOrderItemGroup.setMethodId(relatedOrder.getMethodId());
                                        newRelatedOrderItemGroup.setTestMethodId(relatedOrder.getTestMethodId());
                                        newRelatedOrderItemGroup.setMonitorTmplId(newOrder.getMonitorTmplId());
                                        newRelatedOrderItemGroup.setOrderCategory(newOrder.getOrderCategory());
                                        newRelatedOrderItemGroup.setAnalysisOrgId(relatedOrder.getAnalysisOrgId());
                                        newRelatedOrderItemGroup.setAnalysisOrgName(relatedOrder.getAnalysisOrgName());
                                        newRelatedOrderItemGroup.setTestName(relatedOrder.getTestName());
                                        newRelatedOrderItemGroup.setMethodName(relatedOrder.getMethodName());
                                        newRelatedOrderItemGroup.setTestMethodBasis(relatedOrder.getTestMethodBasis());
                                        newRelatedOrderItemGroup.setMethodVersionId(relatedOrder.getMethodVersionId());
                                        newRelatedOrderItemGroup.setExt$Item("relatedTest", Constant.YES);
                                        curRelatedOrderItemGroupList.add(newRelatedOrderItemGroup);

                                        // 样品分析项信息
                                        Optional.ofNullable(itemGroupIdMap.get(newRelatedOrderItemGroup.getItemGroupId())).ifPresent(itemGroupId -> {
                                            groupItemList.stream().filter(item -> ObjectUtils.equals(itemGroupId, item.getItemGroupId())).forEach(item -> {
                                                EmcOrderItemBean orderItem = new EmcOrderItemBean();
                                                orderItem.setId(ApplicationContextHelper.getNextIdentity());
                                                orderItem.setItemId(item.getItemId());
                                                orderItem.setOrderItemGroupId(newRelatedOrderItemGroup.getId());
                                                orderItem.setOrderId(newOrder.getId());
                                                newOrderItemList.add(orderItem);
                                            });
                                        });
                                    }
                                });
                            }else {
                                relatedOrderItemGroupList.stream().filter(relatedOrder -> Arrays.asList(StringUtils.split(relatedOrder.getExt$Item("relatedItemGroupId"), ",")).contains(orderItemGroup.getItemGroupId().toString())).findFirst().ifPresent(relatedOrder -> {
                                    boolean notExisted = curRelatedOrderItemGroupList.stream().noneMatch(existedOrder -> ObjectUtils.equals(existedOrder.getItemGroupId(), relatedOrder.getItemGroupId()));
                                    if (notExisted) {
                                        EmcOrderItemGroupBean newRelatedOrderItemGroup = new EmcOrderItemGroupBean();
                                        newRelatedOrderItemGroup.setId(ApplicationContextHelper.getNextIdentity());
                                        newRelatedOrderItemGroup.setOrderId(newOrder.getId());
                                        newRelatedOrderItemGroup.setItemGroupId(relatedOrder.getItemGroupId());
                                        newRelatedOrderItemGroup.setItemGroup(relatedOrder.getItemGroup());
                                        newRelatedOrderItemGroup.setSchemeName(relatedOrder.getSchemeName());
                                        newRelatedOrderItemGroup.setTestId(relatedOrder.getTestId());
                                        newRelatedOrderItemGroup.setMethodId(relatedOrder.getMethodId());
                                        newRelatedOrderItemGroup.setTestMethodId(relatedOrder.getTestMethodId());
                                        newRelatedOrderItemGroup.setMonitorTmplId(newOrder.getMonitorTmplId());
                                        newRelatedOrderItemGroup.setOrderCategory(newOrder.getOrderCategory());
                                        newRelatedOrderItemGroup.setAnalysisOrgId(relatedOrder.getAnalysisOrgId());
                                        newRelatedOrderItemGroup.setAnalysisOrgName(relatedOrder.getAnalysisOrgName());
                                        newRelatedOrderItemGroup.setTestName(relatedOrder.getTestName());
                                        newRelatedOrderItemGroup.setMethodName(relatedOrder.getMethodName());
                                        newRelatedOrderItemGroup.setTestMethodBasis(relatedOrder.getTestMethodBasis());
                                        newRelatedOrderItemGroup.setMethodVersionId(relatedOrder.getMethodVersionId());
                                        newRelatedOrderItemGroup.setExt$Item("relatedTest", Constant.YES);
                                        curRelatedOrderItemGroupList.add(newRelatedOrderItemGroup);

                                        // 样品分析项信息
                                        Optional.ofNullable(itemGroupIdMap.get(newRelatedOrderItemGroup.getItemGroupId())).ifPresent(itemGroupId -> {
                                            groupItemList.stream().filter(item -> ObjectUtils.equals(itemGroupId, item.getItemGroupId())).forEach(item -> {
                                                EmcOrderItemBean orderItem = new EmcOrderItemBean();
                                                orderItem.setId(ApplicationContextHelper.getNextIdentity());
                                                orderItem.setItemId(item.getItemId());
                                                orderItem.setOrderItemGroupId(newRelatedOrderItemGroup.getId());
                                                orderItem.setOrderId(newOrder.getId());
                                                newOrderItemList.add(orderItem);
                                            });
                                        });
                                    }
                                });
                            }

                        });

                        // 添加关联的测试
                        newItemGroupList.addAll(curRelatedOrderItemGroupList);
                    }else{
                        LongStream.range(0, orderNum).forEach(index -> {
                            long curOrderFre = (index / preFreOrderNum) + 1;
                            EmcOrderBean newOrder = createOrder(newProject, newFolder, folderNameList, isIdentical, opportunityOrder, folderIndex, monitorTmpl, index + 1, curOrderFre);
                            newOrderList.add(newOrder);

                            List<EmcOrderItemGroupBean> curRelatedOrderItemGroupList = new ArrayList<>();
                            curTaskList.forEach(task -> {
                                EmcOrderItemGroupBean orderItemGroup = createOrderItemGroup(newOrder, task, monitorTmpl, capacityItemGroupIdMap, testMethodMap, itemGroupCapacityIdMap, capacityMap,beforeSampleSubPackageNo);
                                // 容器信息
                                testMethodOrderCategoryList.stream().filter(tmoc -> ObjectUtils.equals(orderItemGroup.getTestMethodId(), tmoc.getTestMethodId())
                                        && StringUtils.equals(orderItemGroup.getMonitorTmplId(), tmoc.getMonitorTmplId())).findFirst().ifPresent(tmoc -> {
                                    orderItemGroup.setContainerCategoryId(tmoc.getContainerCategoryId());
                                    orderItemGroup.setContainerCategoryCode(tmoc.getExt$Item("containercategorycode"));
                                    orderItemGroup.setContainerCategoryName(tmoc.getExt$Item("containercategoryname"));
                                    orderItemGroup.setContainerGroup(tmoc.getContainerGroup());
                                    orderItemGroup.setFixative(tmoc.getFixative());
                                });

                                newItemGroupList.add(orderItemGroup);

                                // 样品分析项信息
                                Optional.ofNullable(itemGroupIdMap.get(orderItemGroup.getItemGroupId())).ifPresent(itemGroupId -> {
                                    groupItemList.stream().filter(item -> ObjectUtils.equals(itemGroupId, item.getItemGroupId())).forEach(item -> {
                                        EmcOrderItemBean orderItem = new EmcOrderItemBean();
                                        orderItem.setId(ApplicationContextHelper.getNextIdentity());
                                        orderItem.setItemId(item.getItemId());
                                        orderItem.setOrderItemGroupId(orderItemGroup.getId());
                                        orderItem.setOrderId(newOrder.getId());
                                        newOrderItemList.add(orderItem);
                                    });
                                });

                                if(StringUtils.equals(newOrder.getOrderCategory(),PfConstant.SOLID_WASTE_WORD)) {
                                    // 添加关联测试，如果一个样品下有多个相同的关联测试，只添加一个
                                    List<EmcOrderItemGroupBean> relatedOrderItemGroupMatchTList =relatedOrderItemGroupList.stream().filter(relatedOrder ->relatedOrder.getTestId()!=275).collect(Collectors.toList());
                                    relatedOrderItemGroupMatchTList.stream().filter(relatedOrder -> Arrays.asList(StringUtils.split(relatedOrder.getExt$Item("relatedItemGroupId"), ",")).contains(orderItemGroup.getItemGroupId().toString())).findFirst().ifPresent(relatedOrder -> {
                                        boolean notExisted = curRelatedOrderItemGroupList.stream().noneMatch(existedOrder -> ObjectUtils.equals(existedOrder.getItemGroupId(), relatedOrder.getItemGroupId()));
                                        if (notExisted) {
                                            EmcOrderItemGroupBean newRelatedOrderItemGroup = new EmcOrderItemGroupBean();
                                            newRelatedOrderItemGroup.setId(ApplicationContextHelper.getNextIdentity());
                                            newRelatedOrderItemGroup.setOrderId(newOrder.getId());
                                            newRelatedOrderItemGroup.setItemGroupId(relatedOrder.getItemGroupId());
                                            newRelatedOrderItemGroup.setItemGroup(relatedOrder.getItemGroup());
                                            newRelatedOrderItemGroup.setSchemeName(relatedOrder.getSchemeName());
                                            newRelatedOrderItemGroup.setTestId(relatedOrder.getTestId());
                                            newRelatedOrderItemGroup.setMethodId(relatedOrder.getMethodId());
                                            newRelatedOrderItemGroup.setTestMethodId(relatedOrder.getTestMethodId());
                                            newRelatedOrderItemGroup.setMonitorTmplId(newOrder.getMonitorTmplId());
                                            newRelatedOrderItemGroup.setOrderCategory(newOrder.getOrderCategory());
                                            newRelatedOrderItemGroup.setAnalysisOrgId(relatedOrder.getAnalysisOrgId());
                                            newRelatedOrderItemGroup.setAnalysisOrgName(relatedOrder.getAnalysisOrgName());
                                            newRelatedOrderItemGroup.setTestName(relatedOrder.getTestName());
                                            newRelatedOrderItemGroup.setMethodName(relatedOrder.getMethodName());
                                            newRelatedOrderItemGroup.setTestMethodBasis(relatedOrder.getTestMethodBasis());
                                            newRelatedOrderItemGroup.setMethodVersionId(relatedOrder.getMethodVersionId());
                                            newRelatedOrderItemGroup.setExt$Item("relatedTest", Constant.YES);
                                            curRelatedOrderItemGroupList.add(newRelatedOrderItemGroup);

                                            // 样品分析项信息
                                            Optional.ofNullable(itemGroupIdMap.get(newRelatedOrderItemGroup.getItemGroupId())).ifPresent(itemGroupId -> {
                                                groupItemList.stream().filter(item -> ObjectUtils.equals(itemGroupId, item.getItemGroupId())).forEach(item -> {
                                                    EmcOrderItemBean orderItem = new EmcOrderItemBean();
                                                    orderItem.setId(ApplicationContextHelper.getNextIdentity());
                                                    orderItem.setItemId(item.getItemId());
                                                    orderItem.setOrderItemGroupId(newRelatedOrderItemGroup.getId());
                                                    orderItem.setOrderId(newOrder.getId());
                                                    newOrderItemList.add(orderItem);
                                                });
                                            });
                                        }
                                    });
                                }else{
                                    // 添加关联测试，如果一个样品下有多个相同的关联测试，只添加一个
                                    relatedOrderItemGroupList.stream().filter(relatedOrder -> Arrays.asList(StringUtils.split(relatedOrder.getExt$Item("relatedItemGroupId"), ",")).contains(orderItemGroup.getItemGroupId().toString())).findFirst().ifPresent(relatedOrder -> {
                                        boolean notExisted = curRelatedOrderItemGroupList.stream().noneMatch(existedOrder -> ObjectUtils.equals(existedOrder.getItemGroupId(), relatedOrder.getItemGroupId()));
                                        if (notExisted) {
                                            EmcOrderItemGroupBean newRelatedOrderItemGroup = new EmcOrderItemGroupBean();
                                            newRelatedOrderItemGroup.setId(ApplicationContextHelper.getNextIdentity());
                                            newRelatedOrderItemGroup.setOrderId(newOrder.getId());
                                            newRelatedOrderItemGroup.setItemGroupId(relatedOrder.getItemGroupId());
                                            newRelatedOrderItemGroup.setItemGroup(relatedOrder.getItemGroup());
                                            newRelatedOrderItemGroup.setSchemeName(relatedOrder.getSchemeName());
                                            newRelatedOrderItemGroup.setTestId(relatedOrder.getTestId());
                                            newRelatedOrderItemGroup.setMethodId(relatedOrder.getMethodId());
                                            newRelatedOrderItemGroup.setTestMethodId(relatedOrder.getTestMethodId());
                                            newRelatedOrderItemGroup.setMonitorTmplId(newOrder.getMonitorTmplId());
                                            newRelatedOrderItemGroup.setOrderCategory(newOrder.getOrderCategory());
                                            newRelatedOrderItemGroup.setAnalysisOrgId(relatedOrder.getAnalysisOrgId());
                                            newRelatedOrderItemGroup.setAnalysisOrgName(relatedOrder.getAnalysisOrgName());
                                            newRelatedOrderItemGroup.setTestName(relatedOrder.getTestName());
                                            newRelatedOrderItemGroup.setMethodName(relatedOrder.getMethodName());
                                            newRelatedOrderItemGroup.setTestMethodBasis(relatedOrder.getTestMethodBasis());
                                            newRelatedOrderItemGroup.setMethodVersionId(relatedOrder.getMethodVersionId());
                                            newRelatedOrderItemGroup.setExt$Item("relatedTest", Constant.YES);
                                            curRelatedOrderItemGroupList.add(newRelatedOrderItemGroup);

                                            // 样品分析项信息
                                            Optional.ofNullable(itemGroupIdMap.get(newRelatedOrderItemGroup.getItemGroupId())).ifPresent(itemGroupId -> {
                                                groupItemList.stream().filter(item -> ObjectUtils.equals(itemGroupId, item.getItemGroupId())).forEach(item -> {
                                                    EmcOrderItemBean orderItem = new EmcOrderItemBean();
                                                    orderItem.setId(ApplicationContextHelper.getNextIdentity());
                                                    orderItem.setItemId(item.getItemId());
                                                    orderItem.setOrderItemGroupId(newRelatedOrderItemGroup.getId());
                                                    orderItem.setOrderId(newOrder.getId());
                                                    newOrderItemList.add(orderItem);
                                                });
                                            });
                                        }
                                    });
                                }

                            });

                            // 添加关联的测试
                            newItemGroupList.addAll(curRelatedOrderItemGroupList);
                        });
                    }

                });
            });

            if (!newFolderList.isEmpty()) {
                folderService.getDao().insert(newFolderList);
                orderService.getDao().insert(newOrderList);
                orderItemGroupService.getDao().insert(newItemGroupList);
                orderItemService.getDao().insert(newOrderItemList);
            }
        }

        projectService.getDao().insert(newProject);
    }

    @Override
    public Page<CoreFileBean> selectFilePagination(RestJsonWrapperBean wrapper) {
        List<Map<String, Object>> mapList = getDao().selectFileByCondition(wrapper.extractMapFilter());
        List<CoreFileBean> fileList = mapList.stream().map(m -> PersistableHelper.mapToPersistable(m, CoreFileBean.class)).collect(Collectors.toList());

        String crmServerUrl = ApplicationContextHelper.getConstantValue("CRM_SERVER_URL");
        fileList.forEach(file -> {
            String fileUrl = FilePathManager.getUrl(CoreFileUtils.toFilePath(file));
            file.setDownloadUrl(crmServerUrl + fileUrl);
        });

        return new Pagination<>(fileList);
    }

    @Override
    public void syncContractFile(RestJsonWrapperBean wrapper) {
        List<CrmOpportunityContractBean> contractList = wrapper.parse(CrmOpportunityContractBean.class);
        List<String> contractIdList = contractList.stream().map(CrmOpportunityContractBean::getId).collect(Collectors.toList());
        List<String> opportunityIdList = contractList.stream().map(CrmOpportunityContractBean::getOpportunityId).distinct().collect(Collectors.toList());

        List<String> targetIdList = new ArrayList<>(contractIdList.size() + opportunityIdList.size());
        targetIdList.addAll(contractIdList.stream().map(id -> "T_CRM_OPPORTUNITY_CONTRACT$" + id).collect(Collectors.toList()));
        targetIdList.addAll(opportunityIdList.stream().map(id -> "T_CRM_OPPORTUNITY_MAIN$" + id).collect(Collectors.toList()));

        List<CoreFileBean> existedFileList = coreFileService.getDao().selectListByOneColumnValues(targetIdList, "targetId", Arrays.asList("id", "targetId", "md5Name"));
        List<String> existedMd5List = existedFileList.stream().map(CoreFileBean::getMd5Name).collect(Collectors.toList());

        // 查询CRM附件视图
        MapperParameter parameter = new MapperParameter();
        parameter.setFilter(SearchFilter.instance().match("targetId", targetIdList).filter(MatchPattern.OR));
        List<Map<String, Object>> mapList = getDao().selectFileByCondition(parameter);
        List<CoreFileBean> crmFileList = mapList.stream().map(m -> PersistableHelper.mapToPersistable(m, CoreFileBean.class)).collect(Collectors.toList());

        // 未同步的文件
        List<CoreFileBean> notSyncedFileList = crmFileList.stream().filter(file -> !existedMd5List.contains(file.getMd5Name())).collect(Collectors.toList());
        opportunityOpenService.downloadAndSaveFile(notSyncedFileList);
    }

    private void convertFolderNo(Map<String, String> maxSeqNoMap, String orderCategoryCode, List<String> folderNoList, int folderNum) {
        String curMaxSeqNo = maxSeqNoMap.get(orderCategoryCode);
        String maxFolderNo = folderNoList.stream().max(Comparator.comparing(Function.identity())).orElse(null);
        if (curMaxSeqNo == null && !StringUtils.isEmpty(maxFolderNo)) {
            maxSeqNoMap.put(orderCategoryCode, maxFolderNo.substring(maxFolderNo.length() - 4));
        } else if (curMaxSeqNo != null && !StringUtils.isEmpty(maxFolderNo)) {
            String prefix = maxFolderNo.substring(0, maxFolderNo.length() - 4);
            folderNoList.clear();

            List<String> newSeqNoList = EmcNumberUtils.nextSeqNoList(curMaxSeqNo, folderNum);
            folderNoList.addAll(newSeqNoList.stream().map(seqNo -> prefix + seqNo).collect(Collectors.toList()));

            maxSeqNoMap.put(orderCategoryCode, newSeqNoList.get(newSeqNoList.size() - 1));
        }
    }

    private EmcProjectBean createProject(CrmOpportunityMainBean opportunityMain, Map<String, CrmOpportunityContractBean> contractMap, Map<String, Integer> projectSeqNoMap, EmcBizCategoryBean bizCategory) {
        EmcProjectBean newProject = new EmcProjectBean();
        newProject.setId(ApplicationContextHelper.getNextIdentity());
        newProject.setOrgId(LocalContextHelper.getLoginUser().getExt$Item("parentOrgId"));
        newProject.setProjName(opportunityMain.getProjectName());
        newProject.setReportDeliveryWay(opportunityMain.getGetReport());
        LocalDate localDateNow = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String localDateNowStr = localDateNow.format(formatter);
        Optional.ofNullable(contractMap.get(opportunityMain.getId())).ifPresent(contract -> {
            int nextSeqNoValue = Optional.ofNullable(projectSeqNoMap.get(contract.getContractNo())).orElse(1);
            String nextSeqNo = StringUtils.leftPad(nextSeqNoValue + "", 2);
            newProject.setProjNo(contract.getContractNo() + nextSeqNo + Optional.ofNullable(bizCategory.getBizCategoryId()).orElse("O"));
            LocalDate commencementDate = contract.getContractCommencementDate();
            if (commencementDate != null) {
                newProject.setRegistDate(commencementDate.atStartOfDay());
                newProject.setAcceptDate(commencementDate.atStartOfDay());
            }

            LocalDate contractEndDate = contract.getContractEndDate();
            if (contractEndDate != null) {
                newProject.setCompleDate(contractEndDate.atStartOfDay());
            }
            newProject.setExt$Item("contractNo", contract.getContractNo());
        });

        newProject.setExt$Item("bidderId", opportunityMain.getBidder());
        newProject.setExt$Item("bidderName", opportunityMain.getBidderName());
        newProject.setExt$Item("sampleSource", opportunityMain.getSampleSource());
        newProject.setExt$Item("customerName", opportunityMain.getCustomerName());
        newProject.setExt$Item("customerContact", opportunityMain.getCustomerContact());
        newProject.setExt$Item("customerPhone", opportunityMain.getCustomerPhone());
        newProject.setExt$Item("inspectedContact", opportunityMain.getInspectedContcts());
        newProject.setExt$Item("inspectedPhone", opportunityMain.getInspectedPhone());
        newProject.setExt$Item("samplingLocation", opportunityMain.getDrawArea());
        newProject.setExt$Item("projcreatedtime",localDateNowStr);
        newProject.setExt$Item("monitoringCategory",opportunityMain.getMonitoringCategory());
        newProject.setSample(opportunityMain.getSampleSource());
        newProject.setBizCategoryId(bizCategory.getId());
        newProject.setBizCategoryCode(bizCategory.getBizCategoryId());
        newProject.setEntrustOrgName(opportunityMain.getAuthorizationUnit());
        newProject.setEntrustOrgAddress(opportunityMain.getEntrustededUnitAdd());
        newProject.setEntrustOrgContact(opportunityMain.getCustomerContact());
        newProject.setEntrustOrgPhone(opportunityMain.getCustomerPhone());
        newProject.setTestOrgAddress(opportunityMain.getInspectedAddr());
        newProject.setTestOrgContact(opportunityMain.getInspectedContcts());
        newProject.setTestOrgName(opportunityMain.getInspectedUnit());
        newProject.setTestOrgPhone(opportunityMain.getInspectedPhone());
        newProject.setUrgent(Optional.ofNullable(opportunityMain.getUrgentFlag()).orElse(Constant.NO));
        newProject.setRemark(opportunityMain.getRemark());

        return newProject;
    }

    private EmcProjOrderBean createProjOrder(EmcProjSchemeBean newScheme, CrmOpportunityOrdtaskBean task, EmcTestMethodBean testMethod, CrmOpportunityOrdersBean order, String subPackageNo) {
        EmcProjOrderBean newOrder = new EmcProjOrderBean();
        newOrder.setId(ApplicationContextHelper.getNextIdentity());
        newOrder.setSchemeName(task.getSchemeName());
        newOrder.setProjSchemeId(newScheme.getId());
        newOrder.setTestId((testMethod != null) ? testMethod.getTestId() : NumberUtils.parseLong(task.getTestCode()));
        newOrder.setItemGroupId(NumberUtils.parseLong(task.getTestGrouporgId()));
        newOrder.setItemGroup(task.getItemGroup());
        newOrder.setSampleNum(Optional.ofNullable(task.getSamplePoints()).orElse(1L));
        newOrder.setExt$Item("remark",task.getRemark());
        // 采样前分包
        if (Constant.YES.equals(task.getSubcontract2())) {
            newOrder.setSubcontractorId(ApplicationContextHelper.getNextIdentity());
            newOrder.setSubPackageNo(subPackageNo);
            newOrder.setExt$Item("subcontract1", Constant.YES);
        }

        // 采样后分包
        if (Constant.YES.equals(task.getSubcontract())) {
            newOrder.setExt$Item("subcontract2", Constant.YES);
        }

        Long methodId = (testMethod != null) ? testMethod.getMethodId() : (NumberUtils.isNumber(task.getMethod()) ? NumberUtils.parseLong(task.getMethod()) : null);
        newOrder.setMethodId(methodId);

        if (NumberUtils.isNumber(task.getIndexCategoryId())) {
            newOrder.setIndexCategoryId(NumberUtils.parseLong(task.getIndexCategoryId()));
            newOrder.setIndexId(task.getIndexId());
        }

        // 对于年度订单，同一点位，存在多个周期频次的，直接把实际的周期频次存于测试之上
        newOrder.setExt$Item("realFrequency", order.getTestingFrequency() + "");
        newOrder.setExt$Item("realCycle", order.getTestingDays() + "");

        return newOrder;
    }

    private Map<String, List<EmcProjOrderBean>> createRelatedProjOrder(List<CrmOpportunityOrdtaskBean> selectedOrderTaskList) {
        // 按点位进行分组
        Map<String, List<CrmOpportunityOrdtaskBean>> orderTaskMap = selectedOrderTaskList.stream().collect(Collectors.groupingBy(CrmOpportunityOrdtaskBean::getOrdersId));
        Map<String, List<EmcProjOrderBean>> relatedProjOrderMap = new HashMap<>(orderTaskMap.size());

        List<Long> capacityItemGroupIdList = selectedOrderTaskList.stream().map(CrmOpportunityOrdtaskBean::getTestGrouporgId).distinct().filter(NumberUtils::isNumber).mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());
        List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.selectListByIds(capacityItemGroupIdList);

        List<Long> testMethodIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getTestMethodId).distinct().collect(Collectors.toList());
        List<EmcRelatedTestMethodBean> relatedTestMethodList = relatedTestMethodService.selectListByFilter(SearchFilter.instance().match("testMethodId", testMethodIdList).filter(MatchPattern.OR).match("replaceFlag2", Arrays.asList("1", null)).filter(MatchPattern.DIFFER), new Order[0]);
        Map<Long, List<EmcRelatedTestMethodBean>> relatedTestMethodMap = relatedTestMethodList.stream().collect(Collectors.groupingBy(EmcRelatedTestMethodBean::getRelatedTestMethodId));
        List<Long> relatedTestMethodIdList = relatedTestMethodList.stream().map(EmcRelatedTestMethodBean::getRelatedTestMethodId).distinct().collect(Collectors.toList());
        List<EmcTestMethodBean> testMethodList = testMethodService.selectListByIds(relatedTestMethodIdList);
        //关联的测试id
        List<Long> relateTestMethodIdList = testMethodList.stream().map(EmcTestMethodBean :: getId).collect(Collectors.toList());

        List<EmcTestCapacityBean> relatedTestCapacityBeforeList = testCapacityService.getDao().selectListByOneColumnValues(relateTestMethodIdList,"testMethodId");
        List<EmcTestCapacityBean> relatedTestCapacityList = relatedTestCapacityBeforeList.stream().filter(tc ->StringUtils.equals(LocalContextHelper.getLoginUser().getExt$Item("parentOrgId"), tc.getOrgId())).collect(Collectors.toList());
        List<Long> relatedTestCapacityIdList = relatedTestCapacityList.stream().map(EmcTestCapacityBean ::getId).collect(Collectors.toList());
        //对应的监测能力库分析项组
        List<EmcCapacityItemGroupBean> relatedItemGroupBeforeList = capacityItemGroupService.getDao().selectListByOneColumnValues(relatedTestMethodIdList, "testMethodId");
        List<EmcCapacityItemGroupBean> relatedItemGroupList = relatedItemGroupBeforeList.stream().filter(tcg -> relatedTestCapacityIdList.contains(tcg.getTestCapacityId())).collect(Collectors.toList());

        orderTaskMap.forEach((ordersId, curOrdtaskList) -> {
            //按点位分组，这里的样品类型就是一种
            String needOrderCategory = curOrdtaskList.get(0).getExt$Item("testtype");
            List<Long> curCapacityItemGroupIdList = curOrdtaskList.stream().map(CrmOpportunityOrdtaskBean::getTestGrouporgId).distinct().filter(NumberUtils::isNumber).mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());
            List<EmcProjOrderBean> relatedProjOrderList = new ArrayList<>();
            //关联的测试的监测能力库分析项组
            //如果needOrderCategory是固废则筛选出测试为含水率的
            if(needOrderCategory.contains(PfConstant.SOLID_WASTE_WORD)){
                relatedItemGroupList.stream().filter(cig ->cig.getTestId()!=275).filter(related -> !curCapacityItemGroupIdList.contains(related.getId())).forEach(related -> {
                    EmcProjOrderBean relatedProjOrder = new EmcProjOrderBean();
                    relatedProjOrder.setItemGroup(related.getItemGroup());
                    relatedProjOrder.setItemGroupId(related.getId());
                    relatedProjOrder.setSchemeName("default");
                    testMethodList.stream().filter(testMethod -> ObjectUtils.equals(related.getTestMethodId(), testMethod.getId())).findFirst().ifPresent(testMethod -> {
                        relatedProjOrder.setMethodId(testMethod.getMethodId());
                        relatedProjOrder.setTestId(testMethod.getTestId());
                    });
                    Optional.ofNullable(relatedTestMethodMap.get(related.getTestMethodId())).ifPresent(methodList -> {
                        List<Long> curTestMethodIdList = methodList.stream().map(EmcRelatedTestMethodBean::getTestMethodId).distinct().collect(Collectors.toList());
                        String concatItemGroupIdStr = capacityItemGroupList.stream().filter(group -> curTestMethodIdList.contains(group.getTestMethodId())).map(EmcCapacityItemGroupBean::getId).map(String::valueOf).collect(Collectors.joining(","));
                        relatedProjOrder.setExt$Item("relatedItemGroupId", concatItemGroupIdStr);
                    });

                    relatedProjOrderList.add(relatedProjOrder);
                });
            }else{
                relatedItemGroupList.stream().filter(related -> !curCapacityItemGroupIdList.contains(related.getId())).forEach(related -> {
                    EmcProjOrderBean relatedProjOrder = new EmcProjOrderBean();
                    relatedProjOrder.setItemGroup(related.getItemGroup());
                    relatedProjOrder.setItemGroupId(related.getId());
                    relatedProjOrder.setSchemeName("default");
                    testMethodList.stream().filter(testMethod -> ObjectUtils.equals(related.getTestMethodId(), testMethod.getId())).findFirst().ifPresent(testMethod -> {
                        relatedProjOrder.setMethodId(testMethod.getMethodId());
                        relatedProjOrder.setTestId(testMethod.getTestId());
                    });
                    Optional.ofNullable(relatedTestMethodMap.get(related.getTestMethodId())).ifPresent(methodList -> {
                        List<Long> curTestMethodIdList = methodList.stream().map(EmcRelatedTestMethodBean::getTestMethodId).distinct().collect(Collectors.toList());
                        String concatItemGroupIdStr = capacityItemGroupList.stream().filter(group -> curTestMethodIdList.contains(group.getTestMethodId())).map(EmcCapacityItemGroupBean::getId).map(String::valueOf).collect(Collectors.joining(","));
                        relatedProjOrder.setExt$Item("relatedItemGroupId", concatItemGroupIdStr);
                    });

                    relatedProjOrderList.add(relatedProjOrder);
                });
            }


            relatedProjOrderMap.put(ordersId, relatedProjOrderList);
        });

        return relatedProjOrderMap;
    }

    private List<EmcOrderItemGroupBean> createRelatedOrderItemGroup(List<CrmOpportunityOrdtaskBean> selectedOrderTaskList) {
        List<EmcOrderItemGroupBean> relatedOrderItemGroupList = new ArrayList<>();
        List<Long> capacityItemGroupIdList = selectedOrderTaskList.stream().map(CrmOpportunityOrdtaskBean::getTestGrouporgId).distinct().filter(NumberUtils::isNumber).mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());
        List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.selectListByIds(capacityItemGroupIdList);

        List<Long> testMethodIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getTestMethodId).distinct().collect(Collectors.toList());
        List<EmcRelatedTestMethodBean> relatedTestMethodList = relatedTestMethodService.selectListByFilter(SearchFilter.instance().match("testMethodId", testMethodIdList).filter(MatchPattern.OR).match("replaceFlag2", Arrays.asList("1", null)).filter(MatchPattern.DIFFER), new Order[0]);
        if (relatedTestMethodList.isEmpty()) {
            return relatedOrderItemGroupList;
        }

        Map<Long, List<EmcRelatedTestMethodBean>> relatedTestMethodMap = relatedTestMethodList.stream().collect(Collectors.groupingBy(EmcRelatedTestMethodBean::getRelatedTestMethodId));
        List<Long> relatedTestMethodIdList = relatedTestMethodList.stream().map(EmcRelatedTestMethodBean::getRelatedTestMethodId).distinct().collect(Collectors.toList());
        List<EmcTestMethodBean> testMethodList = relatedTestMethodIdList.isEmpty() ? Collections.emptyList() : testMethodService.selectListByFilter(SearchFilter.instance().match("id", relatedTestMethodIdList).filter(MatchPattern.OR));

        // 根据方法和orgId查询监测能力方法
        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
        List<EmcTestCapacityBean> relatedCapacityList = testCapacityService.selectListByFilter(SearchFilter.instance().match("testMethodId", relatedTestMethodIdList).filter(MatchPattern.OR).match("orgId", parentOrgId).filter(MatchPattern.SEQ));
        List<Long> testCapacityIdList = relatedCapacityList.stream().map(EmcTestCapacityBean::getId).collect(Collectors.toList());
        Map<Long, EmcTestCapacityBean> relatedCapacityMap = relatedCapacityList.stream().collect(Collectors.toMap(EmcTestCapacityBean::getId, Function.identity()));
        List<EmcCapacityItemGroupBean> relatedItemGroupList = capacityItemGroupService.getDao().selectListByOneColumnValues(testCapacityIdList, "testCapacityId");

        relatedItemGroupList.stream().filter(related -> !capacityItemGroupIdList.contains(related.getId())).forEach(related -> {
            EmcOrderItemGroupBean relatedOrderItemGroup = new EmcOrderItemGroupBean();
            relatedOrderItemGroup.setItemGroup(related.getItemGroup());
            relatedOrderItemGroup.setItemGroupId(related.getId());
            relatedOrderItemGroup.setSchemeName("default");
            testMethodList.stream().filter(testMethod -> ObjectUtils.equals(related.getTestMethodId(), testMethod.getId())).findFirst().ifPresent(testMethod -> {
                relatedOrderItemGroup.setMethodId(testMethod.getMethodId());
                relatedOrderItemGroup.setTestId(testMethod.getTestId());
                relatedOrderItemGroup.setTestMethodId(testMethod.getId());
                relatedOrderItemGroup.setTestName(testMethod.getExt$Item("testName"));
                relatedOrderItemGroup.setMethodName(testMethod.getExt$Item("methodName"));
                relatedOrderItemGroup.setTestMethodBasis(testMethod.getExt$Item("testMethodBasis"));
                String methodVersionId = testMethod.getExt$Item("methodVersionId");
                if (NumberUtils.isNumber(methodVersionId)) {
                    relatedOrderItemGroup.setMethodVersionId(NumberUtils.parseLong(methodVersionId));
                }
            });
            Optional.ofNullable(relatedTestMethodMap.get(related.getTestMethodId())).ifPresent(methodList -> {
                List<Long> curTestMethodIdList = methodList.stream().map(EmcRelatedTestMethodBean::getTestMethodId).distinct().collect(Collectors.toList());
                String concatItemGroupIdStr = capacityItemGroupList.stream().filter(group -> curTestMethodIdList.contains(group.getTestMethodId())).map(EmcCapacityItemGroupBean::getId).map(String::valueOf).collect(Collectors.joining(","));
                relatedOrderItemGroup.setExt$Item("relatedItemGroupId", concatItemGroupIdStr);
            });

            Optional.ofNullable(relatedCapacityMap.get(related.getTestCapacityId())).ifPresent(capacity -> {
                relatedOrderItemGroup.setAnalysisOrgId(capacity.getExt$Item("analysisOrgId"));
                relatedOrderItemGroup.setAnalysisOrgName(capacity.getExt$Item("analysisOrgName"));
            });

            relatedOrderItemGroupList.add(relatedOrderItemGroup);
        });

        return relatedOrderItemGroupList;
    }

    private EmcFolderBean createFolder(EmcProjectBean newProject, List<String> folderNoList, int folderIndex, EmcMonitorTmplBean monitorTmpl) {
        EmcFolderBean newFolder = new EmcFolderBean();
        newFolder.setId(ApplicationContextHelper.getNextIdentity());
        newFolder.setProjId(newProject.getId());
        newFolder.setFolderNo(folderNoList.get(folderIndex));
        newFolder.setMonitorTmplId(monitorTmpl.getId());
        newFolder.setOrderCategory(monitorTmpl.getMonitorTmplName());
        newFolder.setSource(EmcBizConstant.BP_SOURCE_ORDERREGISTRATION);
        newFolder.setTermination(Constant.NO);

        return newFolder;
    }

    private EmcOrderBean createOrder(EmcProjectBean newProject, EmcFolderBean newFolder, List<String> folderNameList, boolean isIdentical,
                                     CrmOpportunityOrdersBean opportunityOrder, int folderIndex, EmcMonitorTmplBean monitorTmpl, long seqNo, long orderFre) {

        EmcOrderBean newOrder = new EmcOrderBean();
        newOrder.setId(ApplicationContextHelper.getNextIdentity());
        newOrder.setProjId(newProject.getId());
        newOrder.setFolderId(newFolder.getId());
        newOrder.setMonitorTmplId(monitorTmpl.getId());
        newOrder.setOrderCategory(monitorTmpl.getMonitorTmplName());
        newOrder.setOrderNo(newFolder.getFolderNo() + StringUtils.leftPad(seqNo + "", 2));
        newOrder.setLabelNo(newOrder.getOrderNo());
        newOrder.setClientOrderName(isIdentical ? folderNameList.get(folderIndex) : opportunityOrder.getPotName() + "-" + (folderIndex + 1));
        newOrder.setSendDate(LocalDate.now());
        newOrder.setSendTime(DateTimeUtils.formatLocalDateTime(LocalDateTime.now(), "HH:mm"));
        newOrder.setOrderNum(EmcConstant.ONE);
        newOrder.setCycle(EmcConstant.ONE);
        newOrder.setFrequency(orderFre);
        newOrder.setOrderNum(EmcConstant.ONE);
        newOrder.setTermination(Constant.NO);

        return newOrder;
    }

    private EmcOrderBean createSingleOrder(EmcProjectBean newProject, EmcFolderBean newFolder, List<String> folderNameList, boolean isIdentical,
                                           CrmOpportunityOrdersBean opportunityOrder, int folderIndex, EmcMonitorTmplBean monitorTmpl, long seqNo) {

        EmcOrderBean newOrder = new EmcOrderBean();
        newOrder.setId(ApplicationContextHelper.getNextIdentity());
        newOrder.setProjId(newProject.getId());
        newOrder.setFolderId(newFolder.getId());
        newOrder.setMonitorTmplId(monitorTmpl.getId());
        newOrder.setOrderCategory(monitorTmpl.getMonitorTmplName());
        newOrder.setOrderNo(newFolder.getFolderNo() + StringUtils.leftPad(seqNo + "", 2));
        newOrder.setLabelNo(newOrder.getOrderNo());
        newOrder.setClientOrderName(isIdentical ? folderNameList.get(folderIndex) : opportunityOrder.getPotName());
        newOrder.setSendDate(LocalDate.now());
        newOrder.setSendTime(DateTimeUtils.formatLocalDateTime(LocalDateTime.now(), "HH:mm"));
        newOrder.setOrderNum(EmcConstant.ONE);
        newOrder.setCycle(EmcConstant.ONE);
        newOrder.setFrequency(EmcConstant.ONE);
        newOrder.setOrderNum(EmcConstant.ONE);
        newOrder.setTermination(Constant.NO);

        return newOrder;
    }

    private EmcOrderItemGroupBean createOrderItemGroup(EmcOrderBean newOrder, CrmOpportunityOrdtaskBean task, EmcMonitorTmplBean monitorTmpl, Map<Long, Long> capacityItemGroupIdMap,
                                                       Map<Long, EmcTestMethodBean> testMethodMap, Map<Long, Long> itemGroupCapacityIdMap, Map<Long, EmcTestCapacityBean> capacityMap,String beforeSampleSubPackageNo) {
        EmcOrderItemGroupBean newItemGroup = new EmcOrderItemGroupBean();
        newItemGroup.setId(ApplicationContextHelper.getNextIdentity());
        newItemGroup.setOrderId(newOrder.getId());
        newItemGroup.setSchemeName(task.getSchemeName());
        newItemGroup.setTestId(NumberUtils.parseLong(task.getTestCode()));
        newItemGroup.setItemGroupId(NumberUtils.parseLong(task.getTestGrouporgId()));
        newItemGroup.setItemGroup(task.getItemGroup());
        newItemGroup.setExt$Item("remark", task.getRemark());
        // 采样后分包
        if (Constant.YES.equals(task.getSubcontract())) {
            newItemGroup.setSubPackageNo(beforeSampleSubPackageNo);
            newItemGroup.setExt$Item("SUBPACKAGETYPE","orderSubpackage");
            newItemGroup.setExt$Item("SUBCONTRACT2", Constant.YES);
        }
        if (NumberUtils.isNumber(task.getMethod())) {
            newItemGroup.setMethodId(NumberUtils.parseLong(task.getMethod()));
        }
        Optional.ofNullable(testMethodMap.get(capacityItemGroupIdMap.get(newItemGroup.getItemGroupId()))).ifPresent(testMethod -> {
            newItemGroup.setTestMethodId(testMethod.getId());
            newItemGroup.setTestId(testMethod.getTestId());
            newItemGroup.setTestName(testMethod.getExt$Item("testName"));
            newItemGroup.setTestMethodBasis(testMethod.getExt$Item("testMethodBasis"));
            newItemGroup.setMethodId(testMethod.getMethodId());
            newItemGroup.setMethodName(testMethod.getExt$Item("methodName"));
            newItemGroup.setMonitorTmplId(monitorTmpl.getId());
            newItemGroup.setOrderCategory(monitorTmpl.getMonitorTmplName());
            String methodVersionId = testMethod.getExt$Item("methodVersionId");
            if (NumberUtils.isNumber(methodVersionId)) {
                newItemGroup.setMethodVersionId(NumberUtils.parseLong(methodVersionId));
            }
        });

        Optional.ofNullable(capacityMap.get(itemGroupCapacityIdMap.get(newItemGroup.getItemGroupId()))).ifPresent(capacity -> {
            newItemGroup.setAnalysisOrgId(capacity.getExt$Item("analysisOrgId"));
            newItemGroup.setAnalysisOrgName(capacity.getExt$Item("analysisOrgName"));
        });

        return newItemGroup;
    }

    /**
     * 分割点位名称
     * @param folderName
     * @return
     */
    private List<String> splitFolderName(String folderName) {
        if (StringUtils.isEmpty(folderName)) {
            return Collections.emptyList();
        }

        String parsedFolderName = folderName.replaceAll("[，|；|、|;]", ",").replaceAll("[：|:|_|\\-|~|\\|]", "/");
        String[] folderNames = StringUtils.split(parsedFolderName, ",");
        List<String> folderNameList = new ArrayList<>();
        Arrays.stream(folderNames).forEach(name -> {
            if (!StringUtils.contains(name, "/")) {
                folderNameList.add(name);
                return;
            }

            String namePrefix = name.substring(0, name.indexOf("/")).replaceAll("[0-9]", "");
            String replacedName = name.replaceAll("[a-zA-Z]", "");
            String[] startEndNums = StringUtils.split(replacedName, "/");

            boolean invalidName = startEndNums.length < 2 || !NumberUtils.isNumber(startEndNums[0]) || !NumberUtils.isNumber(startEndNums[1])
                    || StringUtils.contains(startEndNums[0], ".") || StringUtils.contains(startEndNums[1], ".");

            if (invalidName) {
                folderNameList.add(name);
                return;
            }

            long startNum = NumberUtils.parseLong(startEndNums[0]);
            long endNum = NumberUtils.parseLong(startEndNums[1]);
            if ((endNum - startNum) > MAX_FOLDER_SIZE_PER_TIME) {
                folderNameList.add(name);
                return;
            }

            LongStream.range(startNum, endNum + 1L).forEach(num -> folderNameList.add(namePrefix + num));
        });

        return folderNameList;
    }

    /**
     * 计算项目开案金额
     * @param opportunityMain
     * @param createdProjectList
     * @param allOpportunityOrderList
     * @param opportunityOrderList
     * @return
     */
    private String calcProjectAmount(CrmOpportunityMainBean opportunityMain, List<EmcProjectBean> createdProjectList, List<CrmOpportunityOrdersBean> allOpportunityOrderList, List<CrmOpportunityOrdersBean> opportunityOrderList) {
        // 成交金额即合同金额
        Double amountFee = opportunityMain.getDealAmount();
        Double testingFee = opportunityMain.getTestingFee();

        // 费用合计
        Double totalFee = opportunityMain.getAmountFee();
        double totalNum = allOpportunityOrderList.stream().mapToDouble(CrmOpportunityOrdersBean::getPotNum).sum();
        double avgFee = (totalFee - testingFee) / totalNum;

        double currentNum = opportunityOrderList.stream().mapToDouble(CrmOpportunityOrdersBean::getPotNum).sum();
        double currentTotalFee = avgFee * currentNum;

        AtomicDouble projectAmount = new AtomicDouble(currentTotalFee);
        opportunityOrderList.stream().filter(order -> NumberUtils.isNumber(order.getProjectSubtotal())).forEach(order -> {
            String projectSubtotal = order.getProjectSubtotal();
            double orderFee = NumberUtils.parseDouble(projectSubtotal) * order.getPotNum() * order.getTestingDays() * order.getTestingFrequency();
            projectAmount.addAndGet(orderFee);
        });
        double projectAmountValue = projectAmount.doubleValue();

        // 已经开案的金额总和
        double createdTotalFee = createdProjectList.stream().filter(project -> NumberUtils.isNumber(project.getExt$Item("projectAmount"))).mapToDouble(project -> NumberUtils.parseDouble(project.getExt$Item("projectAmount"))).sum();
        if (createdTotalFee >= amountFee) {
            return "0.00";
        }

        if ((projectAmountValue + createdTotalFee) > amountFee) {
            return String.format("%.2f", amountFee - createdTotalFee);
        }

        return String.format("%.2f", Math.max(0, projectAmountValue));
    }

    /**
     * 解析关联的点位
     * @param opportunityOrderList
     * @return
     */
    private Map<String, List<CrmOpportunityOrdersBean>> parseAssociatedPoint(List<CrmOpportunityOrdersBean> opportunityOrderList) {
        List<CrmOpportunityOrdersBean> associatedOrderList = opportunityOrderList.stream().filter(opportunityOrder -> !StringUtils.isEmpty(opportunityOrder.getAssociatedPoint())).collect(Collectors.toList());
        if (associatedOrderList.isEmpty()) {
            Map<String, List<CrmOpportunityOrdersBean>> associatedOrderMap = new LinkedHashMap<>();
            opportunityOrderList.forEach(order -> associatedOrderMap.put(order.getId(), Collections.singletonList(order)));

            return associatedOrderMap;
        }

        List<String> associatedPointList = associatedOrderList.stream().map(CrmOpportunityOrdersBean::getAssociatedPoint).distinct().collect(Collectors.toList());

        List<CrmOpportunityOrdersBean> mainOrderList = opportunityOrderList.stream().filter(opportunityOrder -> associatedPointList.contains(opportunityOrder.getId())).sorted(Comparator.comparing(CrmOpportunityOrdersBean::getSorter)).collect(Collectors.toList());
        List<String> mainOrderIdList = mainOrderList.stream().map(CrmOpportunityOrdersBean::getId).collect(Collectors.toList());
        List<CrmOpportunityOrdersBean> actualAssociatedOrderList = associatedOrderList.stream().filter(order -> mainOrderIdList.contains(order.getAssociatedPoint())).collect(Collectors.toList());

        Map<String, List<CrmOpportunityOrdersBean>> associatedOrderMap = new LinkedHashMap<>();
        mainOrderList.forEach(opportunityOrder -> {
            List<CrmOpportunityOrdersBean> curAssociatedOrderList = new ArrayList<>();
            curAssociatedOrderList.add(opportunityOrder);
            curAssociatedOrderList.addAll(actualAssociatedOrderList.stream().filter(order -> StringUtils.equals(order.getAssociatedPoint(), opportunityOrder.getId())).collect(Collectors.toList()));
            associatedOrderMap.put(opportunityOrder.getId(), curAssociatedOrderList);
        });

        List<CrmOpportunityOrdersBean> unassociatedOrderList = associatedOrderList.stream().filter(order -> !mainOrderIdList.contains(order.getAssociatedPoint())).collect(Collectors.toList());
        unassociatedOrderList.forEach(order -> associatedOrderMap.put(order.getId(), Collections.singletonList(order)));

        Set<String> associatedOrderIdSet = associatedOrderMap.keySet();
        List<CrmOpportunityOrdersBean> normalOrderList = opportunityOrderList.stream().filter(opportunityOrder -> StringUtils.isEmpty(opportunityOrder.getAssociatedPoint()) && !associatedOrderIdSet.contains(opportunityOrder.getId())).collect(Collectors.toList());
        normalOrderList.forEach(order -> associatedOrderMap.put(order.getId(), Collections.singletonList(order)));

        return associatedOrderMap;
    }
}
