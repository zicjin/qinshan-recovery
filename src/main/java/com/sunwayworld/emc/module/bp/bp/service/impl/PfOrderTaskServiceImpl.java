package com.sunwayworld.emc.module.bp.bp.service.impl;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfBizConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderContainerBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.computing.EmcOrderTaskResultComputingEngine;
import com.sunwayworld.emc.module.bp.bp.computing.async.AsyncFormulaComputingService;
import com.sunwayworld.emc.module.bp.bp.dao.PfOrderTaskDao;
import com.sunwayworld.emc.module.bp.bp.dao.EmcOrderContainerDao;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderContainerService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskLogService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.PfOrderTaskService;
import com.sunwayworld.emc.module.bp.bp.validator.PfSubpackageTaskValidator;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.project.projectlog.bean.EmcProjectEditLogBean;
import com.sunwayworld.emc.module.bp.project.projectlog.service.EmcProjectEditLogService;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjOrderService;
import com.sunwayworld.emc.module.bp.project.projscheme.service.PfProjSchemeService;
import com.sunwayworld.emc.module.bp.result.olifilm.bean.EmcOilFilmBean;
import com.sunwayworld.emc.module.bp.result.olifilm.service.EmcOilFilmService;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.result.temphum.bean.PfRunTempHumidityBean;
import com.sunwayworld.emc.module.bp.result.temphum.service.PfRunTempHumidityService;
import com.sunwayworld.emc.module.bp.samplebottle.bean.EmcSampleBottleBean;
import com.sunwayworld.emc.module.bp.samplebottle.service.EmcSampleBottleService;
import com.sunwayworld.emc.module.bp.voc.bean.PfVocWeightingBean;
import com.sunwayworld.emc.module.bp.voc.service.PfVocWeightingService;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.bean.EmcIndexTestItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.service.EmcIndexTestItemService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlMaintainBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlMaintainItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlMaintainItemService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlMaintainService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyFieldBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyDataService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyFieldService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyTmplService;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.bean.EmcSubcontractRecordBean;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.service.EmcSubcontractRecordService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcIndexCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodOrderCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodOrderCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

@Repository
@GikamBean
public  class PfOrderTaskServiceImpl extends EmcOrderTaskServiceImpl implements PfOrderTaskService {

    @Autowired
    private PfOrderTaskDao orderTaskDao;

    @Autowired
    private EmcOrderItemGroupService orderItemGroupService;

    @Autowired
    private EmcItemService itemService;

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Autowired
    private EmcOrderTaskLogService orderTaskLogService;

    @Autowired
    @Lazy
    private EmcOrderService orderService;

    @Autowired
    private EmcTestMethodOrderCategoryService testMethodOrderCategoryService;

    @Autowired
    private EmcGroupItemService groupItemService;

    @Autowired
    private EmcCapacityItemGroupService capacityItemGroupService;

    @Autowired
    private EmcOrderItemService orderItemService;

    @Autowired
    @Lazy
    private PfRunTempHumidityService pfRunTempHumidityService;

    @Autowired
    private EmcTestMethodService testMethodService;

    @Autowired
    private PfVocWeightingService vocWeightingService;

    @Lazy
    @Autowired
    private EmcStockService stockService;

    @Autowired
    @Lazy
    private EmcMtlMaintainItemService mtlMaintainItemService;

    @Autowired
    @Lazy
    private EmcMtlMaintainService mtlMaintainService;

    @Autowired
    @Lazy
    private EmcTestService testService;

    @Autowired
    private EmcOilFilmService oilFilmService;

    @Autowired
    private EmcSubcontractRecordService subcontractRecordService;

    @Autowired
    @Lazy
    private PfProjSchemeService projSchemeService;

    @Autowired
    @Lazy
    private EmcProjOrderService projOrderService;

    @Autowired
    @Lazy
    private EmcProjectEditLogService projectEditLogService;

    @Autowired
    @Lazy
    private EmcProjectService projectService;

    @Autowired
    private EmcPropertyTmplService propertyTmplService;

    @Autowired
    private EmcIndexCategoryService indexCategoryService;

    @Autowired
    private EmcPropertyDataService propertyDataService;

    @Autowired
    private EmcIndexTestItemService indexTestItemService;

    @Autowired
    private EmcPropertyFieldService propertyFieldService;

    @Autowired
    private AsyncFormulaComputingService asyncFormulaComputingService;

    @Autowired
    private EmcSampleBottleService sampleBottleService;

    @Autowired
    private EmcCapacityItemService capacityItemService;

    @Autowired
    @Lazy
    private EmcOrderContainerService orderContainerService;

    @Autowired
    @Lazy
    private EmcFolderService folderService;

    @Autowired
    @Lazy
    private EmcOrderContainerDao orderContainerDao;

    @Override
    public PfOrderTaskDao getDao() {
        return orderTaskDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertByOrder(List<EmcOrderBean> orderList) {
        List<EmcOrderTaskBean> insertOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> insertOrderTaskResultList = new ArrayList<>();

        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");

        // 查询采集编号后缀信息
        List<Long> testMethodIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean::getTestMethodId).distinct().collect(Collectors.toList());
        List<EmcTestMethodBean> testMethodList = testMethodService.getDao().selectListByOneColumnValues(testMethodIdList, "id", Arrays.asList("id", "acqNoSuffix"));
        Map<Long, String> acqNoSuffixMap = testMethodList.stream().filter(t -> !StringUtils.isEmpty(t.getAcqNoSuffix())).collect(Collectors.toMap(EmcTestMethodBean::getId, EmcTestMethodBean::getAcqNoSuffix));

        List<EmcOrderItemBean> orderItemList = orderItemService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");
        List<Long> uniqueItemIdList = orderItemList.stream().map(EmcOrderItemBean::getItemId).distinct().collect(Collectors.toList());
        List<EmcItemBean> itemList = itemService.getDao().selectListByOneColumnValues(uniqueItemIdList, "ID");

        // 将样品进行排序，保证先生成正常样品，再生成质控样品
        orderList.forEach(order -> order.setOrderNum(ObjectUtils.isEmpty(order.getQcCategoryId()) ? 0L : 1L));
        orderList.sort(Comparator.comparing(EmcOrderBean::getOrderNum));
        Map<String, Long> orderNoIdMap = orderList.stream().collect(Collectors.toMap(EmcOrderBean::getOrderNo, EmcOrderBean::getId));

        for (EmcOrderBean order : orderList) {

            // 当前样品的样品分析项组
            List<EmcOrderItemGroupBean> filterOrderItemGroupList = orderItemGroupList.stream().filter(e -> ObjectUtils.equals(e.getOrderId(), order.getId())).collect(Collectors.toList());

            // 样品分析项组按照测试、方法分组决定生成几条样品测试
            List<EmcOrderItemGroupBean> distinctTestMethodOrderItemGroup = filterOrderItemGroupList.stream().collect(Collectors
                    .collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getTestId() + "#" + o.getMethodId()))), ArrayList<EmcOrderItemGroupBean>::new));

            List<EmcOrderTaskBean> parentTaskList = StringUtils.isEmpty(order.getParentOrderNo()) ? Collections.emptyList()
                    : insertOrderTaskList.stream().filter(task -> ObjectUtils.equals(task.getOrderId(), orderNoIdMap.get(order.getParentOrderNo()))).collect(Collectors.toList());

            for (EmcOrderItemGroupBean orderItemGroup : distinctTestMethodOrderItemGroup) {

                List<EmcOrderItemGroupBean> testMethodOrderItemGroupList = filterOrderItemGroupList.stream()
                        .filter(e -> ObjectUtils.equals(orderItemGroup.getTestId(), e.getTestId()) && ObjectUtils.equals(orderItemGroup.getMethodId(), e.getMethodId())).collect(Collectors.toList());

                List<Long> testMethodOrderItemGroupIdList = testMethodOrderItemGroupList.stream().map(EmcOrderItemGroupBean::getId).collect(Collectors.toList());

                List<EmcOrderItemBean> filterOrderItemList = orderItemList.stream().filter(e -> testMethodOrderItemGroupIdList.contains(e.getOrderItemGroupId())).collect(Collectors.toList());

                List<Long> itemIdList = filterOrderItemList.stream().map(EmcOrderItemBean::getItemId).distinct().collect(Collectors.toList());

                List<EmcItemBean> filterItemList = itemList.stream().filter(e -> itemIdList.contains(e.getId()) && "1".equals(e.getActivatedFlag())).collect(Collectors.toList());

                // 判断当前样品测试分析项组下启用的分析项有几种分析类型,决定插入几条样品测试数据
                List<String> analysisCategoryList = filterItemList.stream().map(EmcItemBean::getAnalysisCategory).distinct().collect(Collectors.toList());
                EmcProjectBean project = projectService.getDao().selectById(order.getProjId());
                if ("0".equals(project.getSample())) {
                    analysisCategoryList = Arrays.asList("analysis");
                }

                for (String analysisCategory : analysisCategoryList) {

                    // 样品测试
                    EmcOrderTaskBean orderTask = new EmcOrderTaskBean();

                    BeanUtils.copyProperties(orderItemGroup, orderTask);

                    orderTask.setId(ApplicationContextHelper.getNextIdentity());
                    orderTask.setAnalysisType(analysisCategory);
                    orderTask.setFolderId(order.getFolderId());
                    orderTask.setProjId(order.getProjId());
                    orderTask.setMonitorTmplId(order.getMonitorTmplId());
                    orderTask.setOrderCategory(order.getOrderCategory());
                    orderTask.setQcCategoryId(order.getQcCategoryId());
                    orderTask.setQcCategoryCode(order.getQcCategoryCode());
                    orderTask.setQcName(order.getQcName());

                    if (!parentTaskList.isEmpty()) {
                        parentTaskList.stream().filter(task -> ObjectUtils.equals(orderTask.getTestMethodId(), task.getTestMethodId())).findFirst().ifPresent(task -> orderTask.setParentId(task.getId()));
                    }

                    boolean isSolidWaste = isSolidWaste(order);
                    String acqNoSuffix = acqNoSuffixMap.get(orderTask.getTestMethodId());
                    String acqNo = (isSolidWaste && acqNoSuffix != null) ? String.join("-", order.getOrderNo(), acqNoSuffix) : order.getOrderNo();

                    orderTask.setEquiptAcquNo(acqNo);
                    orderTask.setExt$Item("relatedTest", orderItemGroup.getExt$Item("relatedTest"));

                    insertOrderTaskList.add(orderTask);

                    // 样品测试结果
                    List<EmcItemBean> analysisCategoryItemList = filterItemList.stream().filter(e -> ObjectUtils.equals(e.getAnalysisCategory(), analysisCategory)).collect(Collectors.toList());

                    insertOrderTaskResultList.addAll(getInsertOrderTaskResultList(orderTask, analysisCategoryItemList, order));
                }

            }
        }

        // 同步子库分析项上的检出限和修约规则到结果表上
        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
        List<Long> itemIdList = insertOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
        List<EmcCapacityItemBean> capacityItemList = capacityItemService.selectListByFilter(SearchFilter.instance().match("itemId", itemIdList).filter(MatchPattern.OR).match("orgId", parentOrgId).filter(MatchPattern.SEQ));
        capacityItemList.forEach(ci -> insertOrderTaskResultList.stream().filter(r -> ObjectUtils.equals(ci.getItemId(), r.getItemId())).forEach(r -> {
            r.setOriginalResult(ci.getDefaultResult());
            r.setLowLimit(ci.getLowLimit());
            r.setRevisionRule(ci.getRevisionRule());
            r.setExt$Item("LIMITUNITNAME", ci.getExt$Item("LIMITUNITNAME"));
            r.setHighLimit(ci.getHighLimit());
        }));

        // 生成容器数据
        orderContainerService.createOrderContainer(orderList, insertOrderTaskList);

        getDao().insert(insertOrderTaskList);
        orderTaskResultService.getDao().insert(insertOrderTaskResultList);
    }

    /**
     * 编辑测试中，添加新的样品测试和结果,并设置样品测试表初始状态
     *
     * @param orderList
     * @param allOldOrderTaskList
     * @param allNewOrderItemGroupList
     * @param model
     *            数据来源于哪个模块
     * @param status
     *            样品测试表设置的初始状态
     */
    @Override
    @Transactional
    public void setDataByOrderList(List<EmcOrderBean> orderList, List<EmcOrderTaskBean> allOldOrderTaskList, List<EmcOrderItemGroupBean> allNewOrderItemGroupList, String model, String status) {

    	//监测能力库中的分析项组ID
        List<Long> capacityItemGroupIdList = allNewOrderItemGroupList.stream().map(e -> e.getItemGroupId()).collect(Collectors.toList());
        List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByIds(capacityItemGroupIdList);
        //测试管理中分析项项ID
        List<Long> itemGroupIdList = capacityItemGroupList.stream().map(e -> e.getItemGroupId()).collect(Collectors.toList());


        // 查询容器信息
        List<Long> testMethodIdList = allNewOrderItemGroupList.stream().map(e -> e.getTestMethodId()).distinct().collect(Collectors.toList());
        List<EmcTestMethodOrderCategoryBean> filterTestMethodOrderCategoryList = testMethodIdList.stream().map(testMethodId -> {
            EmcTestMethodOrderCategoryBean testMethodOrderCategory = new EmcTestMethodOrderCategoryBean();

            testMethodOrderCategory.setTestMethodId(testMethodId);
            testMethodOrderCategory.setMonitorTmplId(orderList.get(0).getMonitorTmplId());

            return testMethodOrderCategory;
        }).collect(Collectors.toList());

        List<Long> testMethodOrderCategoryIdList = testMethodOrderCategoryService.getDao()
                .selectList(filterTestMethodOrderCategoryList, Arrays.asList("TESTMETHODID", "MONITORTMPLID"), Arrays.asList("ID")).stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcTestMethodOrderCategoryBean> testMethodOrderCategoryList = testMethodOrderCategoryIdList.isEmpty() ? Collections.emptyList()
                : testMethodOrderCategoryService.selectListByFilter(SearchFilter.instance().match("ID", testMethodOrderCategoryIdList).filter(MatchPattern.OR));


        List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList,"ITEMGROUPID");

        List<EmcItemBean> itemList = itemService.getDao().selectListByOneColumnValues(groupItemList.stream().map(e -> e.getItemId()).collect(Collectors.toList()), "ID");

        List<EmcOrderTaskBean> insertOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> insertOrderTaskResultList = new ArrayList<>();

        // 判断当前编辑的样品是否有父样，对于有父样的，需要关联对应的父样样品测试ID
        List<String> parentOrderNoList = orderList.stream().filter(order -> !StringUtils.isEmpty(order.getParentOrderNo())).map(EmcOrderBean::getParentOrderNo).collect(Collectors.toList());
        List<EmcOrderTaskBean> parentOrderTaskList = Collections.emptyList();
        if (!parentOrderNoList.isEmpty()) {
            List<EmcOrderBean> parentOrderList = orderService.getDao().selectListByOneColumnValues(parentOrderNoList, "ORDERNO", Arrays.asList("ID", "ORDERNO"));
            List<Long> parentOrderIdList = parentOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
            parentOrderTaskList = getDao().selectListByOneColumnValues(parentOrderIdList, "ORDERID", Arrays.asList("ID", "ORDERID", "TESTID", "TESTMETHODID"));
        }
        List<EmcOrderTaskBean> validParentOrderTaskList = parentOrderTaskList;

        // 添加新的分析项组下的样品测试和样品测试结果
        for (EmcOrderBean o : orderList) {

            // 每一个样品下需要添加的分析项组
            List<EmcOrderTaskBean> oldOrderTaskList = allOldOrderTaskList.stream().filter(e -> ObjectUtils.equals(o.getId(), e.getOrderId())).collect(Collectors.toList());

            List<EmcOrderItemGroupBean> newOrderItemGroupList = allNewOrderItemGroupList.stream().filter(e -> ObjectUtils.equals(o.getId(), e.getOrderId())).collect(Collectors.toList());

            newOrderItemGroupList.forEach(a -> {
                EmcOrderTaskBean matchOrderItemGroup = oldOrderTaskList.stream().filter(e -> ObjectUtils.equals(a.getTestMethodId(), e.getTestMethodId())).findFirst().orElse(null);

                // 这个分析项组需要添加
                if (matchOrderItemGroup == null) {

                    // 添加样品测试容器信息
                    List<EmcTestMethodOrderCategoryBean> orderCategoryList = testMethodOrderCategoryList.stream().filter(c -> ObjectUtils.equals(a.getTestMethodId(), c.getTestMethodId()))
                            .collect(Collectors.toList());

                    if (!orderCategoryList.isEmpty()) {

                        EmcTestMethodOrderCategoryBean orderCategory = orderCategoryList.get(0);

                        a.setContainerCategoryId(orderCategory.getContainerCategoryId());
                        a.setContainerCategoryCode(orderCategory.getExt$Item("containercategorycode"));
                        a.setContainerCategoryName(orderCategory.getExt$Item("containercategoryname"));
                        a.setContainerGroup(orderCategory.getContainerGroup());
                        a.setFixative(orderCategory.getFixative());
                        a.setStoreCondition(orderCategory.getStoreCondition());

                    }

                    // 添加样品测试
                    EmcCapacityItemGroupBean capacityGroupItem = capacityItemGroupList.stream().filter(e -> ObjectUtils.equals(e.getId(), a.getItemGroupId())).collect(Collectors.toList()).get(0);
                    Long itemGroupId = capacityGroupItem.getItemGroupId();
                    List<EmcGroupItemBean> filterGroupItemList = groupItemList.stream().filter(e -> ObjectUtils.equals(e.getItemGroupId(), itemGroupId)).collect(Collectors.toList());
                    List<Long> itemIdList = filterGroupItemList.stream().map(e -> e.getItemId()).collect(Collectors.toList());

                    List<EmcItemBean> filterItemList = itemList.stream().filter(e -> itemIdList.contains(e.getId()) && "1".equals(e.getActivatedFlag())).collect(Collectors.toList());

                    // 判断当前样品分析项组下的启用的分析项有几种分析类型，决定插入几条样品测试数据
                    List<String> analysisCategoryList = filterItemList.stream().map(e -> e.getAnalysisCategory()).distinct().collect(Collectors.toList());

                    EmcOrderTaskBean parentOrderTask = validParentOrderTaskList.stream().filter(t -> ObjectUtils.equals(t.getTestId(), a.getTestId()) && ObjectUtils.equals(t.getTestMethodId(), a.getTestMethodId())).findFirst().orElse(null);

                    for (String analysisCategory : analysisCategoryList) {

                        // 样品测试
                        EmcOrderTaskBean orderTask = new EmcOrderTaskBean();

                        BeanUtils.copyProperties(a, orderTask);

                        orderTask.setId(ApplicationContextHelper.getNextIdentity());
                        orderTask.setAnalysisType(analysisCategory);
                        orderTask.setFolderId(o.getFolderId());
                        orderTask.setProjId(o.getProjId());
                        orderTask.setOrderId(o.getId());
                        if (!StringUtils.isEmpty(status)) {
                            orderTask.setStatus(status);
                        }
                        orderTask.setMonitorTmplId(o.getMonitorTmplId());
                        orderTask.setOrderCategory(o.getOrderCategory());
                        orderTask.setEquiptAcquNo(o.getOrderNo());
                        orderTask.setQcCategoryId(o.getQcCategoryId());
                        orderTask.setQcCategoryCode(o.getQcCategoryCode());
                        orderTask.setQcName(o.getQcName());

                        // 如果父测试不为空，关联父样测试ID
                        if (parentOrderTask != null) {
                            orderTask.setParentId(parentOrderTask.getId());
                        }

                        insertOrderTaskList.add(orderTask);
                        allOldOrderTaskList.add(orderTask);

                        // 样品测试结果
                        List<EmcItemBean> analysisCategoryItemList = filterItemList.stream().filter(e -> ObjectUtils.equals(e.getAnalysisCategory(), analysisCategory)).collect(Collectors.toList());

                        insertOrderTaskResultList.addAll(getInsertOrderTaskResultList(orderTask, analysisCategoryItemList, o));
                    }
                }
            });
        }

        getDao().insert(insertOrderTaskList);
        orderTaskResultService.getDao().insert(insertOrderTaskResultList);

        // 插入样品测试记录表
        orderTaskLogService.insert(insertOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_ADD_TEST, model);

        // 插入新的样品分析项组
        orderItemGroupService.getDao().insert(allNewOrderItemGroupList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateWeighingDate(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> filterTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<EmcOrderTaskBean> targetTaskList = getDao().selectList(filterTaskList, Arrays.asList("orderId", "runId"), Collections.singletonList("id"));
        targetTaskList.forEach(task -> task.setExt$Item("weighingDate", filterTaskList.get(0).getExt$Item("weighingDate")));

        getDao().update(targetTaskList, "weighingDate");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateWeighingEndDate(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> filterTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<EmcOrderTaskBean> targetTaskList = getDao().selectList(filterTaskList, Arrays.asList("orderId", "runId"), Collections.singletonList("id"));
        targetTaskList.forEach(task -> task.setExt$Item("weightEndDate", filterTaskList.get(0).getExt$Item("weightEndDate")));

        getDao().update(targetTaskList, "weightEndDate");
    }


    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateVocBottle(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> filterTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<EmcOrderTaskBean> targetTaskList = getDao().selectList(filterTaskList, Arrays.asList("orderId", "runId"), Arrays.asList("id", "orderId", "runId"));

        targetTaskList.forEach(task -> {
            EmcOrderTaskBean emcOrderTaskBean = filterTaskList.stream().filter(item -> task.getOrderId().equals(item.getOrderId()) && task.getRunId().equals(item.getRunId())).findFirst().orElse(null);
            if (emcOrderTaskBean != null) {
                task.setExt$Item("vocbottle", emcOrderTaskBean.getExt$Item("vocbottle").replace("，", ",").replace(" ", ""));

            }
        });

        List<String> vocBottleList = filterTaskList.stream().map(item -> item.getExt$Item("vocbottle")).collect(Collectors.toList());

        getDao().update(targetTaskList, "VOCBOTTLE");

        this.updateTaskResultVocEmptyWeight(targetTaskList, vocBottleList);

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateOilFilmNo(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> filterTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<EmcOrderTaskBean> targetTaskList = getDao().selectList(filterTaskList, Arrays.asList("orderId", "runId"), Arrays.asList("id", "orderId", "runId"));

        targetTaskList.forEach(task -> {
            EmcOrderTaskBean emcOrderTaskBean = filterTaskList.stream().filter(item -> task.getOrderId().equals(item.getOrderId()) && task.getRunId().equals(item.getRunId())).findFirst().orElse(null);
            if (emcOrderTaskBean != null) {
                task.setExt$Item("oilfilmno", emcOrderTaskBean.getExt$Item("oilfilmno").replace("，", ",").replace(" ", ""));
            }
        });

        List<String> oilFilmNoList = filterTaskList.stream().map(item -> item.getExt$Item("oilfilmno")).collect(Collectors.toList());

        getDao().update(targetTaskList, "oilFilmNo");

        this.updateTaskResultOilFilmNo(targetTaskList, oilFilmNoList);

    }


    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void batchAutoUpdateVocBottle(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> filterTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<Long> orderIdList = filterTaskList.stream().map(EmcOrderTaskBean::getOrderId).collect(Collectors.toList());

        // 只要分配了voc瓶的样品
        List<EmcOrderBean> orderList = orderService.selectListByFilter(SearchFilter.instance().match("id", orderIdList).filter(MatchPattern.OR))
                .stream()
                .filter(item -> !StringUtils.isEmpty(item.getExt$Item("vocbottle")))
                .collect(Collectors.toList());
        List<Long> filterOrderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());

        // 过来掉样品中没有voc瓶的样品测试
        List<EmcOrderTaskBean> targetTaskList = getDao().selectList(filterTaskList, Arrays.asList("orderId", "runId"), Arrays.asList("id", "orderId", "runId","qcCategoryCode"))
                .stream()
                .filter(item -> filterOrderIdList.contains(item.getOrderId()))
                .collect(Collectors.toList());

        // 设置样品测试voc瓶编号
        targetTaskList.forEach(task -> {
            EmcOrderBean emcOrderBean = orderList.stream().filter(item -> task.getOrderId().equals(item.getId())).findFirst().orElse(null);
            if (emcOrderBean != null) {
                String vocBottle = emcOrderBean.getExt$Item("vocbottle").replace("，", ",").replace(" ", "");
                vocBottle = vocBottle.replaceAll(" ","");
                // 如果存在多个voc瓶编号，默认设置第一个瓶号
                if (!StringUtils.isEmpty(vocBottle)) {
                	if(!StringUtils.isEmpty(task.getQcCategoryCode())){
                		if(vocBottle.split(",").length>=3){
                    		if(task.getQcCategoryCode().equals("N/A")){
                                task.setExt$Item("vocbottle", vocBottle.split(",")[0]);
                    		}else if(task.getQcCategoryCode().equals("L-DUP")){
                                task.setExt$Item("vocbottle", vocBottle.split(",")[1]);
                    		}else if(task.getQcCategoryCode().equals("L-SPKC")){
                                task.setExt$Item("vocbottle", vocBottle.split(",")[2]);
                    		}else{
                                task.setExt$Item("vocbottle", vocBottle.split(",")[0]);
                    		}
                		}else{
                            task.setExt$Item("vocbottle", vocBottle.split(",")[0]);
                		}

                	}
                }
            }
        });
        // voc 瓶号
        List<String> vocBottleList = targetTaskList.stream()
                .filter(item -> !StringUtils.isEmpty(item.getExt$Item("vocbottle")))
                .map(item -> item.getExt$Item("vocbottle")).collect(Collectors.toList());

        getDao().update(targetTaskList, "VOCBOTTLE");

        this.updateTaskResultVocEmptyWeight(targetTaskList, vocBottleList);

    }

    /**
     * 如果样品测试的样品测试结果的分析项别名包含 ‘VOC瓶重量’ ，将voc称重的空瓶重量填入到样品测试结果中
     * @param targetTaskList
     * @param vocBottleList
     * @return
     */
    protected List<EmcOrderTaskResultBean> updateTaskResultVocEmptyWeight (List<EmcOrderTaskBean> targetTaskList,
                                                                           List<String> vocBottleList) {

        List<PfVocWeightingBean> vocWeightingList = vocWeightingService.selectListByFilter(SearchFilter.instance().match("vocNumber", vocBottleList).filter(MatchPattern.OR));
        List<EmcOrderTaskResultBean> updateOrderTaskResultList = new ArrayList<>();
        if (!vocBottleList.isEmpty()) {
            List<Long> orderTaskIdList = targetTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());

            List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.selectListByFilter(SearchFilter.instance().match("orderTaskId", orderTaskIdList).filter(MatchPattern.OR)
             .match("itemAliasName", "VOC瓶重量").filter(MatchPattern.EQ));

            orderTaskResultList.forEach(result -> {
                targetTaskList.stream().filter(task -> task.getId().equals(result.getOrderTaskId())).findFirst().ifPresent( curOrderTask -> {
                    String vocbottle = curOrderTask.getExt$Item("vocbottle");
                    if (!StringUtils.isEmpty(vocbottle)) {
                        Optional<PfVocWeightingBean> vocWeighting = vocWeightingList.stream().filter(voc -> StringUtils.equals(voc.getVocNumber(), vocbottle)).findFirst();
                        vocWeighting.ifPresent(item -> {
                            result.setOriginalResult(item.getEmptyBottleWeight());
                            updateOrderTaskResultList.add(result);
                        });
                    }
                });

            });

        }

        orderTaskResultService.getDao().update(updateOrderTaskResultList, "ORIGINALRESULT");
        if (!updateOrderTaskResultList.isEmpty()) {
            List<Long> resultIdList = updateOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
            EmcOrderTaskResultComputingEngine.getEngine(resultIdList).compute();
        }
        return updateOrderTaskResultList;

    }

    /**
     * 如果样品测试的样品测试结果的分析项别名包含 ‘滤筒编号’ ，将滤筒编号空瓶重量填入到样品测试结果中
     * @param targetTaskList
     * @param oilFilmNoList
     * @return
     */
    protected List<EmcOrderTaskResultBean> updateTaskResultOilFilmNo (List<EmcOrderTaskBean> targetTaskList,
            List<String> oilFilmNoList) {
    	List<EmcOilFilmBean> oilFilmList = oilFilmService.getDao().selectListByOneColumnValues(oilFilmNoList, "oilFilmNo");
        List<EmcOrderTaskResultBean> updateOrderTaskResultList = new ArrayList<>();
        if (!oilFilmNoList.isEmpty()) {
            List<Long> orderTaskIdList = targetTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());

            List<EmcOrderTaskResultBean> orderTaskResultOneList = orderTaskResultService.selectListByFilter(SearchFilter.instance().match("orderTaskId", orderTaskIdList).filter(MatchPattern.OR)
             .match("itemAliasName", "空滤筒M1-1").filter(MatchPattern.SB));

            orderTaskResultOneList.forEach(result -> {
                targetTaskList.stream().filter(task -> task.getId().equals(result.getOrderTaskId())).findFirst().ifPresent( curOrderTask -> {
                    String oilFilmNo = curOrderTask.getExt$Item("oilfilmno");
                    if (!StringUtils.isEmpty(oilFilmNo)) {
                        Optional<EmcOilFilmBean> oilFilm = oilFilmList.stream().filter(oil -> StringUtils.equals(oil.getOilFilmNo(), oilFilmNo)).findFirst();
                        oilFilm.ifPresent(t -> {
                            result.setOriginalResult(t.getEmptyFilterBarrelMOneOne());
                            updateOrderTaskResultList.add(result);
                        });
                    }
                });

            });

            List<EmcOrderTaskResultBean> orderTaskResultTwoList = orderTaskResultService.selectListByFilter(SearchFilter.instance().match("orderTaskId", orderTaskIdList).filter(MatchPattern.OR)
                    .match("itemAliasName", "空滤筒M1-2").filter(MatchPattern.SB));

            orderTaskResultTwoList.forEach(result -> {
                       targetTaskList.stream().filter(task -> task.getId().equals(result.getOrderTaskId())).findFirst().ifPresent( curOrderTask -> {
                           String oilFilmNo = curOrderTask.getExt$Item("oilfilmno");
                           if (!StringUtils.isEmpty(oilFilmNo)) {
                               Optional<EmcOilFilmBean> oilFilm = oilFilmList.stream().filter(oil -> StringUtils.equals(oil.getOilFilmNo(), oilFilmNo)).findFirst();
                               oilFilm.ifPresent(t -> {
                                   result.setOriginalResult(t.getEmptyFilterBarrelMOneTwo());
                                   updateOrderTaskResultList.add(result);
                               });
                           }
                       });

                   });

            List<EmcOrderTaskResultBean> orderTaskResultThreeList = orderTaskResultService.selectListByFilter(SearchFilter.instance().match("orderTaskId", orderTaskIdList).filter(MatchPattern.OR)
                    .match("itemAliasName", "空滤筒M1-3").filter(MatchPattern.SB));

            orderTaskResultThreeList.forEach(result -> {
                       targetTaskList.stream().filter(task -> task.getId().equals(result.getOrderTaskId())).findFirst().ifPresent( curOrderTask -> {
                           String oilFilmNo = curOrderTask.getExt$Item("oilfilmno");
                           if (!StringUtils.isEmpty(oilFilmNo)) {
                               Optional<EmcOilFilmBean> oilFilm = oilFilmList.stream().filter(oil -> StringUtils.equals(oil.getOilFilmNo(), oilFilmNo)).findFirst();
                               oilFilm.ifPresent(t -> {
                                   result.setOriginalResult(t.getEmptyFilterBarrelMOneThree());
                                   updateOrderTaskResultList.add(result);
                               });
                           }
                       });

                   });

            List<EmcOrderTaskResultBean> orderTaskResultFourList = orderTaskResultService.selectListByFilter(SearchFilter.instance().match("orderTaskId", orderTaskIdList).filter(MatchPattern.OR)
                    .match("itemAliasName", "空滤筒M1-4").filter(MatchPattern.SB));

            orderTaskResultFourList.forEach(result -> {
                       targetTaskList.stream().filter(task -> task.getId().equals(result.getOrderTaskId())).findFirst().ifPresent( curOrderTask -> {
                           String oilFilmNo = curOrderTask.getExt$Item("oilfilmno");
                           if (!StringUtils.isEmpty(oilFilmNo)) {
                               Optional<EmcOilFilmBean> oilFilm = oilFilmList.stream().filter(oil -> StringUtils.equals(oil.getOilFilmNo(), oilFilmNo)).findFirst();
                               oilFilm.ifPresent(t -> {
                                   result.setOriginalResult(t.getEmptyFilterBarrelMOneFour());
                                   updateOrderTaskResultList.add(result);
                               });
                           }
                       });

                   });

            List<EmcOrderTaskResultBean> orderTaskResultFiveList = orderTaskResultService.selectListByFilter(SearchFilter.instance().match("orderTaskId", orderTaskIdList).filter(MatchPattern.OR)
                    .match("itemAliasName", "空滤筒M1-5").filter(MatchPattern.SB));

            orderTaskResultFiveList.forEach(result -> {
                       targetTaskList.stream().filter(task -> task.getId().equals(result.getOrderTaskId())).findFirst().ifPresent( curOrderTask -> {
                           String oilFilmNo = curOrderTask.getExt$Item("oilfilmno");
                           if (!StringUtils.isEmpty(oilFilmNo)) {
                               Optional<EmcOilFilmBean> oilFilm = oilFilmList.stream().filter(oil -> StringUtils.equals(oil.getOilFilmNo(), oilFilmNo)).findFirst();
                               oilFilm.ifPresent(t -> {
                                   result.setOriginalResult(t.getEmptyFilterBarrelMOneFive());
                                   updateOrderTaskResultList.add(result);
                               });
                           }
                       });

                   });

            List<EmcOrderTaskResultBean> orderTaskResultSixList = orderTaskResultService.selectListByFilter(SearchFilter.instance().match("orderTaskId", orderTaskIdList).filter(MatchPattern.OR)
                    .match("itemAliasName", "空滤筒M1-6").filter(MatchPattern.SB));

            orderTaskResultSixList.forEach(result -> {
                       targetTaskList.stream().filter(task -> task.getId().equals(result.getOrderTaskId())).findFirst().ifPresent( curOrderTask -> {
                           String oilFilmNo = curOrderTask.getExt$Item("oilfilmno");
                           if (!StringUtils.isEmpty(oilFilmNo)) {
                               Optional<EmcOilFilmBean> oilFilm = oilFilmList.stream().filter(oil -> StringUtils.equals(oil.getOilFilmNo(), oilFilmNo)).findFirst();
                               oilFilm.ifPresent(t -> {
                                   result.setOriginalResult(t.getEmptyFilterBarrelMOneSix());
                                   updateOrderTaskResultList.add(result);
                               });
                           }
                       });

                   });

            List<EmcOrderTaskResultBean> orderTaskResultSevenList = orderTaskResultService.selectListByFilter(SearchFilter.instance().match("orderTaskId", orderTaskIdList).filter(MatchPattern.OR)
                    .match("itemAliasName", "滤筒或滤膜编号").filter(MatchPattern.SB));

            orderTaskResultSevenList.forEach(result -> {
                       targetTaskList.stream().filter(task -> task.getId().equals(result.getOrderTaskId())).findFirst().ifPresent( curOrderTask -> {
                           String oilFilmNo = curOrderTask.getExt$Item("oilfilmno");
                           if (!StringUtils.isEmpty(oilFilmNo)) {
                               Optional<EmcOilFilmBean> oilFilm = oilFilmList.stream().filter(oil -> StringUtils.equals(oil.getOilFilmNo(), oilFilmNo)).findFirst();
                               oilFilm.ifPresent(t -> {
                                   result.setOriginalResult(oilFilmNo);
                                   updateOrderTaskResultList.add(result);
                               });
                           }
                       });

                   });


        }
        orderTaskResultService.getDao().update(updateOrderTaskResultList, "ORIGINALRESULT");
        if (!updateOrderTaskResultList.isEmpty()) {
            List<Long> resultIdList = updateOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
            EmcOrderTaskResultComputingEngine.getEngine(resultIdList).compute();
        }
        return updateOrderTaskResultList;
    }


    @Override
    public void updateBatchNo(RestJsonWrapperBean wrapper) {
        List orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        String stockBatchNo = ((EmcOrderTaskBean) orderTaskList.get(0)).getStockBatchNo();
        List orderTaskIdList = (List) orderTaskList.stream().map(e -> ((EmcOrderTaskBean) e).getId()).distinct().collect(Collectors.toList());
        orderTaskList = getDao().selectListByIds(orderTaskIdList, new Order[0]);
        List orderIdList = (List) orderTaskList.stream().map(e -> ((EmcOrderTaskBean) e).getOrderId()).distinct().collect(Collectors.toList());
        List runIdList = (List) orderTaskList.stream().map(e -> ((EmcOrderTaskBean) e).getRunId()).distinct().collect(Collectors.toList());
        List updateOrderTaskList = selectListByFilter(SearchFilter.instance().match("RUNID", runIdList).filter(MatchPattern.OR)
                .match("ORDERID", orderIdList).filter(MatchPattern.OR).match("STATUS", "logged").filter(MatchPattern.SEQ), new Order[0]);
        for (Object value : updateOrderTaskList) {
            EmcOrderTaskBean task = (EmcOrderTaskBean) value;
            task.setStockBatchNo(stockBatchNo);
        }
        getDao().update(updateOrderTaskList, new String[]{"STOCKBATCHNO"});
    }

    @Override
    public Page<EmcOrderTaskBean> selectUniqueOrderTaskList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        List<EmcOrderTaskBean> orderTaskList = getDao().selectOrderTaskList(parameter);

        ArrayList<EmcOrderTaskBean> uniqueOrderTaskList = orderTaskList.stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmcOrderTaskBean::getTestId))), ArrayList::new));

        return MybatisPageHelper.get(rowBounds, () -> uniqueOrderTaskList);
    }

    @Override
    public Page<EmcOrderTaskBean> selectOrderTaskByRunsList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();

        Page<EmcOrderTaskBean> orderTaskPage = selectPagination(() -> getDao().selectOrderTaskByRunsList(parameter), rowBounds);

        // 根据样品编码分组
        Map<Long, List<EmcOrderTaskBean>> map = orderTaskPage.getRows().stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getOrderId));
        List<EmcOrderTaskBean> orderTaskGroupByList = new ArrayList<>();
        // 分组后每组取第一条，并拼接测试名称
        map.forEach((orderId, curTaskList) -> {
            String concatTestName = curTaskList.stream().sorted(Comparator.comparing(EmcOrderTaskBean::getTestName)).map(EmcOrderTaskBean::getTestName).distinct().collect(Collectors.joining(","));
            EmcOrderTaskBean firstOrderTask = curTaskList.get(0);
            firstOrderTask.setExt$Item("concatTestName", concatTestName);
            orderTaskGroupByList.add(firstOrderTask);
        });
        orderTaskGroupByList.sort(Comparator.comparing(EmcOrderTaskBean::getCupNo));

        orderTaskPage.setRows(orderTaskGroupByList);

        return orderTaskPage;
    }

    @Override
    public void batchUpdateOrderTask(RestJsonWrapperBean wrapper) {
    	List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
    	Long equiptId = Long.parseLong(wrapper.getFilterValue("equiptId"));
        List<Long> idList = orderTaskList.stream().map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> toOrderTaskList = getDao().selectListByIds(idList);
        //对数组进行循环赋值仪器ID
        List<EmcOrderTaskBean> updateOrderTaskList =  new ArrayList<EmcOrderTaskBean>();
        toOrderTaskList.forEach(e -> {
        	EmcOrderTaskBean updateOrderTask = new EmcOrderTaskBean();
        	updateOrderTask.setId(e.getId());
        	updateOrderTask.setEquiptId(equiptId);
        	updateOrderTaskList.add(updateOrderTask);
        });
        getDao().update(updateOrderTaskList,"EQUIPTID");
        //需要更新仪器id的测试
        //还需要同步更新项目温湿度表的仪器id,温湿度也需要清空
        List<PfRunTempHumidityBean> toUpdateTempHumOrgList = pfRunTempHumidityService.getDao().selectListByOneColumnValues(idList, "ORDERTASKID");
        List<PfRunTempHumidityBean> toUpdateTempHumList =  new ArrayList<PfRunTempHumidityBean>();
        toUpdateTempHumOrgList.forEach(e ->{
        	PfRunTempHumidityBean toUpdateTempHum = new PfRunTempHumidityBean();
        	toUpdateTempHum.setId(e.getId());
        	toUpdateTempHum.setEquiptId(equiptId);
        	toUpdateTempHum.setTemperature("");
        	toUpdateTempHum.setHumidity("");
        	toUpdateTempHumList.add(toUpdateTempHum);

        });
        pfRunTempHumidityService.getDao().update(toUpdateTempHumList,"EQUIPTID","TEMPERATURE","HUMIDITY");
    	return;
    }

    @Override
    public Page<EmcOrderTaskBean> selectUniqueOrderList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();

        return  selectPagination(() -> getDao().selectUniqueOrderList(parameter), rowBounds);
    }

    @Override
    public void batchUpdateForOrder(RestJsonWrapperBean wrapper) {
    	List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
    	Long equiptId = Long.parseLong(wrapper.getFilterValue("equiptId"));
    	String runId = orderTaskList.get(0).getRunId();//同一批下runid都一样
        List<Long> orderIdList = orderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());

        List<EmcOrderTaskBean> toOrderTaskOrgList = getDao().selectListByOneColumnValues(orderIdList, "ORDERID");
        List<EmcOrderTaskBean> toOrderTaskList = toOrderTaskOrgList.stream()
		  .filter(e -> StringUtils.equals(runId, e.getRunId())).collect(Collectors.toList());
        //对数组进行循环赋值仪器ID
        List<EmcOrderTaskBean> updateOrderTaskList =  new ArrayList<EmcOrderTaskBean>();

        toOrderTaskList.forEach(e -> {
        	EmcOrderTaskBean updateOrderTask = new EmcOrderTaskBean();
        	updateOrderTask.setId(e.getId());
        	updateOrderTask.setEquiptId(equiptId);
        	updateOrderTaskList.add(updateOrderTask);
        });
        getDao().update(updateOrderTaskList,"EQUIPTID");
        //需要更新仪器id的测试
        //还需要同步更新项目温湿度表的仪器id,温湿度也需要清空
        List<PfRunTempHumidityBean> tempHumOrgList = pfRunTempHumidityService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");
        //温湿度也要RUNID匹配
        List<PfRunTempHumidityBean> toUpdateTempHumOrgList = tempHumOrgList.stream()
		  .filter(t -> StringUtils.equals(runId, t.getRunId())).collect(Collectors.toList());
        List<PfRunTempHumidityBean> toUpdateTempHumList =  new ArrayList<PfRunTempHumidityBean>();
        toUpdateTempHumOrgList.forEach(e ->{
        	PfRunTempHumidityBean toUpdateTempHum = new PfRunTempHumidityBean();
        	toUpdateTempHum.setId(e.getId());
        	toUpdateTempHum.setEquiptId(equiptId);
        	toUpdateTempHum.setTemperature("");
        	toUpdateTempHum.setHumidity("");
        	toUpdateTempHumList.add(toUpdateTempHum);

        });
        pfRunTempHumidityService.getDao().update(toUpdateTempHumList,"EQUIPTID","TEMPERATURE","HUMIDITY");
    }

    @Override
    public List<EmcOrderTaskResultBean> getInsertOrderTaskResultList(EmcOrderTaskBean orderTask, List<EmcItemBean> itemList, EmcOrderBean order) {
        List<EmcOrderTaskResultBean> insertOrderTaskResultList = new ArrayList<>();
        String volume = order.getExt$Item("volume");
        if(!StringUtils.isEmpty(volume)){
            volume = volume.replaceAll("[^0-9.]", "");
        }
        for (EmcItemBean item : itemList) {
            for (int index = 1; index <= item.getRepeatItem(); index++) {
                EmcOrderTaskResultBean orderTaskResult = new EmcOrderTaskResultBean();
                BeanUtils.copyProperties(item, orderTaskResult);

                orderTaskResult.setId(ApplicationContextHelper.getNextIdentity());
                orderTaskResult.setFolderId(orderTask.getFolderId());
                orderTaskResult.setOrderId(orderTask.getOrderId());
                orderTaskResult.setProjId(orderTask.getProjId());
                orderTaskResult.setOrderTaskId(orderTask.getId());
                orderTaskResult.setTestMethodId(orderTask.getTestMethodId());
                orderTaskResult.setMethodId(orderTask.getMethodId());
                orderTaskResult.setMethodVersionId(orderTask.getMethodVersionId());
                orderTaskResult.setItemId(item.getId());
                orderTaskResult.setItemOrderNo(item.getOrderNo());
                orderTaskResult.setRepeatNo(0L + index);
                orderTaskResult.setRepeatQty(item.getRepeatItem());
                orderTaskResult.setQcCategoryId(orderTask.getQcCategoryId());
                orderTaskResult.setQcCategoryCode(orderTask.getQcCategoryCode());
                orderTaskResult.setQcName(orderTask.getQcName());
                orderTaskResult.setTestId(orderTask.getTestId());
                orderTaskResult.setScriptFlag(item.getScriptFlag());
                orderTaskResult.setResultCategory(item.getResultCategory());
                orderTaskResult.setCycle(order.getCycle());
                orderTaskResult.setOrderNo(order.getOrderNo());
                orderTaskResult.setParentOrderNo(order.getParentOrderNo());
                orderTaskResult.setFrequency(order.getFrequency());
                orderTaskResult.setAnalysisType(item.getAnalysisCategory());
                if(!StringUtils.isEmpty(item.getExt$Item("ISSENDTORESULT")) && !StringUtils.isEmpty(volume)){
                    if(item.getExt$Item("ISSENDTORESULT").equals("1")){
                    	if(!StringUtils.isEmpty(item.getExt$Item("SENDCOEFFICIENT"))){
/*                            BigDecimal volume1 = new BigDecimal(String.valueOf(volume.trim()));
                            BigDecimal sendCofficient = new BigDecimal(String.valueOf(item.getExt$Item("SENDCOEFFICIENT")));
                            BigDecimal volumeF = volume1.divide(sendCofficient,4,BigDecimal.ROUND_HALF_UP);
                            orderTaskResult.setOriginalResult(volumeF.toString()+"");
                            orderTaskResult.setReportResult(volumeF.toString()+"");*/
                    		  Double volumeF = ArithUtils.div(volume.trim(), item.getExt$Item("SENDCOEFFICIENT"));

                    		  orderTaskResult.setOriginalResult(volumeF.toString()+"");
                    		  orderTaskResult.setReportResult(volumeF.toString()+"");

                    	}else{
                            orderTaskResult.setOriginalResult(volume);
                            orderTaskResult.setReportResult(volume);

                    	}
                    }
                }
                insertOrderTaskResultList.add(orderTaskResult);
            }
        }

        return insertOrderTaskResultList;
    }

    @Override
    public Page<EmcOrderTaskBean> selectUniqueItemGroupPagination(RestJsonWrapperBean wrapper) {
        Page<EmcOrderTaskBean> orderTaskPage = selectPagination(wrapper);

        List<EmcOrderTaskBean> orderTaskList = orderTaskPage.getRows();
        ArrayList<EmcOrderTaskBean> uniqueGroupList = orderTaskList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(task -> task.getTestMethodId() + "#" + task.getExt$Item("itemGroup")))), ArrayList::new));
        orderTaskPage.setRows(uniqueGroupList);

        return orderTaskPage;
    }

    @Override
    public Page<EmcOrderTaskBean> selectOrderTaskPagationByOrder(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskList = selectListByFilter(SearchFilter.instance(), wrapper);

        List<EmcOrderTaskBean> orderTaskContainerList = orderTaskList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmcOrderTaskBean::getTestName))), ArrayList::new));

        return new Pagination<>(orderTaskContainerList);
    }

    @Override
    public Page<EmcOrderTaskBean> selectOrderTaskPagationByOrderAndTask(RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        EmcOrderTaskBean containerTask = wrapper.parseUnique(EmcOrderTaskBean.class);
        List<Long> orderIds = orderList.stream().map(e -> e.getId()).distinct().collect(Collectors.toList());
        if (orderIds.isEmpty()) return new Pagination<>();
        SearchFilter filter = SearchFilter.instance().match("orderId", orderIds).filter(MatchPattern.OR);
        if (!ObjectUtils.isEmpty(containerTask.getContainerCategoryId())) {
            filter.match("ContainerCategoryId", containerTask.getContainerCategoryId()).filter(MatchPattern.SEQ);
        }
        if (!ObjectUtils.isEmpty(containerTask.getContainerGroup())) {
            filter.match("ContainerGroup", containerTask.getContainerGroup()).filter(MatchPattern.SEQ);
        }
        if (!ObjectUtils.isEmpty(containerTask.getFixative())) {
            filter.match("Fixative", containerTask.getFixative()).filter(MatchPattern.SEQ);
        }
        if (!ObjectUtils.isEmpty(containerTask.getStoreCondition())) {
            filter.match("StoreCondition", containerTask.getStoreCondition()).filter(MatchPattern.SEQ);
        }
        List<EmcOrderTaskBean> taskList = selectListByFilter(filter, new Order[0]);
        List<EmcOrderTaskBean> uniqueTasks = taskList.stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmcOrderTaskBean::getTestName))), ArrayList::new));
        return new Pagination<>(uniqueTasks);
    }

    // ----------------------------------------------------------------------
    // 私有方法
    // ----------------------------------------------------------------------
    private boolean isSolidWaste(EmcOrderBean order) {
        return Arrays.stream(PfConstant.DIOXIN_SOLID_WASTE_ORDER_CATEGORIES).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));
    }

    @Override
    public String selectStockNoByOrderTask(RestJsonWrapperBean wrapper) {
	    String stockBatchNoStr = "";
	    //根据选择的测试找到测试
	    List<EmcOrderTaskBean> orderTaskParseList = wrapper.parse(EmcOrderTaskBean.class);
	    String runId = orderTaskParseList.get(0).getRunId();
	    if(!StringUtils.isEmpty(runId)){
	        List<EmcOrderTaskBean> orderTaskList = getDao().selectListByOneColumnValue(runId, "runId", Arrays.asList("id", "methodId","QCCATEGORYCODE","testMethodId"));
	        //获取该批下的标准测试样品QC类型的EmcOrderTaskResult表数据
	        List<EmcOrderTaskBean> orderQCSTDTaskList = orderTaskList.stream().filter(o -> StringUtils.equals(EmcConstant.QCCATEGORY_QCCATEGORYID_QC_STD,o.getQcCategoryCode() )).collect(Collectors.toList());
	        List<Long> orderTaskIdList = orderQCSTDTaskList.stream().map(o ->o.getId()).collect(Collectors.toList());
	        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "orderTaskId", Arrays.asList("id", "rn16","standQty","itemName","testMethodId","itemId","qccategorycode"));
	        List<EmcOrderTaskResultBean> orderTaskQCSTDResultList = orderTaskResultList.stream().filter(r -> StringUtils.equals(EmcConstant.QCCATEGORY_QCCATEGORYID_QC_STD,r.getQcCategoryCode())).collect(Collectors.toList());
	        //获取监测能力库默认测试及方法相同的数据
	        List<Long> testMethodIdList = orderTaskQCSTDResultList.stream().map(r ->r.getTestMethodId()).distinct().collect(Collectors.toList());
	        //根据测试找到对应的库存浓度
	        List<EmcMtlMaintainItemBean> maintainItemList = mtlMaintainItemService.getDao().selectListByOneColumnValues(testMethodIdList,"testmethodid");
	        List<Long> maintainIdList = maintainItemList.stream().map(mi ->mi.getMtlMaitainId()).distinct().collect(Collectors.toList());
	        List<EmcMtlMaintainBean> maintainList = mtlMaintainService.getDao().selectListByIds(maintainIdList);
	        List<String> mtlIdList = maintainList.stream().map(m ->m.getMtlId()).distinct().collect(Collectors.toList());
	        List<EmcStockBean> stockList = stockService.getDao().selectListByOneColumnValues(mtlIdList,"mtlId");
	        List<String> batchNoList = stockList.stream().map(s ->s.getBatchNo()).collect(Collectors.toList());
	        stockBatchNoStr =  StringUtils.join(batchNoList.toArray(), ",");
    }

    return stockBatchNoStr;
    }

    @Override
    public String selectMatchStockTestMethodId(RestJsonWrapperBean wrapper) {
    	String stdMtachTestMethodIds ="";
    	String batchNo = wrapper.getFilterValue("batchNo");
	    //根据选择的测试找到测试
	    List<EmcOrderTaskBean> orderTaskParseList = wrapper.parse(EmcOrderTaskBean.class);
	    String runId = orderTaskParseList.get(0).getRunId();
	    if(!StringUtils.isEmpty(runId)){
            List<EmcOrderTaskBean> orderQCSTDTaskList = selectListByFilter(SearchFilter.instance().match("RUNID",runId).filter(MatchPattern.EQ)
                    .match("QCCATEGORYCODE",EmcConstant.QCCATEGORY_QCCATEGORYID_QC_STD).filter(MatchPattern.EQ));
	        //获取该批下的标准测试样品QC类型的EmcOrderTaskResult表数据
	        List<Long> orderTaskIdList = orderQCSTDTaskList.stream().map(o ->o.getId()).collect(Collectors.toList());
	        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "orderTaskId", Arrays.asList("id", "rn16","standQty","itemName","testMethodId","itemId","qccategorycode"));
	        List<EmcOrderTaskResultBean> orderTaskQCSTDResultList = orderTaskResultList.stream().filter(r -> StringUtils.equals(EmcConstant.QCCATEGORY_QCCATEGORYID_QC_STD,r.getQcCategoryCode())).collect(Collectors.toList());
	        //获取监测能力库默认测试及方法相同的数据
	        List<Long> testMethodIdList = orderTaskQCSTDResultList.stream().map(r ->r.getTestMethodId()).distinct().collect(Collectors.toList());
	        List<EmcMtlMaintainItemBean> maintainItemList = mtlMaintainItemService.getDao().selectListByOneColumnValues(testMethodIdList,"testmethodid");
	        //查询选择的库存
	        List<EmcStockBean> stockList =  stockService.getDao().selectListByOneColumnValue(batchNo,"batchNo", Arrays.asList("id","batchNo","mtlId"));
	        List<String> mtlIdList = stockList.stream().map(s ->s.getMtlId()).collect(Collectors.toList());
	        List<EmcMtlMaintainBean> maintainList = mtlMaintainService.getDao().selectListByOneColumnValues(mtlIdList,"mtlId", Arrays.asList("id", "mtlId"));
	        List<Long> maintainIdList = maintainList.stream().map(i ->i.getId()).collect(Collectors.toList());
	        //选择的库存
	        List<EmcMtlMaintainItemBean> maintainMatchItemList = maintainItemList.stream().filter(mi ->maintainIdList.contains(mi.getMtlMaitainId())).collect(Collectors.toList());
	        List<Long> miTestMethodIdList = maintainMatchItemList.stream().map(mi ->mi.getTestMethodId()).distinct().collect(Collectors.toList());
	        List<Long> matchTestMethodIdList = testMethodIdList.stream().filter(item ->miTestMethodIdList.contains(item)).collect(Collectors.toList());
		    stdMtachTestMethodIds =  StringUtils.join(matchTestMethodIdList.toArray(), ",");

    }

    	return stdMtachTestMethodIds;
    }
    //找到ordertask表多的测试，询问是否删除
    @Override
    public String selectOverTest(RestJsonWrapperBean wrapper) {
    	String stdMtachTestNames ="";
    	String batchNo = wrapper.getFilterValue("batchNo");
	    //根据选择的测试找到测试
	    List<EmcOrderTaskBean> orderTaskParseList = wrapper.parse(EmcOrderTaskBean.class);
	    List<Long> selectTestMethodIdList = orderTaskParseList.stream().map(task ->task.getTestMethodId()).collect(Collectors.toList());
	    String runId = orderTaskParseList.get(0).getRunId();
	    if(!StringUtils.isEmpty(runId)){
	        //获取该批下的标准测试样品QC类型的EmcOrderTaskResult表数据
            List<EmcOrderTaskBean> orderQCSTDTaskList = selectListByFilter(SearchFilter.instance().match("RUNID",runId).filter(MatchPattern.EQ)
                    .match("QCCATEGORYCODE",EmcConstant.QCCATEGORY_QCCATEGORYID_QC_STD).filter(MatchPattern.EQ));
	        List<Long> orderTaskIdList = orderQCSTDTaskList.stream().map(o ->o.getId()).collect(Collectors.toList());
	        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "orderTaskId", Arrays.asList("id", "rn16","standQty","itemName","testMethodId","itemId","testId","qccategorycode"));
	        List<EmcOrderTaskResultBean> orderTaskQCSTDResultList = orderTaskResultList.stream().filter(r -> StringUtils.equals(EmcConstant.QCCATEGORY_QCCATEGORYID_QC_STD,r.getQcCategoryCode())).collect(Collectors.toList());
	        //获取监测能力库默认测试及方法相同的数据
	        List<Long> testMethodIdList = orderTaskQCSTDResultList.stream().map(r ->r.getTestMethodId()).distinct().collect(Collectors.toList());
	        List<EmcMtlMaintainItemBean> maintainItemList = mtlMaintainItemService.getDao().selectListByOneColumnValues(testMethodIdList,"testmethodid");
	        //查询选择的库存
	        List<EmcStockBean> stockList =  stockService.getDao().selectListByOneColumnValue(batchNo,"batchNo", Arrays.asList("id","batchNo","mtlId"));
	        List<String> mtlIdList = stockList.stream().map(s ->s.getMtlId()).collect(Collectors.toList());
	        List<EmcMtlMaintainBean> maintainList = mtlMaintainService.getDao().selectListByOneColumnValues(mtlIdList,"mtlId", Arrays.asList("id", "mtlId"));
	        List<Long> maintainIdList = maintainList.stream().map(i ->i.getId()).collect(Collectors.toList());
	        //选择的库存
	        List<EmcMtlMaintainItemBean> maintainMatchItemList = maintainItemList.stream().filter(mi ->maintainIdList.contains(mi.getMtlMaitainId())).collect(Collectors.toList());
	        List<Long> miTestMethodIdList = maintainMatchItemList.stream().map(mi ->mi.getTestMethodId()).distinct().collect(Collectors.toList());
	        //标准样品测试下不在标液中的测试
	        List<Long> notInMtlTestMethodIdList = testMethodIdList.stream().filter(item ->!miTestMethodIdList.contains(item)).distinct().collect(Collectors.toList());
	        //标准样品测试下在标液中的测试
	        List<Long> inMtlTestMethodIdList = testMethodIdList.stream().filter(item ->miTestMethodIdList.contains(item)).distinct().collect(Collectors.toList());
	        //需要删除的为notInMtlTestMethodIdList +在标液里面的测试未选择的测试
	        //进一步筛选未选择的inMtlTestMethodIdList -选择的=未选择的
	        List<Long> notSelectTestMethodIdList = inMtlTestMethodIdList.stream().filter(item ->!selectTestMethodIdList.contains(item)).distinct().collect(Collectors.toList());
	        //真正要删除的测试
	        notInMtlTestMethodIdList.addAll(notSelectTestMethodIdList);
	        List<EmcOrderTaskResultBean> deleteOrderTaskQCSTDResultList= orderTaskQCSTDResultList.stream().filter(res ->notInMtlTestMethodIdList.contains(res.getTestMethodId())).collect(Collectors.toList());
	        List<Long> testIdList = deleteOrderTaskQCSTDResultList.stream().map(res ->res.getTestId()).distinct().collect(Collectors.toList());
	        List<EmcTestBean> testList = testService.getDao().selectListByIds(testIdList);
	        List<String> testNameList = testList.stream().map(t ->t.getTestName()).distinct().collect(Collectors.toList());
	        stdMtachTestNames =  StringUtils.join(testNameList.toArray(), ",");


    }

    	return stdMtachTestNames;
    }

    @Override
    public void syncOilFilmno(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> filterTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<Long> orderIdList = filterTaskList.stream().map(EmcOrderTaskBean::getOrderId).collect(Collectors.toList());

        String runId = filterTaskList.get(0).getRunId();
        List<EmcOrderTaskBean> filterUpdateTaskList = selectListByFilter(SearchFilter.instance().match("RUNID",runId).filter(MatchPattern.EQ));
        // 只要分配滤筒的样品
        List<EmcOrderBean> orderList = orderService.selectListByFilter(SearchFilter.instance().match("id", orderIdList).filter(MatchPattern.OR))
                .stream().filter(item -> !StringUtils.isEmpty(item.getExt$Item("oilFilmNo"))).collect(Collectors.toList());
        List<Long> filterOrderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> filterForUpdateTaskList = filterUpdateTaskList.stream().filter(ok ->filterOrderIdList.contains(ok.getOrderId())).collect(Collectors.toList());


        List<String> oilFilmNoList = filterForUpdateTaskList.stream().map(item -> item.getExt$Item("oilfilmno")).collect(Collectors.toList());


        this.updateTaskResultOilFilmNo(filterForUpdateTaskList, oilFilmNoList);


    }


    @Override
    public void syncNoEquiptIdTask(RestJsonWrapperBean wrapper) {
        EmcRunBean run = wrapper.parseUnique(EmcRunBean.class);
        //批下没有仪器id的测试同步该批下该样品下其他测试的仪器id
        String runId = run.getId();
        List<EmcOrderTaskBean> orderTaskList = getDao().selectListByOneColumnValue(runId, "runId");
        List<EmcOrderTaskBean> orderTaskEmptyEquiptList = orderTaskList.stream().filter(ok ->ObjectUtils.isEmpty(ok.getEquiptId())).collect(Collectors.toList());
        if(orderTaskEmptyEquiptList.isEmpty()){
        	return;
        };
        List<EmcOrderTaskBean> orderTaskUpdateList = new ArrayList<>();
        orderTaskEmptyEquiptList.forEach(em ->{
        	List<EmcOrderTaskBean> matchOrderTaskList = orderTaskList.stream().filter(ok ->ObjectUtils.equals(em.getOrderId(),ok.getOrderId()))
        			.filter(ok ->!ObjectUtils.isEmpty(ok.getEquiptId())).collect(Collectors.toList());
        	if(!matchOrderTaskList.isEmpty()){
            	em.setEquiptId(matchOrderTaskList.get(0).getEquiptId());
        	}
        	orderTaskUpdateList.add(em);
        });
        getDao().update(orderTaskUpdateList,"equiptId");

    }

    @Override
    @ValidateDataWith(PfSubpackageTaskValidator.class)
    @Transactional
    public void subpackageTest(RestJsonWrapperBean wrapper) {
        String subpackageType = wrapper.getParamValue("subpackageType");
        String byFolder = wrapper.getParamValue("byFolder");

        boolean beforeSampling = StringUtils.equals(subpackageType, "beforeSampling");
        boolean isByFolder = StringUtils.equals(byFolder, "1");
        List<EmcFolderBean> folderList = wrapper.parse(EmcFolderBean.class);
        List<EmcOrderTaskBean> orderTaskBeforeList = wrapper.parse(EmcOrderTaskBean.class);
        List<Long> orderTaskIdList = orderTaskBeforeList.stream().map(EmcOrderTaskBean :: getId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = getDao().selectListByIds(orderTaskIdList);


        List<EmcOrderBean> orderList = new ArrayList<>();
        //如果按点位，样品为点位下所有样品
        if (isByFolder) {
            List<Long> folderIdList = folderList.stream().map(EmcFolderBean :: getId).distinct().collect(Collectors.toList());
            List<EmcOrderBean> orderByFolderList = orderService.getDao().selectListByOneColumnValues(folderIdList,"folderId");
            orderList.addAll(orderByFolderList);
        }else{
            orderList.addAll(wrapper.parse(EmcOrderBean.class));
        }
        //更新监测样品表
        List<String> folderNoList = folderList.stream().map(EmcFolderBean :: getFolderNo).distinct().collect(Collectors.toList());
       	Long projId = folderList.get(0).getProjId();
        List<EmcProjSchemeBean> schemeList = projSchemeService.selectListByFilter(SearchFilter.instance().match("folderno", folderNoList).filter(MatchPattern.OR)
        		.match("projid", projId).filter(MatchPattern.EQ));
        //选中的测试，方法
        List<Long> testIdList = orderTaskList.stream().map(o ->o.getTestId()).distinct().collect(Collectors.toList());
        if(!schemeList.isEmpty()){
        	List<Long> schemeIdList =  schemeList.stream().map(EmcProjSchemeBean :: getId).distinct().collect(Collectors.toList());
            List<EmcProjOrderBean> projOrderList = projOrderService.selectListByFilter(SearchFilter.instance()
            		.match("projschemeid", schemeIdList).filter(MatchPattern.OR)
            		.match("testId", testIdList).filter(MatchPattern.OR));

            if(!projOrderList.isEmpty()){
                projOrderList.forEach(po ->{
                	po.setExt$Item("SUBCONTRACT2", "1");
                	if(beforeSampling){
                        po.setExt$Item("SUBPACKAGETYPE", "beforeSampling");
                    }else {
                        po.setExt$Item("SUBPACKAGETYPE", "orderSubpackage");
                    }
                });
                projOrderService.getDao().update(projOrderList, "SUBCONTRACT2","SUBPACKAGETYPE");
            }
        }

        List<EmcOrderTaskBean> filterTaskList = new ArrayList<>();

        List<Long> testMethodIdList = orderTaskList.stream().map(EmcOrderTaskBean::getTestMethodId).distinct().collect(Collectors.toList());
        if (folderList.size() == 1) {
            List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
            List<String> searchColumns = new ArrayList<>(Arrays.asList("orderId", "testMethodId"));
            orderIdList.forEach(orderId -> testMethodIdList.forEach(testMethodId -> {
                EmcOrderTaskBean filterTask = new EmcOrderTaskBean();
                filterTask.setOrderId(orderId);
                filterTask.setTestMethodId(testMethodId);
                filterTaskList.add(filterTask);
            }));

            orderTaskList = getDao().selectList(filterTaskList, searchColumns, Arrays.asList("id", "runId","orderId","testMethodId", "status", "analysisType"));
        } else {
            List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
            List<String> searchColumns = new ArrayList<>(Arrays.asList("folderId", "testMethodId"));
            folderIdList.forEach(folderId -> testMethodIdList.forEach(testMethodId -> {
                EmcOrderTaskBean filterTask = new EmcOrderTaskBean();
                filterTask.setFolderId(folderId);
                filterTask.setTestMethodId(testMethodId);
                if (!beforeSampling) {
                    filterTask.setAnalysisType(PfBizConstant.ANALYSISCATEGORY_ANALYSIS);
                    searchColumns.add("analysisType");
                }
                filterTaskList.add(filterTask);
            }));

            orderTaskList = getDao().selectList(filterTaskList, searchColumns, Arrays.asList("id", "runId","orderId","testMethodId","status", "analysisType"));
        }

        // 如果是采样后分包（即样品分包）的话，只需要分包分析的
        List<EmcOrderTaskBean> validTaskList = beforeSampling ? orderTaskList : orderTaskList.stream().filter(task -> PfBizConstant.ANALYSISCATEGORY_ANALYSIS.equals(task.getAnalysisType())).collect(Collectors.toList());;

        Long subcontractorId = ApplicationContextHelper.getNextIdentity();
        String subpackageNo = ApplicationContextHelper.getNextSequence(orderItemGroupService.getDao().getTable());

        List<Long> validOrderTaskIdList = validTaskList.stream().map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());
        // 测试分包记录
        List<EmcSubcontractRecordBean> recordList = new ArrayList<>(validOrderTaskIdList.size());
        validOrderTaskIdList.forEach(taskId -> {
            EmcSubcontractRecordBean record = new EmcSubcontractRecordBean();
            record.setId(ApplicationContextHelper.getNextIdentity());
            record.setOrderTaskId(taskId);
            record.setSubcontractorId(subcontractorId);
            recordList.add(record);
        });
        subcontractRecordService.getDao().insert(recordList);

        List<Long> orderIdList = orderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<EmcOrderItemGroupBean> orderItemGroupBeans = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList,"orderId");
        List<EmcOrderItemGroupBean> orderItemGroupUpdateBeans = new ArrayList<>();
        orderTaskList.forEach(task -> {
            // 对于采样后分包的，不修改采样类型的测试状态，仅修改分包类型，分包类型针对测试而言的
            if (validOrderTaskIdList.contains(task.getId())) {
                task.setSubPackageNo(subpackageNo);
                task.setSubcontractorId(subcontractorId);
                task.setStatus(PfBizConstant.ORDERTASK_STATUS_DONE);
                task.setSamplingSubpackage("1");
            }
            task.setExt$Item("subpackageType", subpackageType);
            if(StringUtils.equals("orderSubpackage",subpackageType)){
                List<EmcOrderItemGroupBean> orderItemGroupMatChBeans =  orderItemGroupBeans.stream().filter(oig ->ObjectUtils.equals(oig.getOrderId(),task.getOrderId()))
                        .filter(oig ->ObjectUtils.equals(oig.getTestMethodId(),task.getTestMethodId())).collect(Collectors.toList());
                if(!orderItemGroupMatChBeans.isEmpty()){
                    orderItemGroupUpdateBeans.addAll(orderItemGroupMatChBeans);
                }
            }

        });
        if(!orderItemGroupUpdateBeans.isEmpty()){
            orderItemGroupUpdateBeans.forEach(oig ->{
                oig.setExt$Item("subpackageType","orderSubpackage");
            });
            orderItemGroupService.getDao().update(orderItemGroupUpdateBeans,"subpackageType");
        }

        getDao().update(orderTaskList, "status", "samplingsubpackage", "subcontractorId", "subpackageNo", "subpackageType");

        //给日志信息
        //登陆人信息
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        //项目信息(需要替换projID)
        long projectEditMatchId = orderList.get(0).getProjId() ;
        if(ObjectUtils.isEmpty(projectEditMatchId)){
            return;
        }
        EmcProjectBean projectEditMatch = projectService.getDao().selectById(projectEditMatchId);
        //操作信息(根据业务种类不同区分)
        //resource层的备注 +
        EmcProjectEditLogBean editLogBean = new EmcProjectEditLogBean();
        editLogBean.setId(ApplicationContextHelper.getNextIdentity());
        editLogBean.setIsSample(projectEditMatch.getSample());
        editLogBean.setOperateInfo("项目方案修改-分包测试");
        editLogBean.setOrgId(parentOrgId);
        editLogBean.setProjId(projectEditMatch.getId());
        editLogBean.setProjNo(projectEditMatch.getProjNo());
        editLogBean.setCreatedById(LocalContextHelper.getLoginUserId());
        editLogBean.setCreatedByName(LocalContextHelper.getLoginUserName());
        editLogBean.setCreatedTime(LocalDateTime.now());
        editLogBean.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
        editLogBean.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
        projectEditLogService.getDao().insert(editLogBean);
    }

    @Override
    @Transactional
    public void cancelSubpackageTest(RestJsonWrapperBean wrapper) {
        List<EmcFolderBean> folderList = wrapper.parse(EmcFolderBean.class);
        List<EmcOrderTaskBean> targetOrderTaskList = wrapper.parse(EmcOrderTaskBean.class);

        String byFolder = wrapper.getParamValue("byFolder");
        boolean isByFolder = StringUtils.equals(byFolder, "1");
        List<EmcOrderBean> orderList = new ArrayList<>();
        //如果按点位，样品为点位下所有样品
        if (isByFolder) {
            List<Long> folderIdList = folderList.stream().map(EmcFolderBean :: getId).distinct().collect(Collectors.toList());
            List<EmcOrderBean> orderByFolderList = orderService.getDao().selectListByOneColumnValues(folderIdList,"folderId");
            orderList.addAll(orderByFolderList);
        }else{
            orderList.addAll(wrapper.parse(EmcOrderBean.class));
        }

        //更新监测样品表
        List<String> folderNoList = folderList.stream().map(EmcFolderBean :: getFolderNo).distinct().collect(Collectors.toList());
       	Long projId = folderList.get(0).getProjId();

        List<EmcProjSchemeBean> schemeList = projSchemeService.selectListByFilter(SearchFilter.instance().match("folderno", folderNoList).filter(MatchPattern.OR)
        		.match("projid", projId).filter(MatchPattern.EQ));
        List<Long> testIdList = targetOrderTaskList.stream().map(o ->o.getTestId()).distinct().collect(Collectors.toList());
        if(!schemeList.isEmpty()){
        	List<Long> schemeIdList =  schemeList.stream().map(EmcProjSchemeBean :: getId).distinct().collect(Collectors.toList());
            List<EmcProjOrderBean> projOrderList = projOrderService.selectListByFilter(SearchFilter.instance()
            		.match("projschemeid", schemeIdList).filter(MatchPattern.OR)
            		.match("testId", testIdList).filter(MatchPattern.OR));
        	if(!projOrderList.isEmpty()){
                projOrderList.forEach(po ->{
                	po.setExt$Item("SUBCONTRACT2", null);
                    po.setExt$Item("SUBPACKAGETYPE", null);

                });
                projOrderService.getDao().update(projOrderList, "SUBCONTRACT2","SUBPACKAGETYPE");
            }
        }

        List<EmcOrderTaskBean> filterTaskList = new ArrayList<>();

        List<Long> testMethodIdList = targetOrderTaskList.stream().map(EmcOrderTaskBean::getTestMethodId).distinct().collect(Collectors.toList());
        if (folderList.size() == 1) {
            List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
            orderIdList.forEach(orderId -> testMethodIdList.forEach(testMethodId -> {
                EmcOrderTaskBean filterTask = new EmcOrderTaskBean();
                filterTask.setOrderId(orderId);
                filterTask.setTestMethodId(testMethodId);
                filterTaskList.add(filterTask);
            }));

            targetOrderTaskList = getDao().selectList(filterTaskList, Arrays.asList("orderId", "testMethodId"), Arrays.asList("id", "runId", "status", "subcontractorid", "analysisType"));
        } else {
            List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
            folderIdList.forEach(folderId -> testMethodIdList.forEach(testMethodId -> {
                EmcOrderTaskBean filterTask = new EmcOrderTaskBean();
                filterTask.setFolderId(folderId);
                filterTask.setTestMethodId(testMethodId);
                filterTaskList.add(filterTask);
            }));

            targetOrderTaskList = getDao().selectList(filterTaskList, Arrays.asList("folderId", "testMethodId"), Arrays.asList("id", "runId", "status", "subcontractorid", "analysisType"));
        }

        List<EmcOrderTaskBean> validOrderTaskList = targetOrderTaskList.stream().filter(task -> !ObjectUtils.isEmpty(task.getSubcontractorId())).collect(Collectors.toList());

        List<Long> validOrderTaskIdList = validOrderTaskList.stream().map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());
        // 删除测试分包记录
        List<EmcSubcontractRecordBean> recordList = new ArrayList<>(validOrderTaskIdList.size());
        validOrderTaskIdList.forEach(taskId -> {
            EmcSubcontractRecordBean record = new EmcSubcontractRecordBean();
            record.setOrderTaskId(taskId);
            recordList.add(record);
        });
        subcontractRecordService.getDao().deleteBy(recordList, "orderTaskId");

        List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = getDao().selectListByOneColumnValues(folderIdList, "folderId", Arrays.asList("id", "status", "analysisType"));

        // 恢复分析类型的状态
        List<String> defaultAnalysisTaskStatusList = Arrays.asList("draft", "preLogged", "logged");
        List<EmcOrderTaskBean> analysisTaskList = orderTaskList.stream().filter(task -> PfBizConstant.ANALYSISCATEGORY_ANALYSIS.equals(task.getAnalysisType())).collect(Collectors.toList());
        List<String> analysisTaskStatusList = analysisTaskList.stream().filter(task -> !validOrderTaskIdList.contains(task.getId())).map(EmcOrderTaskBean::getStatus).distinct().collect(Collectors.toList());
        List<Integer> analysisStatusIndexList = analysisTaskStatusList.stream().mapToInt(defaultAnalysisTaskStatusList::indexOf).sorted().boxed().collect(Collectors.toList());
        String newAnalysisTaskStatus = (analysisStatusIndexList.isEmpty() || analysisStatusIndexList.get(0) == -1) ? "logged" : defaultAnalysisTaskStatusList.get(analysisStatusIndexList.get(0));

        // 恢复采样类型的状态
        List<String> defaultSamplingTaskStatusList = Arrays.asList("draft", "approve", "audit", "done");
        List<EmcOrderTaskBean> samplingTaskList = orderTaskList.stream().filter(task -> PfBizConstant.ANALYSISCATEGORY_SAMPLING.equals(task.getAnalysisType())).collect(Collectors.toList());
        List<String> samplingTaskStatusList = samplingTaskList.stream().filter(task -> !validOrderTaskIdList.contains(task.getId())).map(EmcOrderTaskBean::getStatus).distinct().collect(Collectors.toList());
        List<Integer> samplingStatusIndexList = samplingTaskStatusList.stream().mapToInt(defaultSamplingTaskStatusList::indexOf).sorted().boxed().collect(Collectors.toList());
        String newSamplingTaskStatus = (samplingStatusIndexList.isEmpty() || samplingStatusIndexList.get(0) == -1) ? "draft" : defaultSamplingTaskStatusList.get(samplingStatusIndexList.get(0));

        targetOrderTaskList.forEach(task -> {
            if (validOrderTaskIdList.contains(task.getId())) {
                task.setSubPackageNo(null);
                task.setSubcontractorId(null);
                task.setSamplingSubpackage(null);
                task.setStatus(PfBizConstant.ANALYSISCATEGORY_ANALYSIS.equals(task.getAnalysisType()) ? newAnalysisTaskStatus : newSamplingTaskStatus);
            }
            task.setExt$Item("subpackageType", null);
        });

        getDao().update(targetOrderTaskList, "status", "samplingsubpackage", "subcontractorId", "subpackageNo", "subpackageType");

        //给日志信息
        //登陆人信息
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        //项目信息(需要替换projID)
        long projectEditMatchId = orderList.get(0).getProjId() ;
        if(ObjectUtils.isEmpty(projectEditMatchId)){
            return;
        }
        EmcProjectBean projectEditMatch = projectService.getDao().selectById(projectEditMatchId);
        //操作信息(根据业务种类不同区分)
        //resource层的备注 +
        EmcProjectEditLogBean editLogBean = new EmcProjectEditLogBean();
        editLogBean.setId(ApplicationContextHelper.getNextIdentity());
        editLogBean.setIsSample(projectEditMatch.getSample());
        editLogBean.setOperateInfo("项目方案修改-取消分包测试");
        editLogBean.setOrgId(parentOrgId);
        editLogBean.setProjId(projectEditMatch.getId());
        editLogBean.setProjNo(projectEditMatch.getProjNo());
        editLogBean.setCreatedById(LocalContextHelper.getLoginUserId());
        editLogBean.setCreatedByName(LocalContextHelper.getLoginUserName());
        editLogBean.setCreatedTime(LocalDateTime.now());
        editLogBean.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
        editLogBean.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
        projectEditLogService.getDao().insert(editLogBean);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateResultIndex(RestJsonWrapperBean jsonWrapper) {
        List<EmcOrderTaskBean> orderTaskList = jsonWrapper.parse(EmcOrderTaskBean.class);
        List<EmcIndexSetBean> indexSetList = jsonWrapper.parse(EmcIndexSetBean.class);
        List<Long> orderTaskIdList = orderTaskList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> resultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID");
        List<String> runIdList = resultList.stream().map(EmcOrderTaskResultBean :: getRunId)
                .filter(r -> !StringUtils.isEmpty(r)).distinct().collect(Collectors.toList());
        String runId = runIdList.get(0);
        List<Long> resultIdList = resultList.stream().map(EmcOrderTaskResultBean :: getId).distinct().collect(Collectors.toList());
        Map<Long,String> propertytmplId2NameMap = propertyTmplService.getDao().selectListByOneColumnValue("evaluationStandard", "TEMPLATEUSE").stream().collect(Collectors.toMap(e -> e.getId(),e -> e.getTemplateName(),  (s1, s2) -> s1));
        List<Long> indexItemIdList = indexSetList.stream().map(e -> e.getIndexItemId()).collect(Collectors.toList());
        List<EmcIndexTestItemBean> indexTestItemList = indexTestItemService.getDao().selectListByOneColumnValues(indexItemIdList, "INDEXITEMID");

        for (EmcIndexSetBean indexSet : indexSetList) {
            Long indexItemId = indexSet.getIndexItemId();// 指标项目ID
            String indexLowLimit = indexSet.getIndexLowLimit(); // 指标低限
            String indexHighLimit = indexSet.getIndexHighLimit(); // 指标高限
            String indexLimitUnit = indexSet.getUnit(); // 指标高低限单位

            List<EmcIndexTestItemBean> filterIndexTestItemList = indexTestItemList.stream().filter(f -> ObjectUtils.equals(indexItemId, f.getIndexItemId())).collect(Collectors.toList());
            resultList.stream().forEach(e -> {
                if (filterIndexTestItemList.stream().anyMatch(b -> ObjectUtils.equals(e.getItemId(), b.getItemId()))) {
                    e.setIndexLowLimit(indexLowLimit);
                    e.setIndexHighLimit(indexHighLimit);
                    e.setIndexLimitUnit(indexLimitUnit);
                }
            });
        }

        // 更新昼夜评价的指标高低限
        Optional<Long> dayNightEvalPropertyTmplIdOption = propertytmplId2NameMap.entrySet().stream().filter(e -> e.getValue().contains(EmcConstant.DAY_NIGHT_EVAL_PROPERTY_TMPL_NAME)).map(e -> e.getKey()).findFirst();
        if(dayNightEvalPropertyTmplIdOption.isPresent()){
            List<EmcIndexCategoryBean> indexCategoryList = indexCategoryService.getDao().selectListByOneColumnValue(dayNightEvalPropertyTmplIdOption.get(), "PROPERTYTMPLID");
            if (!indexCategoryList.isEmpty()) {
                List<Long> indexCategoryIdList = indexCategoryList.stream().map(EmcIndexCategoryBean::getId).collect(Collectors.toList());
                List<String> indexIdList = indexSetList.stream().filter(s -> indexCategoryIdList.contains(s.getIndexCategoryId())).map(EmcIndexSetBean::getIndexId).distinct().collect(Collectors.toList());

                if (!indexIdList.isEmpty()) {
                    // 从指标设置中过滤指标项目ID
                    List<Long> validIndexItemIdList = indexSetList.stream().filter(s -> indexIdList.contains(s.getIndexId())).map(EmcIndexSetBean::getIndexItemId).distinct().collect(Collectors.toList());
                    List<Long> filterItemIdList = indexTestItemList.stream().filter(i -> validIndexItemIdList.contains(i.getIndexItemId())).map(EmcIndexTestItemBean::getItemId)
                            .collect(Collectors.toList());
                    List<Long> indexSetIdList = indexSetList.stream().filter(s -> indexIdList.contains(s.getIndexId())).map(EmcIndexSetBean::getId).collect(Collectors.toList());
                    List<EmcPropertyDataBean> propertyDataList = propertyDataService.getDao().selectListByOneColumnValues(indexSetIdList, "BIZID");

                    LocalTime dayStartTime = LocalTime.of(5, 59); // 昼间开始时间 6:00
                    LocalTime dayEndTime = LocalTime.of(22, 00); // 昼间结束时间 21:59

                    List<Long> orderIdList = orderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
                    List<EmcOrderBean> orderList = orderService.getDao().selectListByIds(orderIdList);

                    resultList.stream().filter(r -> filterItemIdList.contains(r.getItemId())).forEach(r -> {
                        EmcOrderTaskBean orderTaskBean = orderTaskList.stream().filter(t -> ObjectUtils.equals(t.getId(), r.getOrderTaskId())).findFirst().get();
                        indexSetList.stream().filter(s -> ObjectUtils.equals(s.getIndexId(), orderTaskBean.getIndexId())).findFirst().ifPresent(s -> {
                            EmcOrderBean orderBean = orderList.stream().filter(o -> ObjectUtils.equals(o.getId(), r.getOrderId())).findFirst().get();
                            propertyDataList.stream().filter(d -> ObjectUtils.equals(d.getBizId(), s.getId())).findFirst().ifPresent(d -> {
                                String sampleBeginTime = orderBean.getSampleBeginTime();
                                if (!StringUtils.isEmpty(sampleBeginTime)) {
                                    String[] pairs = sampleBeginTime.split(":");
                                    LocalTime sampleTime = LocalTime.of(NumberUtils.parseInt(pairs[0]), NumberUtils.parseInt(pairs[1]));
                                    if (sampleTime.isAfter(dayStartTime) && sampleTime.isBefore(dayEndTime)) {
                                        r.setIndexHighLimit(d.getExt1()); // 昼间指标高限
                                    } else {
                                        r.setIndexHighLimit(d.getExt2()); // 夜间指标低限
                                    }
                                }
                            });
                        });
                    });
                }
            }
        }


        //更新内插法、外插法的指标高限
        Optional<Long> interpolateEvalPropertyTmplIdOption = propertytmplId2NameMap.entrySet().stream().filter(e -> e.getValue().contains(EmcConstant.INTERPOLATE_EVAL_PROPERTY_TMPL_NAME)).map(e -> e.getKey()).findFirst();

        if(interpolateEvalPropertyTmplIdOption.isPresent()){
            List<EmcIndexCategoryBean> indexCategoryOfInterpolationList = indexCategoryService.getDao().selectListByOneColumnValue(interpolateEvalPropertyTmplIdOption.get(), "PROPERTYTMPLID");
            if (!indexCategoryOfInterpolationList.isEmpty()) {
                List<Long> indexCategoryIdList = indexCategoryOfInterpolationList.stream().map(EmcIndexCategoryBean::getId).collect(Collectors.toList());
                List<String> indexIdList = indexSetList.stream().filter(s -> indexCategoryIdList.contains(s.getIndexCategoryId())).map(EmcIndexSetBean::getIndexId).distinct().collect(Collectors.toList());
                // 从指标设置中过滤指标项目ID
                List<Long> validIndexItemIdList = indexSetList.stream().filter(s -> indexIdList.contains(s.getIndexId())).map(EmcIndexSetBean::getIndexItemId).distinct().collect(Collectors.toList());
                List<Long> filterItemIdList = indexTestItemList.stream().filter(i -> validIndexItemIdList.contains(i.getIndexItemId())).map(EmcIndexTestItemBean::getItemId)
                        .collect(Collectors.toList());
                List<Long> indexSetIdList = indexSetList.stream().filter(s -> indexIdList.contains(s.getIndexId())).map(EmcIndexSetBean::getId).collect(Collectors.toList());
                List<EmcPropertyDataBean> propertyDataList = propertyDataService.getDao().selectListByOneColumnValues(indexSetIdList, "BIZID");

                //获取烟囱高度,属于一个点位
                Long folderId = resultList.stream().map(e -> e.getFolderId()).findFirst().get();
                List<EmcPropertyDataBean> propertyDataListOfChimney = propertyDataService.selectListByFilter(SearchFilter.instance().match("TABLENAME", "T_EMC_FOLDER").filter(MatchPattern.EQ).match("bizId", folderId).filter(MatchPattern.EQ));
                Double chimneyHeight = NumberUtils.parseDouble(propertyDataListOfChimney.get(0).getExt50());
                //通过烟囱高度比较确定使用哪个方法进行计算
                List<EmcPropertyFieldBean> propertyFieldList = propertyFieldService.getDao().selectListByOneColumnValue(interpolateEvalPropertyTmplIdOption.get(), "TMPLID");
                List<EmcPropertyFieldBean> filedNameListBySorted = propertyFieldList.stream().sorted(Comparator.comparing(EmcPropertyFieldBean::getFieldName)).collect(Collectors.toList());
                double maxFieldName = propertyFieldList.stream().mapToDouble(e -> NumberUtils.parseDouble(e.getFieldName())).max().orElse(0.00);
                //比较最大高度
                EmcPropertyFieldBean field = propertyFieldList.stream().filter(p -> ObjectUtils.equals(maxFieldName, NumberUtils.parseDouble(p.getFieldName()))).findFirst().get();
                if (chimneyHeight == maxFieldName) {
                    //说明就是最高高度
                    String internalField = field.getInternalField();
                    indexSetList.forEach(e -> {
                        EmcPropertyDataBean emcPropertyDataBean = propertyDataList.stream().filter(f -> ObjectUtils.equals(e.getId(), f.getBizId())).findFirst().get();
                        String propertyValue = BeanUtils.getPropertyValue(emcPropertyDataBean, internalField).toString();
                        resultList.stream().forEach(r -> {
                            if (filterItemIdList.stream().anyMatch(b -> ObjectUtils.equals(r.getItemId(), b))) {
                                r.setIndexHighLimit(propertyValue);
                            }
                        });
                    });
                } else if (chimneyHeight > maxFieldName) {
                    //使用外推法
                    String internalField = field.getInternalField();
                    indexSetList.forEach(e -> {
                        EmcPropertyDataBean emcPropertyDataBean = propertyDataList.stream().filter(f -> ObjectUtils.equals(e.getId(), f.getBizId())).findFirst().get();
                        String propertyValue = BeanUtils.getPropertyValue(emcPropertyDataBean, internalField).toString();
                        double mul = ArithUtils.mul(ArithUtils.div(chimneyHeight, maxFieldName), ArithUtils.div(chimneyHeight, maxFieldName));
                        String highLimit = ObjectUtils.toString(ArithUtils.mul(NumberUtils.parseDouble(propertyValue), mul));
                        resultList.stream().forEach(r -> {
                            if (filterItemIdList.stream().anyMatch(b -> ObjectUtils.equals(r.getItemId(), b))) {
                                r.setIndexHighLimit(highLimit);
                            }
                        });
                    });
                } else {
                    //内插法,如果区间有相等的值则直接更新
                    for (EmcPropertyFieldBean fieldBean : filedNameListBySorted) {
                        if (chimneyHeight == NumberUtils.parseDouble(fieldBean.getFieldName())) {
                            String internalField = fieldBean.getInternalField();
                            indexSetList.forEach(e -> {
                                EmcPropertyDataBean emcPropertyDataBean = propertyDataList.stream().filter(f -> ObjectUtils.equals(e.getId(), f.getBizId())).findFirst().get();
                                String propertyValue = BeanUtils.getPropertyValue(emcPropertyDataBean, internalField).toString();
                                resultList.stream().forEach(r -> {
                                    if (filterItemIdList.stream().anyMatch(b -> ObjectUtils.equals(r.getItemId(), b))) {
                                        r.setIndexHighLimit(propertyValue);
                                    }
                                });
                            });
                        } else {
                            for (int i = 1; i < filedNameListBySorted.size(); i++) {
                                if (NumberUtils.parseDouble(filedNameListBySorted.get(i - 1).getFieldName()) < chimneyHeight && chimneyHeight < NumberUtils.parseDouble(filedNameListBySorted.get(i).getFieldName())) {
                                    String maxFieldNameForChimneyHeight = filedNameListBySorted.get(i - 1).getFieldName();
                                    String minFieldNameForChimneyHeight = filedNameListBySorted.get(i).getFieldName();
                                    String maxInternalField = filedNameListBySorted.get(i - 1).getInternalField();
                                    String minInternalField = filedNameListBySorted.get(i).getInternalField();
                                    indexSetList.forEach(e -> {
                                        EmcPropertyDataBean emcPropertyDataBean = propertyDataList.stream().filter(f -> ObjectUtils.equals(e.getId(), f.getBizId())).findFirst().get();
                                        String maxPropertyValue = BeanUtils.getPropertyValue(emcPropertyDataBean, maxInternalField).toString();
                                        String minPropertyValue = BeanUtils.getPropertyValue(emcPropertyDataBean, minInternalField).toString();
                                        double sub = ArithUtils.sub(NumberUtils.parseDouble(minPropertyValue), NumberUtils.parseDouble(maxPropertyValue));
                                        double subHeightQ1 = ArithUtils.sub(chimneyHeight, NumberUtils.parseDouble(maxFieldNameForChimneyHeight));
                                        double subHeightQ2 = ArithUtils.sub(NumberUtils.parseDouble(minFieldNameForChimneyHeight), NumberUtils.parseDouble(maxFieldNameForChimneyHeight));
                                        double mul = ArithUtils.mul(sub, ArithUtils.div(subHeightQ1, subHeightQ2));
                                        String propertyValue = ObjectUtils.toString(ArithUtils.add(NumberUtils.parseDouble(maxPropertyValue), mul));
                                        resultList.stream().forEach(r -> {
                                            if (filterItemIdList.stream().anyMatch(b -> ObjectUtils.equals(r.getItemId(), b))) {
                                                r.setIndexHighLimit(propertyValue);
                                            }
                                        });
                                    });
                                }
                            }
                        }
                    }
                }
            }
        }
        resultList.forEach(res ->{
            res.setExt$Item("indexaddflag","1");
        });
        orderTaskResultService.getDao().update(resultList, "INDEXLOWLIMIT", "INDEXHIGHLIMIT","INDEXADDFLAG", "INDEXLIMITUNIT");
        asyncFormulaComputingService.asyncComputing(runId, resultIdList, LocalContextHelper.getLoginUserId());

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateSampleBottle(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> filterTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<EmcOrderTaskBean> targetTaskList = getDao().selectList(filterTaskList, Arrays.asList("orderId", "runId"), Arrays.asList("id", "orderId", "runId"));

        targetTaskList.forEach(task -> {
            EmcOrderTaskBean emcOrderTaskBean = filterTaskList.stream().filter(item -> task.getOrderId().equals(item.getOrderId()) && task.getRunId().equals(item.getRunId())).findFirst().orElse(null);
            if (emcOrderTaskBean != null) {
                task.setExt$Item("samplebottleno", emcOrderTaskBean.getExt$Item("samplebottleno").replace("，", ",").replace(" ", ""));
            }
        });

        List<String> sampleBottleList = filterTaskList.stream().map(item -> item.getExt$Item("samplebottleno")).collect(Collectors.toList());

        getDao().update(targetTaskList, "SAMPLEBOTTLENO");

        this.updateTaskResultSampleEmptyWeight(targetTaskList, sampleBottleList);

    }



    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void batchAutoUpdateSampleBottle(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> filterTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<Long> orderIdList = filterTaskList.stream().map(EmcOrderTaskBean::getOrderId).collect(Collectors.toList());

        // 只要分配了sample瓶的样品
        List<EmcOrderBean> orderList = orderService.selectListByFilter(SearchFilter.instance().match("id", orderIdList).filter(MatchPattern.OR))
                .stream()
                .filter(item -> !StringUtils.isEmpty(item.getExt$Item("samplebottleno")))
                .collect(Collectors.toList());
        List<Long> filterOrderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());

        // 过来掉样品中没有sample瓶的样品测试
        List<EmcOrderTaskBean> targetTaskList = getDao().selectList(filterTaskList, Arrays.asList("orderId", "runId"), Arrays.asList("id", "orderId", "runId","qcCategoryCode"))
                .stream()
                .filter(item -> filterOrderIdList.contains(item.getOrderId()))
                .collect(Collectors.toList());

        // 设置样品测试sample瓶编号
        targetTaskList.forEach(task -> {
            EmcOrderBean emcOrderBean = orderList.stream().filter(item -> task.getOrderId().equals(item.getId())).findFirst().orElse(null);
            if (emcOrderBean != null) {
                String sampleBottleNo = emcOrderBean.getExt$Item("samplebottleno").replace("，", ",").replace(" ", "");
                sampleBottleNo = sampleBottleNo.replaceAll(" ","");
                // 如果存在多个sample瓶编号，默认设置第一个瓶号
                if (!StringUtils.isEmpty(sampleBottleNo)) {
                    if(!StringUtils.isEmpty(task.getQcCategoryCode())){
                        if(sampleBottleNo.split(",").length>=3){
                            if(task.getQcCategoryCode().equals("N/A")){
                                task.setExt$Item("samplebottleno", sampleBottleNo.split(",")[0]);
                            }else if(task.getQcCategoryCode().equals("L-DUP")){
                                task.setExt$Item("samplebottleno", sampleBottleNo.split(",")[1]);
                            }else if(task.getQcCategoryCode().equals("L-SPKC")){
                                task.setExt$Item("samplebottleno", sampleBottleNo.split(",")[2]);
                            }else{
                                task.setExt$Item("samplebottleno", sampleBottleNo.split(",")[0]);
                            }
                        }else{
                            task.setExt$Item("samplebottleno", sampleBottleNo.split(",")[0]);
                        }

                    }
                }
            }
        });
        // sample 瓶号
        List<String> sampleBottleList = targetTaskList.stream()
                .filter(item -> !StringUtils.isEmpty(item.getExt$Item("samplebottleno")))
                .map(item -> item.getExt$Item("samplebottleno")).collect(Collectors.toList());

        getDao().update(targetTaskList, "SAMPLEBOTTLENO");

        this.updateTaskResultSampleEmptyWeight(targetTaskList, sampleBottleList);

    }

    @Override
    public Page<EmcOrderTaskBean> selectContainerInfo(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return MybatisPageHelper.get(rowBounds, () -> getDao().selectContainerInfo(parameter));
    }

    @Override
    public Page<EmcOrderTaskBean> selectTaskGroupByTest(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        List<EmcOrderTaskBean> orderTaskList = getDao().selectTaskGroupByTest(parameter);
        String sampleId = wrapper.getFilterValue("sampleId");

        List<EmcOrderTaskResultBean> resultList = orderTaskResultService.selectListByFilter(SearchFilter.instance()
                .match("SAMPLEID", sampleId).filter(MatchPattern.EQ)
                .match("SCRIPTFLAG", "0").filter(MatchPattern.EQ)
                .match("ANALYSISTYPE", "sampling").filter(MatchPattern.EQ));

        for (EmcOrderTaskBean orderTask : orderTaskList) {
            List<EmcOrderTaskResultBean> filterResultList = resultList.stream().filter(r -> ObjectUtils.equals(orderTask.getTestMethodId(), r.getTestMethodId())).collect(Collectors.toList());
            Long doneCount = filterResultList.stream().filter(a -> !StringUtils.isEmpty(a.getReportResult())).count();
            Long allCount = (long) filterResultList.size();
            if (doneCount.equals(allCount)) {
                orderTask.setExt$Item("progress", "√");
            } else {
                orderTask.setExt$Item("progress", doneCount + "/" + allCount);
            }
        }

        return MybatisPageHelper.get(rowBounds, () -> orderTaskList);
    }

    @Override
    public Page<EmcOrderTaskBean> selectByRunQc(String runId, RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskBeanList = this.selectListByFilter(SearchFilter.instance()
                .match("USECATEGORY", "lab").filter(MatchPattern.SEQ)
                .match("RUNID", runId).filter(MatchPattern.SEQ));
        Pagination<EmcOrderTaskBean> page = new Pagination<>();
        if (orderTaskBeanList != null && orderTaskBeanList.size() > 0) {
            List<Long> orderTaskIds = orderTaskBeanList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
            List<EmcOrderTaskResultBean> orderTaskResultBeanList = orderTaskResultService.selectListByFilter(SearchFilter.instance().match("ORDERTASKID", orderTaskIds).filter(MatchPattern.OR));
            Map<String, List<EmcOrderTaskResultBean>> resultListMap = orderTaskResultBeanList.stream().collect(Collectors.groupingBy(EmcOrderTaskResultBean::getQcCategoryCode));
            Map<String, List<EmcOrderTaskBean>> listMap = orderTaskBeanList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getQcCategoryCode));
            List<EmcOrderTaskBean> returnOrderTaskList = new ArrayList<>();
            listMap.forEach((k, v) -> {
                EmcOrderTaskBean otBean = new EmcOrderTaskBean();
                otBean.setExt$Item("qcnums", v.size() + "");
                List<Long> otids = v.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
                otBean.setExt$Item("otids", StringUtils.join(otids, ","));
                otBean.setQcCategoryCode(k);
                otBean.setQcName(v.get(0).getQcName());
                otBean.setQcCategoryId(v.get(0).getQcCategoryId());
                List<EmcOrderTaskResultBean> resultList = resultListMap.get(k);
                boolean notQualifiedFlag = resultList.stream().anyMatch(r -> "OOS".equals(r.getResultStatus()));
                if (notQualifiedFlag) {
                    otBean.setExt$Item("qualified", "0");
                } else {
                    otBean.setExt$Item("qualified", "1");
                }
                returnOrderTaskList.add(otBean);
            });
            page.setRows(returnOrderTaskList);
        }
        return page;
    }

    @Override
    public Page<EmcOrderTaskBean> selectOrderContainerInfo(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return MybatisPageHelper.get(rowBounds, () -> getDao().selectOrderContainerInfo(parameter));
    }

    @Override
    public Page<EmcOrderTaskBean> selectOrderTaskPagination(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return MybatisPageHelper.get(rowBounds, () -> getDao().selectOrderTaskPagination(parameter));
    }

    @Override
    @Transactional
    public void updateContainerInfo(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> targetTaskList = wrapper.parse(EmcOrderTaskBean.class, 0);
        EmcOrderTaskBean sourceTask = wrapper.parse(EmcOrderTaskBean.class, 1).get(0);

        // 查询是否已经存在有相同的容器
        EmcOrderContainerBean filterContainer = new EmcOrderContainerBean();
        filterContainer.setOrderId(targetTaskList.get(0).getOrderId());
        filterContainer.setContainerCategoryId(sourceTask.getContainerCategoryId());
        filterContainer.setContainerCategoryCode(sourceTask.getContainerCategoryCode());
        filterContainer.setContainerCategoryName(sourceTask.getContainerCategoryName());
        filterContainer.setContainerGroup(sourceTask.getContainerGroup());
        filterContainer.setFixative(sourceTask.getFixative());
        filterContainer.setStoreCondition(sourceTask.getStoreCondition());
        List<EmcOrderContainerBean> existedContainerList = orderContainerService.getDao().selectList(filterContainer, Collections.singletonList("id"));

        List<Long> targetOrderIdList = targetTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<Long> testMethodIdList = targetTaskList.stream().map(EmcOrderTaskBean::getTestMethodId).distinct().collect(Collectors.toList());
        targetTaskList = selectListByFilter(SearchFilter.instance().match("orderId", targetOrderIdList).filter(MatchPattern.OR).match("testMethodId", testMethodIdList).filter(MatchPattern.OR));

        // 如果已经存在，则直接更新
        if (!existedContainerList.isEmpty()) {
            Long orderContainerId = existedContainerList.get(0).getId();
            targetTaskList.forEach(task -> {
                task.setOrderContainerId(orderContainerId);
                task.setContainerCategoryId(sourceTask.getContainerCategoryId());
                task.setContainerCategoryCode(sourceTask.getContainerCategoryCode());
                task.setContainerCategoryName(sourceTask.getContainerCategoryName());
                task.setContainerGroup(sourceTask.getContainerGroup());
                task.setFixative(sourceTask.getFixative());
                task.setStoreCondition(sourceTask.getStoreCondition());
            });

            getDao().update(targetTaskList, Arrays.asList("orderContainerId", "ContainerCategoryId", "ContainerCategoryCode", "ContainerCategoryName", "ContainerGroup", "Fixative", "StoreCondition"));
            return;
        }

        // 如果容器不存在，则新建
        EmcOrderTaskBean basicTask = targetTaskList.get(0);
        List<EmcOrderContainerBean> orderContainerList = orderContainerService.getDao().selectListByOneColumnValue(basicTask.getOrderId(), "orderId");
        EmcOrderContainerBean maxOrderContainer = orderContainerList.stream().max(Comparator.comparing(EmcOrderContainerBean::getOrderContainerNo)).orElse(orderContainerList.get(0));
        String maxOrderContainerNo = maxOrderContainer.getOrderContainerNo();
        int maxSuffix = NumberUtils.parseInt(maxOrderContainerNo.substring(maxOrderContainerNo.length() - 3));
        String newOrderContainerNo = maxOrderContainerNo.substring(0, maxOrderContainerNo.length() - 3) + StringUtils.leftPad((maxSuffix + 1) + "", 3);

        EmcOrderContainerBean newContainer = new EmcOrderContainerBean();
        BeanUtils.copyProperties(maxOrderContainer, newContainer, PfConstant.CREATED_COLUMNS);
        newContainer.setId(ApplicationContextHelper.getNextIdentity());
        newContainer.setOrderContainerNo(newOrderContainerNo);
        newContainer.setOrderNum((long) orderContainerList.size() + 1);
        newContainer.setContainerCategoryId(sourceTask.getContainerCategoryId());
        newContainer.setContainerCategoryCode(sourceTask.getContainerCategoryCode());
        newContainer.setContainerCategoryName(sourceTask.getContainerCategoryName());
        newContainer.setContainerGroup(sourceTask.getContainerGroup());
        newContainer.setFixative(sourceTask.getFixative());
        newContainer.setStoreCondition(sourceTask.getStoreCondition());

        orderContainerService.getDao().insert(newContainer);

        // 更新测试中的容器ID
        targetTaskList.forEach(task -> {
            task.setOrderContainerId(newContainer.getId());
            task.setContainerCategoryId(sourceTask.getContainerCategoryId());
            task.setContainerCategoryCode(sourceTask.getContainerCategoryCode());
            task.setContainerCategoryName(sourceTask.getContainerCategoryName());
            task.setContainerGroup(sourceTask.getContainerGroup());
            task.setFixative(sourceTask.getFixative());
            task.setStoreCondition(sourceTask.getStoreCondition());
        });

        getDao().update(targetTaskList, Arrays.asList("orderContainerId", "ContainerCategoryId", "ContainerCategoryCode", "ContainerCategoryName", "ContainerGroup", "Fixative", "StoreCondition"));

    }

    @Override
    public void syncContainerInfo(RestJsonWrapperBean wrapper) {
        List<EmcOrderContainerBean> orderContainerList = wrapper.parse(EmcOrderContainerBean.class);
        List<Long> orderContainerIdList = orderContainerList.stream().map(EmcOrderContainerBean::getId).collect(Collectors.toList());

        List<EmcOrderTaskBean> orderTaskList = getDao().selectListByOneColumnValues(orderContainerIdList, "orderContainerId");
        List<Long> testMethodIdList = orderTaskList.stream().map(EmcOrderTaskBean::getTestMethodId).distinct().collect(Collectors.toList());
        List<String> monitorTmplIdList = orderTaskList.stream().map(EmcOrderTaskBean::getMonitorTmplId).distinct().collect(Collectors.toList());
        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");

        List<EmcTestMethodOrderCategoryBean> orderCategoryList = testMethodOrderCategoryService.selectListByFilter(SearchFilter.instance().match("testMethodId", testMethodIdList).filter(MatchPattern.OR).match("monitorTmplId", monitorTmplIdList).filter(MatchPattern.OR).match("orgId", parentOrgId).filter(MatchPattern.SEQ));

        orderTaskList.forEach(orderTask -> {
            EmcTestMethodOrderCategoryBean orderCategory = orderCategoryList.stream().filter(oc -> ObjectUtils.equals(orderTask.getTestMethodId(), oc.getTestMethodId())
                    && StringUtils.equals(orderTask.getMonitorTmplId(), oc.getMonitorTmplId())).findFirst().orElse(null);
            boolean empty = ObjectUtils.isEmpty(orderCategory);
            orderTask.setContainerCategoryCode(empty ? null : orderCategory.getExt$Item("containerCategoryCode"));
            orderTask.setContainerCategoryId(empty ? null : orderCategory.getContainerCategoryId());
            orderTask.setContainerCategoryName(empty ? null : orderCategory.getExt$Item("containerCategoryName"));
            orderTask.setContainerGroup(empty ? null : orderCategory.getContainerGroup());
            orderTask.setFixative(empty ? null : orderCategory.getFixative());
            orderTask.setStoreCondition(empty ? null : orderCategory.getStoreCondition());
        });

        List<EmcOrderContainerBean> existedOrderContainerList = orderContainerService.getDao().selectListByOneColumnValue(orderContainerList.get(0).getOrderId(), "orderId");
        EmcOrderContainerBean maxOrderContainer = existedOrderContainerList.stream().max(Comparator.comparing(EmcOrderContainerBean::getOrderContainerNo)).orElse(orderContainerList.get(0));
        String maxOrderContainerNo = maxOrderContainer.getOrderContainerNo();
        int maxSuffix = NumberUtils.parseInt(maxOrderContainerNo.substring(maxOrderContainerNo.length() - 3));
        AtomicInteger noSuffix = new AtomicInteger(maxSuffix);

        List<EmcOrderContainerBean> newOrderContainerList = new ArrayList<>();

        orderTaskList.forEach(ot -> {
            EmcOrderContainerBean existedOrderContainer = existedOrderContainerList.stream().filter(eoc -> ObjectUtils.equals(ot.getOrderId(), eoc.getOrderId())
                    && ObjectUtils.equals(ot.getContainerCategoryId(), eoc.getContainerCategoryId())
                    && StringUtils.equals(ot.getContainerCategoryCode(), eoc.getContainerCategoryCode())
                    && StringUtils.equals(ot.getContainerCategoryName(), eoc.getContainerCategoryName())
                    && StringUtils.equals(ot.getContainerGroup(), eoc.getContainerGroup())
                    && StringUtils.equals(ot.getFixative(), eoc.getFixative())
                    && StringUtils.equals(ot.getStoreCondition(), eoc.getStoreCondition())).findFirst().orElse(null);

            // 如果不存在容器，则需要新建
            if (ObjectUtils.isEmpty(existedOrderContainer)) {
                String newOrderContainerNo = maxOrderContainerNo.substring(0, maxOrderContainerNo.length() - 3) + StringUtils.leftPad((noSuffix.getAndIncrement() + 1) + "", 3);
                EmcOrderContainerBean newContainer = new EmcOrderContainerBean();
                BeanUtils.copyProperties(maxOrderContainer, newContainer, PfConstant.CREATED_COLUMNS);
                newContainer.setId(ApplicationContextHelper.getNextIdentity());
                newContainer.setOrderContainerNo(newOrderContainerNo);
                newContainer.setOrderNum((long) existedOrderContainerList.size() + 1);
                newContainer.setContainerCategoryId(ot.getContainerCategoryId());
                newContainer.setContainerCategoryCode(ot.getContainerCategoryCode());
                newContainer.setContainerCategoryName(ot.getContainerCategoryName());
                newContainer.setContainerGroup(ot.getContainerGroup());
                newContainer.setFixative(ot.getFixative());
                newContainer.setStoreCondition(ot.getStoreCondition());

                newOrderContainerList.add(newContainer);
                ot.setOrderContainerId(newContainer.getId());
            } else {
                ot.setOrderContainerId(existedOrderContainer.getId());
            }
        });

        List<Long> updateOrderContainerIdList = orderTaskList.stream().map(EmcOrderTaskBean::getOrderContainerId).distinct().collect(Collectors.toList());
        List<Long> needDeleteOrderContainerIdList = orderContainerIdList.stream().filter(id -> !updateOrderContainerIdList.contains(id)).collect(Collectors.toList());

        orderContainerService.getDao().deleteByIdList(needDeleteOrderContainerIdList);
        orderContainerService.getDao().insert(newOrderContainerList);
        getDao().update(orderTaskList, Arrays.asList("orderContainerId", "ContainerCategoryId", "ContainerCategoryCode", "ContainerCategoryName", "ContainerGroup", "Fixative", "StoreCondition"));
    }

    @Override
    public void syncOrderContainerInfo(RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        if (orderList.isEmpty()) {
            return;
        }

        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> relatedOrderTaskList = getDao().selectListByOneColumnValues(orderIdList, "ORDERID", Arrays.asList("ID", "ORDERID", "TESTMETHODID", "MONITORTMPLID", "ORDERCONTAINERID"));
        syncContainerInfo(relatedOrderTaskList);
    }

    @Override
    public void syncContainerInfo(List<EmcOrderTaskBean> orderTaskList) {
        List<Long> orderContainerIdList = orderTaskList.stream().map(EmcOrderTaskBean::getOrderContainerId).distinct().collect(Collectors.toList());
        List<EmcOrderContainerBean> existedOrderContainerList = orderContainerService.getDao().selectListByIds(orderContainerIdList);

        List<Long> testMethodIdList = orderTaskList.stream().map(EmcOrderTaskBean::getTestMethodId).distinct().collect(Collectors.toList());
        List<String> monitorTmplIdList = orderTaskList.stream().map(EmcOrderTaskBean::getMonitorTmplId).distinct().collect(Collectors.toList());
        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");

        List<EmcTestMethodOrderCategoryBean> orderCategoryList = testMethodOrderCategoryService.selectListByFilter(SearchFilter.instance().match("testMethodId", testMethodIdList).filter(MatchPattern.OR).match("monitorTmplId", monitorTmplIdList).filter(MatchPattern.OR).match("orgId", parentOrgId).filter(MatchPattern.SEQ));

        orderTaskList.forEach(orderTask -> {
            EmcTestMethodOrderCategoryBean orderCategory = orderCategoryList.stream().filter(oc -> ObjectUtils.equals(orderTask.getTestMethodId(), oc.getTestMethodId())
                    && StringUtils.equals(orderTask.getMonitorTmplId(), oc.getMonitorTmplId())).findFirst().orElse(null);
            boolean empty = ObjectUtils.isEmpty(orderCategory);
            orderTask.setContainerCategoryCode(empty ? null : orderCategory.getExt$Item("containerCategoryCode"));
            orderTask.setContainerCategoryId(empty ? null : orderCategory.getContainerCategoryId());
            orderTask.setContainerCategoryName(empty ? null : orderCategory.getExt$Item("containerCategoryName"));
            orderTask.setContainerGroup(empty ? null : orderCategory.getContainerGroup());
            orderTask.setFixative(empty ? null : orderCategory.getFixative());
            orderTask.setStoreCondition(empty ? null : orderCategory.getStoreCondition());
        });

        // 按样品分组
        Map<Long, List<EmcOrderContainerBean>> existedOrderContainerMap = existedOrderContainerList.stream().collect(Collectors.groupingBy(EmcOrderContainerBean::getOrderId));
        Map<Long, Integer> maxOrderContainerNoSuffixMap = new HashMap<>(existedOrderContainerMap.size());
        Map<Long, EmcOrderContainerBean> maxOrderContainerMap = new HashMap<>(existedOrderContainerMap.size());
        existedOrderContainerMap.forEach((orderId, curOrderContainerList) -> {
            EmcOrderContainerBean maxOrderContainer = curOrderContainerList.stream().max(Comparator.comparing(EmcOrderContainerBean::getOrderContainerNo)).orElse(curOrderContainerList.get(0));
            String maxOrderContainerNo = maxOrderContainer.getOrderContainerNo();
            int maxSuffix = NumberUtils.parseInt(maxOrderContainerNo.substring(maxOrderContainerNo.length() - 3));
            maxOrderContainerNoSuffixMap.put(orderId, maxSuffix);
            maxOrderContainerMap.put(orderId, maxOrderContainer);
        });

        List<EmcOrderContainerBean> newOrderContainerList = new ArrayList<>();

        Map<Long, List<EmcOrderTaskBean>> orderTaskMap = orderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getOrderId));
        orderTaskMap.forEach((orderId, curTaskList) -> curTaskList.forEach(ot -> {
            EmcOrderContainerBean existedOrderContainer = existedOrderContainerList.stream().filter(eoc -> ObjectUtils.equals(ot.getOrderId(), eoc.getOrderId())
                    && ObjectUtils.equals(ot.getContainerCategoryId(), eoc.getContainerCategoryId())
                    && StringUtils.equals(ot.getContainerCategoryCode(), eoc.getContainerCategoryCode())
                    && StringUtils.equals(ot.getContainerCategoryName(), eoc.getContainerCategoryName())
                    && StringUtils.equals(ot.getContainerGroup(), eoc.getContainerGroup())
                    && StringUtils.equals(ot.getFixative(), eoc.getFixative())
                    && StringUtils.equals(ot.getStoreCondition(), eoc.getStoreCondition())).findFirst().orElse(null);

            // 如果不存在容器，则需要新建
            if (ObjectUtils.isEmpty(existedOrderContainer)) {
                EmcOrderContainerBean maxOrderContainer = maxOrderContainerMap.get(orderId);
                String maxOrderContainerNo = maxOrderContainer.getOrderContainerNo();
                int newSuffix = Optional.ofNullable(maxOrderContainerNoSuffixMap.get(orderId)).orElse(0) + 1;
                maxOrderContainerNoSuffixMap.put(orderId, newSuffix);

                String newOrderContainerNo = maxOrderContainerNo.substring(0, maxOrderContainerNo.length() - 3) + StringUtils.leftPad(newSuffix + "", 3);
                EmcOrderContainerBean newContainer = new EmcOrderContainerBean();
                BeanUtils.copyProperties(maxOrderContainer, newContainer, PfConstant.CREATED_COLUMNS);
                newContainer.setId(ApplicationContextHelper.getNextIdentity());
                newContainer.setOrderContainerNo(newOrderContainerNo);
                newContainer.setOrderNum((long) existedOrderContainerList.size() + 1);
                newContainer.setContainerCategoryId(ot.getContainerCategoryId());
                newContainer.setContainerCategoryCode(ot.getContainerCategoryCode());
                newContainer.setContainerCategoryName(ot.getContainerCategoryName());
                newContainer.setContainerGroup(ot.getContainerGroup());
                newContainer.setFixative(ot.getFixative());
                newContainer.setStoreCondition(ot.getStoreCondition());

                newOrderContainerList.add(newContainer);
                ot.setOrderContainerId(newContainer.getId());
            } else {
                ot.setOrderContainerId(existedOrderContainer.getId());
            }
        }));

        List<Long> updateOrderContainerIdList = orderTaskList.stream().map(EmcOrderTaskBean::getOrderContainerId).distinct().collect(Collectors.toList());
        List<Long> needDeleteOrderContainerIdList = orderContainerIdList.stream().filter(id -> !updateOrderContainerIdList.contains(id)).collect(Collectors.toList());

        orderContainerService.getDao().deleteByIdList(needDeleteOrderContainerIdList);
        orderContainerService.getDao().insert(newOrderContainerList);
        getDao().update(orderTaskList, Arrays.asList("orderContainerId", "ContainerCategoryId", "ContainerCategoryCode", "ContainerCategoryName", "ContainerGroup", "Fixative", "StoreCondition"));
    }

    /**
     * 如果样品测试的样品测试结果的分析项别名包含 ‘采样瓶重量’ ，将sample称重的空瓶重量填入到样品测试结果中
     * @param targetTaskList
     * @param sampleBottleList
     * @return
     */
    protected List<EmcOrderTaskResultBean> updateTaskResultSampleEmptyWeight (List<EmcOrderTaskBean> targetTaskList,
                                                                              List<String> sampleBottleList) {

        List<EmcSampleBottleBean> sampleWeightingList = sampleBottleService.selectListByFilter(SearchFilter.instance().match("sampleBottleNo", sampleBottleList).filter(MatchPattern.OR));
        List<EmcOrderTaskResultBean> updateOrderTaskResultList = new ArrayList<>();
        if (!sampleBottleList.isEmpty()) {
            List<Long> orderTaskIdList = targetTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());

            List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.selectListByFilter(SearchFilter.instance().match("orderTaskId", orderTaskIdList).filter(MatchPattern.OR)
                    .match("itemAliasName", "采样瓶称重").filter(MatchPattern.EQ));

            orderTaskResultList.forEach(result -> {
                targetTaskList.stream().filter(task -> task.getId().equals(result.getOrderTaskId())).findFirst().ifPresent( curOrderTask -> {
                    String samplebottle = curOrderTask.getExt$Item("samplebottleno");
                    if (!StringUtils.isEmpty(samplebottle)) {
                        Optional<EmcSampleBottleBean> sampleWeighting = sampleWeightingList.stream().filter(sample -> StringUtils.equals(sample.getSampleBottleNo(), samplebottle)).findFirst();
                        sampleWeighting.ifPresent(item -> {
                            result.setOriginalResult(item.getEmptyBottleWeight());
                            updateOrderTaskResultList.add(result);
                        });
                    }
                });

            });

        }

        orderTaskResultService.getDao().update(updateOrderTaskResultList, "ORIGINALRESULT");
        if (!updateOrderTaskResultList.isEmpty()) {
            List<Long> resultIdList = updateOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
            EmcOrderTaskResultComputingEngine.getEngine(resultIdList).compute();
        }
        return updateOrderTaskResultList;

    }
}
