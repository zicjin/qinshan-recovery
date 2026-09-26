package com.sunwayworld.emc.module.bp.bp.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.sunwayworld.emc.module.bp.bp.service.PfFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderContainerService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskLogService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.PfOrderService;
import com.sunwayworld.emc.module.bp.bp.service.PfOrderTaskService;
import com.sunwayworld.emc.module.bp.bp.validator.PfDeleteItemGroupValidator;
import com.sunwayworld.emc.module.bp.bp.validator.PfDeleteCheckProjectOrderItemGroupValidator;
import com.sunwayworld.emc.module.bp.bp.validator.PfDeleteOrderItemGroupValidator;
import com.sunwayworld.emc.module.bp.bp.validator.PfEditOrderQcValidator;
import com.sunwayworld.emc.module.bp.bp.validator.PfSubpackageTestValidator;
import com.sunwayworld.emc.module.bp.bp.validator.PfSyncNewOrderItemGroupValidator;
import com.sunwayworld.emc.module.bp.bp.validator.PfSyncNewOrderValidator;
import com.sunwayworld.emc.module.bp.order.orderlabel.bean.EmcOrderLabelBean;
import com.sunwayworld.emc.module.bp.order.orderlabel.service.EmcOrderLabelService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.project.projectlog.bean.EmcProjectEditLogBean;
import com.sunwayworld.emc.module.bp.project.projectlog.service.EmcProjectEditLogService;
import com.sunwayworld.emc.module.bp.project.projscheme.service.PfProjSchemeService;
import com.sunwayworld.emc.module.bp.project.projscheme.validator.PfDeleteOrderValidator;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportService;
import com.sunwayworld.emc.module.mdm.basemdm.containercategory.service.EmcContainerCategoryService;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.bean.EmcIndexTestItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.service.EmcIndexTestItemService;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.bean.EmcSubcontractRecordBean;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.service.EmcSubcontractRecordService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcTestCapacityBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcTestCapacityService;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcOrderIndexBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcIndexSetService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorTmplService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcRelatedTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcRelatedTestMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.exception.UnsupportedException;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
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
public class PfOrderServiceImpl extends EmcOrderServiceImpl implements PfOrderService {

    private static final Logger logger = LogManager.getLogger(PfOrderServiceImpl.class);

    @Autowired
    @Lazy
    private EmcProjectService projectService;

    @Autowired
    private EmcOrderItemService orderItemService;

    @Autowired
    @Lazy
    private PfFolderService folderService;

    @Autowired
    private EmcOrderItemGroupService orderItemGroupService;

    @Autowired
    @Lazy
    private PfOrderTaskService orderTaskService;

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Autowired
    private EmcOrderTaskLogService orderTaskLogService;

    @Autowired
    private EmcContainerCategoryService containerCategoryService;

    @Autowired
    @Lazy
    private PfProjSchemeService pfProjSchemeService;

    @Autowired
    private EmcMonitorTmplService monitorTmplService;

    @Autowired
    private EmcItemService itemService;

    @Autowired
    private EmcOrderLabelService orderLabelService;

    @Autowired
    private EmcTestMethodService testMethodService;

    @Autowired
    private EmcRelatedTestMethodService relatedTestMethodService;

    @Autowired
    private EmcCapacityItemGroupService capacityItemGroupService;

    @Autowired
    private EmcTestCapacityService testCapacityService;

    @Autowired
    private EmcGroupItemService groupItemService;

    @Autowired
    @Lazy
    private EmcReportService reportService;

    @Autowired
    private EmcSubcontractRecordService subcontractRecordService;

    @Autowired
    @Lazy
    private EmcProjectEditLogService projectEditLogService;

    @Autowired
    private EmcOrderContainerService orderContainerService;

    @Autowired
    private EmcIndexSetService indexSetService;

    @Autowired
    private EmcIndexTestItemService indexTestItemService;

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long projectId, RestJsonWrapperBean wrapper) {
        EmcOrderBean order = wrapper.parse(EmcOrderBean.class).get(0);
        order.setProjId(projectId);

        EmcFolderBean folder = folderService.insertByOrder(order);

        order.setId(ApplicationContextHelper.getNextIdentity());
        order.setFolderId(folder.getId());
        order.setOrderNo(folder.getFolderNo() + "001");
        if (order.getLabelNo() == null) {
            order.setLabelNo(order.getOrderNo());
        }
        order.setSendDate(LocalDate.now());
        order.setSendTime(DateTimeUtils.formatLocalDateTime(LocalDateTime.now(), "HH:mm"));
        order.setSenderId(LocalContextHelper.getLoginUserId());
        order.setExt$Item("sender", LocalContextHelper.getLoginUserName());
        order.setCycle(EmcConstant.ONE);
        order.setFrequency(EmcConstant.ONE);
        order.setOrderNum(Long.parseLong(folder.getFolderNo().substring(folder.getFolderNo().length()-3)));
        order.setTermination(Constant.NO);
        getDao().insert(order);

        List<EmcOrderItemGroupBean> orderItemGroupList = wrapper.parse(EmcOrderItemGroupBean.class);

        // 委托送样默认容器类型，针对送样的任务，默认设置固定的容器信息
        orderItemGroupList.forEach(itemGroup -> {
            // 容器组为999，固定剂为/
            itemGroup.setContainerGroup("999");
            itemGroup.setFixative("/");
            itemGroup.setStoreCondition("/");
        });

        // 增加关联测试，如果新增的测试有关联测试，则自动增加
        List<Long> orderItemGroupIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean::getId).collect(Collectors.toList());
        List<Long> testMethodIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean::getTestMethodId).distinct().collect(Collectors.toList());
        List<EmcRelatedTestMethodBean> relatedTestMethodList = relatedTestMethodService.selectListByFilter(
                SearchFilter.instance().match("testMethodId", testMethodIdList).filter(MatchPattern.OR)
                        .match("replaceFlag2", "1").filter(MatchPattern.DIFFER), new Order[0]);
        List<Long> relatedTestMethodIdList = relatedTestMethodList.stream().map(EmcRelatedTestMethodBean::getRelatedTestMethodId).distinct().collect(Collectors.toList());
        if (!relatedTestMethodIdList.isEmpty()) {
            List<EmcTestMethodBean> testMethodList = testMethodService.selectListByFilter(SearchFilter.instance().match("id", relatedTestMethodIdList).filter(MatchPattern.OR));

            String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
            List<EmcTestCapacityBean> relatedCapacityList = testCapacityService.selectListByFilter(SearchFilter.instance().match("testMethodId", relatedTestMethodIdList).filter(MatchPattern.OR).match("orgId", parentOrgId).filter(MatchPattern.SEQ));
            List<Long> testCapacityIdList = relatedCapacityList.stream().map(EmcTestCapacityBean::getId).collect(Collectors.toList());
            Map<Long, EmcTestCapacityBean> relatedCapacityMap = relatedCapacityList.stream().collect(Collectors.toMap(EmcTestCapacityBean::getId, Function.identity()));

            List<EmcCapacityItemGroupBean> relatedItemGroupList = capacityItemGroupService.getDao().selectListByOneColumnValues(testCapacityIdList, "testCapacityId");
            List<EmcCapacityItemGroupBean> newRelatedItemGroupList = relatedItemGroupList.stream().filter(itemGroup -> !orderItemGroupIdList.contains(itemGroup.getId())).collect(Collectors.toList());

            // 判定关联的测试是否是现场测试，如果为现场测试，则不添加
            List<Long> newItemGroupIdList = newRelatedItemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
            List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(newItemGroupIdList, "itemGroupId");
            List<Long> itemIdList = groupItemList.stream().map(EmcGroupItemBean::getItemId).distinct().collect(Collectors.toList());
            List<EmcItemBean> itemList = itemService.getDao().selectListByOneColumnValues(itemIdList, "id", Arrays.asList("id", "analysisCategory"));
            List<Long> validCapacityItemGroupIdList = new ArrayList<>();
            newRelatedItemGroupList.forEach(group -> {
                List<Long> curItemIdList = groupItemList.stream().filter(groupItem -> ObjectUtils.equals(group.getItemGroupId(), groupItem.getItemGroupId())).map(EmcGroupItemBean::getItemId).distinct().collect(Collectors.toList());
                boolean allSamplingItem = itemList.stream().filter(item -> curItemIdList.contains(item.getId())).allMatch(item -> StringUtils.equals(item.getAnalysisCategory(), "sampling"));
                if (!allSamplingItem) {
                    validCapacityItemGroupIdList.add(group.getId());
                }
            });
            List<EmcCapacityItemGroupBean> validCapacityItemGroupList = newRelatedItemGroupList.stream().filter(group -> validCapacityItemGroupIdList.contains(group.getId())).collect(Collectors.toList());

            validCapacityItemGroupList.forEach(related -> {
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

                Optional.ofNullable(relatedCapacityMap.get(related.getTestCapacityId())).ifPresent(capacity -> {
                    relatedOrderItemGroup.setAnalysisOrgId(capacity.getExt$Item("analysisOrgId"));
                    relatedOrderItemGroup.setAnalysisOrgName(capacity.getExt$Item("analysisOrgName"));
                    relatedOrderItemGroup.setInputUserId(capacity.getExt$Item("inputUserId"));
                    relatedOrderItemGroup.setInputUserName(capacity.getExt$Item("inputUserName"));
                });
                relatedOrderItemGroup.setExt$Item("relatedTest", Constant.YES);

                orderItemGroupList.add(relatedOrderItemGroup);
            });
        }
        for (int i = 0; i < orderItemGroupList.size(); i++) {
            orderItemGroupList.get(i).setSortNum(i + 1L);
        }
        orderItemGroupService.insert(Collections.singletonList(order), orderItemGroupList);

        return order.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copy(RestJsonWrapperBean jsonWrapper) {
        List<EmcOrderBean> oldOrderList = jsonWrapper.parse(EmcOrderBean.class);
        int copies = NumberUtils.parseInt(jsonWrapper.getParamValue("copies"));
        String operateType = jsonWrapper.getParamValue("operateType");
        boolean schemeEdit = StringUtils.equals(operateType, "schemeEdit");

        List<Long> oldOrderIdList = oldOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> oldOrderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(oldOrderIdList, "orderId");
        List<EmcOrderItemBean> oldOrderItemList = orderItemService.getDao().selectListByOneColumnValues(oldOrderIdList, "orderId");

        List<EmcOrderBean> newOrderList = new ArrayList<>();
        List<EmcOrderItemGroupBean> newOrderItemGroupList = new ArrayList<>();
        List<EmcOrderItemBean> newOrderItemList = new ArrayList<>();

        List<Long> folderIdList = oldOrderList.stream().map(EmcOrderBean::getFolderId).distinct().collect(Collectors.toList());
        List<EmcFolderBean> oldFolderList = folderService.getDao().selectListByOneColumnValues(folderIdList, "ID");

        Long projId = oldOrderList.get(0).getProjId();
        EmcProjectBean project = projectService.selectById(projId);

        List<String> monitorTmplIdList = oldOrderList.stream().map(EmcOrderBean::getMonitorTmplId).distinct().collect(Collectors.toList());
        List<EmcMonitorTmplBean> monitorTmplList = monitorTmplService.selectListByIds(monitorTmplIdList);

        Map<String, String> orderCategoryCodeMap = monitorTmplList.stream().collect(HashMap::new, (map, item) -> map.put(item.getMonitorTmplName(), item.getExt$Item("orderCategoryCode")), HashMap::putAll);

        List<EmcFolderBean> insertFolderList = new ArrayList<>();
        Map<String, List<EmcOrderBean>> orderListMap = oldOrderList.stream().collect(Collectors.groupingBy(EmcOrderBean::getOrderCategory));
        orderListMap.forEach((orderCategory, curOrderList) -> {
            List<String> curNewFolderNoList = generateFolderNosByProjId(project.getId(), project.getProjNo(), curOrderList.size() * copies);
            List<Long> curFolderIdList = curOrderList.stream().map(EmcOrderBean::getFolderId).distinct().collect(Collectors.toList());
            List<EmcFolderBean> curFolderList = oldFolderList.stream().filter(f -> curFolderIdList.contains(f.getId())).collect(Collectors.toList());
            int curFolderCount = curFolderList.size();
            IntStream.range(0, copies).forEach(copyIndex -> {
                IntStream.range(0, curFolderList.size()).forEach(folderIndex -> {
                    // 点位
                    EmcFolderBean oldFolder = curFolderList.get(folderIndex);
                    EmcFolderBean insertFolder = new EmcFolderBean();
                    BeanUtils.copyProperties(oldFolder, insertFolder);
                    Long newFolderId = ApplicationContextHelper.getNextIdentity();
                    String newFolderNo = curNewFolderNoList.get(copyIndex * curFolderCount + folderIndex);
                    insertFolder.setId(newFolderId);
                    insertFolder.setFolderNo(newFolderNo);
                    insertFolderList.add(insertFolder);

                    // 样品
                    EmcOrderBean oldOrder = curOrderList.stream().filter(e -> ObjectUtils.equals(oldFolder.getId(), e.getFolderId())).findFirst().orElseGet(EmcOrderBean::new);
                    EmcOrderBean newOrder = new EmcOrderBean();
                    BeanUtils.copyProperties(oldOrder, newOrder);
                    Long newOrderId = ApplicationContextHelper.getNextIdentity();
                    newOrder.setId(newOrderId);
                    String newOrderNo = newFolderNo + "001";
                    newOrder.setOrderNo(newOrderNo);
                    newOrder.setLabelNo(newOrderNo);
                    newOrder.setOrderNum(Long.parseLong(newFolderNo.substring(newFolderNo.length() - 3)));
                    newOrder.setFolderId(newFolderId);
                    newOrder.setExt$Item("syncStatus", schemeEdit ? Constant.YES : null);
                    newOrder.setSource(schemeEdit ? "schemeEdit" : oldOrder.getSource());
                    newOrder.setSampleId(schemeEdit ? null : oldOrder.getSampleId());
                    newOrderList.add(newOrder);

                    oldOrderItemGroupList.stream().filter(t -> ObjectUtils.equals(t.getOrderId(), oldOrder.getId())).forEach(t -> {
                        Long newOrderItemGroupId = ApplicationContextHelper.getNextIdentity();
                        oldOrderItemList.stream().filter(r -> ObjectUtils.equals(r.getOrderItemGroupId(), t.getId())).forEach(r -> {
                            EmcOrderItemBean newOrderItem = new EmcOrderItemBean();
                            BeanUtils.copyProperties(r, newOrderItem);
                            newOrderItem.setId(ApplicationContextHelper.getNextIdentity());
                            newOrderItem.setOrderId(newOrderId);
                            newOrderItem.setOrderItemGroupId(newOrderItemGroupId);
                            newOrderItemList.add(newOrderItem);
                        });

                        EmcOrderItemGroupBean newOrderItemGroup = new EmcOrderItemGroupBean();
                        BeanUtils.copyProperties(t, newOrderItemGroup);
                        newOrderItemGroup.setId(newOrderItemGroupId);
                        newOrderItemGroup.setOrderId(newOrderId);
                        newOrderItemGroup.setExt$Item("syncStatus", schemeEdit ? Constant.YES : null);
                        newOrderItemGroup.setExt$Item("source", schemeEdit ? "schemeEdit" : null);
                        newOrderItemGroupList.add(newOrderItemGroup);
                    });
                });
            });
        });

        if (newOrderItemList.size() > 100000) {
            logger.warn("复制样品{}出现问题，数据异常 -> {}", project.getProjNo(), newOrderItemList.size());
            throw new UnsupportedException("数据异常");
        }

        folderService.getDao().insert(insertFolderList);
        getDao().insert(newOrderList);
        orderItemGroupService.getDao().insert(newOrderItemGroupList);
        orderItemService.getDao().insert(newOrderItemList);
    }

    public List<String> generateFolderNosByProjId(Long projId, String projectNo, int num) {
        List<EmcFolderBean> schemeOfProjectList = folderService.getDao().selectListByOneColumnValue(projId, "PROJID");
        List<String> newFolderNoList = new ArrayList<>();
        String firstFolderNo = "";
        if (schemeOfProjectList.isEmpty()) {
            firstFolderNo = projectNo + "001";
            newFolderNoList.add(firstFolderNo);

        } else {

            String maxFolderNo = schemeOfProjectList.stream().max(Comparator.comparing(EmcFolderBean::getFolderNo)).get().getFolderNo();
            firstFolderNo = maxFolderNo.substring(0, maxFolderNo.length() - 3) + String.format("%03d", Long.parseLong(maxFolderNo.substring(maxFolderNo.length() - 3)) + 1);
            newFolderNoList.add(firstFolderNo);
        }

        if (num > 1) {
            for (int i = 0; i < num - 1; i++) {
                String beforeFolderNo = newFolderNoList.get(i);
                String nextFolderNo = beforeFolderNo.substring(0, beforeFolderNo.length() - 3) + String.format("%03d", Long.parseLong(beforeFolderNo.substring(beforeFolderNo.length() - 3)) + 1);
                newFolderNoList.add(nextFolderNo);
            }
        }

        return newFolderNoList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copyQc(RestJsonWrapperBean jsonWrapper) {
        List<EmcOrderBean> oldOrderList = jsonWrapper.parse(EmcOrderBean.class);
        if (oldOrderList.isEmpty()) {
            return;
        }

        String operateType = oldOrderList.get(0).getExt$Item("operateType");
        boolean schemeEdit = StringUtils.equals(operateType, "schemeEdit");

        List<Long> oldOrderIdList = oldOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> oldOrderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(oldOrderIdList, "orderId");
        List<EmcOrderItemBean> oldOrderItemList = orderItemService.getDao().selectListByOneColumnValues(oldOrderIdList, "orderId");

        List<EmcItemBean> itemList = itemService.selectListByIds(oldOrderItemList.stream().map(EmcOrderItemBean::getItemId).distinct().collect(Collectors.toList()));

        List<EmcOrderBean> newOrderList = new ArrayList<>();
        List<EmcOrderItemGroupBean> newOrderItemGroupList = new ArrayList<>();
        List<EmcOrderItemBean> newOrderItemList = new ArrayList<>();

        List<Long> folderIdList = oldOrderList.stream().map(EmcOrderBean::getFolderId).distinct().collect(Collectors.toList());
        List<EmcFolderBean> oldFolderList = folderService.getDao().selectListByOneColumnValues(folderIdList, "ID");

        Long projId = oldOrderList.get(0).getProjId();
        EmcProjectBean project = projectService.selectById(projId);

        List<String> monitorTmplIdList = oldOrderList.stream().map(EmcOrderBean::getMonitorTmplId).distinct().collect(Collectors.toList());
        List<EmcMonitorTmplBean> monitorTmplList = monitorTmplService.selectListByIds(monitorTmplIdList);

        Map<String, String> orderCategoryCodeMap = monitorTmplList.stream().collect(HashMap::new, (map, item) -> map.put(item.getMonitorTmplName(), item.getExt$Item("orderCategoryCode")), HashMap::putAll);
        Map<String, List<EmcOrderBean>> orderListMap = oldOrderList.stream().collect(Collectors.groupingBy(EmcOrderBean::getOrderCategory));

        List<EmcFolderBean> insertFolderList = new ArrayList<>();
        orderListMap.forEach((orderCategory, curOrderList) -> {
            List<String> curNewFolderNoList = pfProjSchemeService.generateFolderNos(project, orderCategoryCodeMap.get(orderCategory), curOrderList.size());
            List<Long> curFolderIdList = curOrderList.stream().map(EmcOrderBean::getFolderId).distinct().collect(Collectors.toList());
            List<EmcFolderBean> curFolderList = oldFolderList.stream().filter(f -> curFolderIdList.contains(f.getId())).collect(Collectors.toList());
            int curFolderCount = curFolderList.size();
            IntStream.range(0, curFolderCount).forEach(folderIndex -> {
                // 点位
                EmcFolderBean oldFolder = curFolderList.get(folderIndex);
                EmcFolderBean insertFolder = new EmcFolderBean();
                BeanUtils.copyProperties(oldFolder, insertFolder);
                Long newFolderId = ApplicationContextHelper.getNextIdentity();
                String newFolderNo = curNewFolderNoList.get(folderIndex);
                insertFolder.setId(newFolderId);
                insertFolder.setFolderNo(newFolderNo);
                insertFolderList.add(insertFolder);

                // 样品
                EmcOrderBean newOrder = new EmcOrderBean();
                EmcOrderBean oldOrder = curOrderList.stream().filter(e -> ObjectUtils.equals(oldFolder.getId(), e.getFolderId())).findFirst().orElseGet(EmcOrderBean::new);
                String oldOrderNo = oldOrder.getOrderNo();
                Long newOrderId = ApplicationContextHelper.getNextIdentity();

                List<EmcOrderItemBean> curOrderItemList = new ArrayList<>();
                oldOrderItemGroupList.stream().filter(t -> ObjectUtils.equals(t.getOrderId(), oldOrder.getId())).forEach(t -> {
                    curOrderItemList.clear();
                    Long newOrderItemGroupId = ApplicationContextHelper.getNextIdentity();

                    oldOrderItemList.stream().filter(oldOrderItem -> ObjectUtils.equals(oldOrderItem.getOrderItemGroupId(), t.getId())).forEach(oldOrderItem -> {
                        EmcItemBean item = itemList.stream().filter(e -> ObjectUtils.equals(e.getId(), oldOrderItem.getItemId())).findFirst().orElseGet(EmcItemBean::new);

                        if (!StringUtils.isEmpty(item.getQcCategoryId()) && ArrayUtils.contains(StringUtils.split(item.getQcCategoryId(), ","), "" + oldOrder.getQcCategoryId())) {
                            EmcOrderItemBean newOrderItem = new EmcOrderItemBean();
                            BeanUtils.copyProperties(oldOrderItem, newOrderItem);
                            newOrderItem.setId(ApplicationContextHelper.getNextIdentity());
                            newOrderItem.setOrderId(newOrderId);
                            newOrderItem.setOrderItemGroupId(newOrderItemGroupId);
                            curOrderItemList.add(newOrderItem);
                        }
                    });

                    if (!curOrderItemList.isEmpty()) {
                        newOrderItemList.addAll(curOrderItemList);

                        EmcOrderItemGroupBean newOrderItemGroup = new EmcOrderItemGroupBean();
                        BeanUtils.copyProperties(t, newOrderItemGroup);

                        newOrderItemGroup.setId(newOrderItemGroupId);
                        newOrderItemGroup.setOrderId(newOrderId);
                        newOrderItemGroup.setQcCategoryId(oldOrder.getQcCategoryId());
                        newOrderItemGroup.setQcCategoryCode(oldOrder.getQcCategoryCode());
                        newOrderItemGroup.setQcName(oldOrder.getQcName());
                        newOrderItemGroup.setExt$Item("syncStatus", schemeEdit ? Constant.YES : null);
                        newOrderItemGroup.setExt$Item("source", schemeEdit ? "schemeEdit" : null);
                        newOrderItemGroupList.add(newOrderItemGroup);
                    }
                });

                BeanUtils.copyProperties(oldOrder, newOrder);
                newOrder.setFolderId(newFolderId);
                newOrder.setId(newOrderId);
                newOrder.setOrderNo(newFolderNo + "01");
                newOrder.setLabelNo(newFolderNo + "01");
                newOrder.setParentOrderNo(oldOrderNo);
                newOrder.setExt$Item("syncStatus", schemeEdit ? Constant.YES : null);
                newOrder.setSource(schemeEdit ? "schemeEdit" : oldOrder.getSource());
                newOrder.setSampleId(schemeEdit ? null : oldOrder.getSampleId());
                newOrderList.add(newOrder);
            });
        });

        if (!newOrderItemGroupList.isEmpty()) {
            folderService.getDao().insert(insertFolderList);
            getDao().insert(newOrderList);
            orderItemGroupService.getDao().insert(newOrderItemGroupList);
            orderItemService.getDao().insert(newOrderItemList);
        }
    }

    @Override
    public void copyOrders(RestJsonWrapperBean wrapper) {
        EmcOrderBean order = wrapper.parseUnique(EmcOrderBean.class);
        // 查询样品信息
        order = getDao().selectById(order.getId());

        //获取样品周期字符
        String orderNo = order.getOrderNo();
        String cl = orderNo.substring(orderNo.length()-4,orderNo.length()-3);
        EmcFolderBean folder = folderService.selectById(order.getFolderId());
        List<EmcOrderBean> orderList = getDao().selectListByOneColumnValue(order.getFolderId(), "FOLDERID");
        List<EmcOrderBean> filterOrderList = orderList.stream().filter(o -> o.getOrderNo().contains(cl)).collect(Collectors.toList());
        String maxOrderNo = filterOrderList.stream().max(Comparator.comparing(EmcOrderBean::getOrderNo)).map(EmcOrderBean::getOrderNo).orElse(null);

        String newOrderNo = StringUtils.isEmpty(maxOrderNo) ? folder.getFolderNo() + "001" : folder.getFolderNo() + EmcBizConstant.NUMFORENG[Integer.parseInt(order.getCycle().toString())-1] + StringUtils.leftPad((NumberUtils.parseInt(maxOrderNo.substring(folder.getFolderNo().length()+1)) + 1) + "", 3);

        EmcOrderBean newOrder = new EmcOrderBean();
        BeanUtils.copyProperties(order, newOrder);

        Long newOrderId = ApplicationContextHelper.getNextIdentity();
        newOrder.setId(newOrderId);
        newOrder.setOrderNo(newOrderNo);
        newOrder.setLabelNo(newOrderNo);
        getDao().insert(newOrder);

        // 查询样品下的对应测试信息和测试结果信息
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValue(order.getId(), "ORDERID");
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValue(order.getId(), "ORDERID");

        // 查询样品分析项组
        List<EmcOrderItemGroupBean> copiedOrderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValue(order.getId(), "ORDERID");

        List<EmcOrderTaskBean> newOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> newOrderTaskResultList = new ArrayList<>();
        List<EmcOrderItemGroupBean> newOrderItemGroupList = new ArrayList<>();

        // 复制样品分析项组
        copyOrderItemGroup(copiedOrderItemGroupList, order, newOrderId, newOrderItemGroupList);

        for (EmcOrderTaskBean orderTask : orderTaskList) {
            if (order.getId().equals(orderTask.getOrderId())) {
                EmcOrderTaskBean newOrderTask = new EmcOrderTaskBean();
                BeanUtils.copyProperties(orderTask, newOrderTask);

                Long newOrderTaskId = ApplicationContextHelper.getNextIdentity();
                newOrderTask.setId(newOrderTaskId);
                newOrderTask.setOrderId(newOrderId);
                newOrderTask.setEquiptAcquNo(newOrderNo);
                newOrderTaskList.add(newOrderTask);

                for (EmcOrderTaskResultBean orderTaskResult : orderTaskResultList) {
                    if (orderTask.getId().equals(orderTaskResult.getOrderTaskId())) {
                        EmcOrderTaskResultBean newOrderTaskResult = new EmcOrderTaskResultBean();
                        BeanUtils.copyProperties(orderTaskResult, newOrderTaskResult);

                        newOrderTaskResult.setId(ApplicationContextHelper.getNextIdentity());
                        newOrderTaskResult.setOrderId(newOrderId);
                        newOrderTaskResult.setOrderTaskId(newOrderTaskId);
                        newOrderTaskResult.setOrderNo(newOrderNo);
                        newOrderTaskResultList.add(newOrderTaskResult);
                    }
                }
            }
        }

        // 生成样品容器信息
        orderContainerService.createOrderContainer(Collections.singletonList(newOrder), newOrderTaskList);

        orderTaskService.getDao().insert(newOrderTaskList);
        orderTaskResultService.getDao().insert(newOrderTaskResultList);
        orderItemGroupService.getDao().insert(newOrderItemGroupList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void edit(RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        List<EmcOrderItemGroupBean> orderItemGroupList = wrapper.parse(EmcOrderItemGroupBean.class);

        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> oldOrderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");

        List<Long> oldItemGroupIdList = oldOrderItemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
        List<EmcOrderItemGroupBean> newOrderItemGroupList = orderItemGroupList.stream().filter(group -> !oldItemGroupIdList.contains(group.getItemGroupId())).collect(Collectors.toList());

        List<Long> orderItemGroupIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> deleteOrderItemGroupList = oldOrderItemGroupList.stream().filter(group -> !orderItemGroupIdList.contains(group.getItemGroupId())).collect(Collectors.toList());
        // 已存在的
        List<EmcOrderItemGroupBean> existOrderItemGroupList = oldOrderItemGroupList.stream().filter(group -> orderItemGroupIdList.contains(group.getItemGroupId())).collect(Collectors.toList());
        // 增加关联测试，如果新增的测试有关联测试，则自动增加
        if (!newOrderItemGroupList.isEmpty()) {
            List<Long> testMethodIdList = newOrderItemGroupList.stream().map(EmcOrderItemGroupBean::getTestMethodId).distinct().collect(Collectors.toList());
            List<EmcRelatedTestMethodBean> relatedTestMethodList = relatedTestMethodService.selectListByFilter(
                SearchFilter.instance().match("testMethodId", testMethodIdList).filter(MatchPattern.OR)
                        .match("replaceFlag2", "1").filter(MatchPattern.DIFFER), new Order[0]);
            List<Long> relatedTestMethodIdList = relatedTestMethodList.stream().map(EmcRelatedTestMethodBean::getRelatedTestMethodId).distinct().collect(Collectors.toList());
            if (!relatedTestMethodIdList.isEmpty()) {
                List<EmcTestMethodBean> testMethodList = testMethodService.selectListByFilter(SearchFilter.instance().match("id", relatedTestMethodIdList).filter(MatchPattern.OR));

                String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
                List<EmcTestCapacityBean> relatedCapacityList = testCapacityService.selectListByFilter(SearchFilter.instance().match("testMethodId", relatedTestMethodIdList).filter(MatchPattern.OR).match("orgId", parentOrgId).filter(MatchPattern.SEQ));
                List<Long> testCapacityIdList = relatedCapacityList.stream().map(EmcTestCapacityBean::getId).collect(Collectors.toList());
                Map<Long, EmcTestCapacityBean> relatedCapacityMap = relatedCapacityList.stream().collect(Collectors.toMap(EmcTestCapacityBean::getId, Function.identity()));

                List<EmcCapacityItemGroupBean> relatedItemGroupList = capacityItemGroupService.getDao().selectListByOneColumnValues(testCapacityIdList, "testCapacityId");
                List<EmcCapacityItemGroupBean> newRelatedItemGroupList = relatedItemGroupList.stream().filter(itemGroup -> !orderItemGroupIdList.contains(itemGroup.getId())).collect(Collectors.toList());

                // 判定关联的测试是否是现场测试，如果为现场测试，则不添加
                List<Long> newItemGroupIdList = newRelatedItemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
                List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(newItemGroupIdList, "itemGroupId");
                List<Long> itemIdList = groupItemList.stream().map(EmcGroupItemBean::getItemId).distinct().collect(Collectors.toList());
                List<EmcItemBean> itemList = itemService.getDao().selectListByOneColumnValues(itemIdList, "id", Arrays.asList("id", "analysisCategory"));
                List<Long> validCapacityItemGroupIdList = new ArrayList<>();
                newRelatedItemGroupList.forEach(group -> {
                    List<Long> curItemIdList = groupItemList.stream().filter(groupItem -> ObjectUtils.equals(group.getItemGroupId(), groupItem.getItemGroupId())).map(EmcGroupItemBean::getItemId).distinct().collect(Collectors.toList());
                    boolean allSamplingItem = itemList.stream().filter(item -> curItemIdList.contains(item.getId())).allMatch(item -> StringUtils.equals(item.getAnalysisCategory(), "sampling"));
                    if (!allSamplingItem) {
                        validCapacityItemGroupIdList.add(group.getId());
                    }
                });
                List<EmcCapacityItemGroupBean> validCapacityItemGroupList = newRelatedItemGroupList.stream().filter(group -> validCapacityItemGroupIdList.contains(group.getId())).collect(Collectors.toList());

                validCapacityItemGroupList.forEach(related -> {
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

                    Optional.ofNullable(relatedCapacityMap.get(related.getTestCapacityId())).ifPresent(capacity -> {
                        relatedOrderItemGroup.setAnalysisOrgId(capacity.getExt$Item("analysisOrgId"));
                        relatedOrderItemGroup.setAnalysisOrgName(capacity.getExt$Item("analysisOrgName"));
                    });
                    relatedOrderItemGroup.setExt$Item("relatedTest", Constant.YES);

                    newOrderItemGroupList.add(relatedOrderItemGroup);
                });
            }
        }

        // 如果是项目方案修改，则不允许直接通过编辑删除测试
        String operateType = wrapper.getParamValue("operateType");
        if (!deleteOrderItemGroupList.isEmpty() && !StringUtils.equals(operateType, "schemeEdit")) {
            orderItemGroupService.delete(deleteOrderItemGroupList);
        }

        if (!newOrderItemGroupList.isEmpty() && StringUtils.equals(operateType, "schemeEdit")) {
            newOrderItemGroupList.forEach(group -> {
                group.setExt$Item("source", "schemeEdit");
                group.setExt$Item("syncStatus", Constant.YES);
            });
        }

        newOrderItemGroupList.forEach(itemGroup -> itemGroup.setMonitorTmplId(orderList.get(0).getMonitorTmplId()));
        if (!ObjectUtils.isEmpty(existOrderItemGroupList)) {
            Long maxSortNum = existOrderItemGroupList.stream().map(EmcOrderItemGroupBean::getSortNum).max(Comparator.naturalOrder()).orElse(0L);
            for (int i = 0; i < newOrderItemGroupList.size(); i++) {
                newOrderItemGroupList.get(i).setSortNum(i + 1L + maxSortNum);
            }
        }
        orderItemGroupService.insert(orderList, newOrderItemGroupList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void editTest(RestJsonWrapperBean wrapper) {
        String[] ids = StringUtils.split(wrapper.getParamValue("ids"), ",");
        List<EmcOrderItemGroupBean> orderItemGroupList = wrapper.parse(EmcOrderItemGroupBean.class, 1);

        List<Long> orderIdList = Arrays.asList(ids).stream().map(e -> NumberUtils.parseLong(e)).collect(Collectors.toList());
        List<EmcOrderBean> orderList = getDao().selectListByIds(orderIdList);
        List<EmcOrderItemGroupBean> allOrderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");

        // 删除没有的，添加新增的
        List<EmcOrderTaskBean> allOldOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "orderId");
        List<EmcOrderTaskBean> deleteOldOrderTaskList = new ArrayList<>();
        List<EmcOrderItemGroupBean> allNewOrderItemGroupList = new ArrayList<>();
        List<EmcOrderItemGroupBean> allDeleteOrderItemGroupList = new ArrayList<>();

        orderList.forEach(o -> {
            List<EmcOrderTaskBean> oldOrderTaskList = allOldOrderTaskList.stream().filter(e -> ObjectUtils.equals(o.getId(), e.getOrderId())).collect(Collectors.toList());

            List<EmcOrderItemGroupBean> newOrderItemGroupList = new ArrayList<>();

            // 删除未添加的样品测试
            oldOrderTaskList.forEach(t -> {

                EmcOrderItemGroupBean matchOrderItemGroup = orderItemGroupList.stream().filter(e -> ObjectUtils.equals(t.getTestMethodId(), e.getTestMethodId())).findFirst().orElse(null);

                if (matchOrderItemGroup == null) {
                    deleteOldOrderTaskList.add(t);
                    List<EmcOrderItemGroupBean> deleteOrderItemGroupList = allOrderItemGroupList.stream()
                            .filter(a -> ObjectUtils.equals(t.getOrderId(), a.getOrderId()) && ObjectUtils.equals(t.getTestMethodId(), a.getTestMethodId())).collect(Collectors.toList());
                    allDeleteOrderItemGroupList.addAll(deleteOrderItemGroupList);
                }
            });

            // 筛选非重复的所有的新的样品测试
            orderItemGroupList.forEach(i -> {

                EmcOrderTaskBean matchOrderTask = oldOrderTaskList.stream().filter(e -> ObjectUtils.equals(i.getTestMethodId(), e.getTestMethodId())).findFirst().orElse(null);

                if (matchOrderTask == null) {

                    EmcOrderItemGroupBean newOrderItemGroup = new EmcOrderItemGroupBean();
                    BeanUtils.copyProperties(i, newOrderItemGroup);
                    newOrderItemGroup.setOrderId(o.getId());
                    newOrderItemGroup.setId(ApplicationContextHelper.getNextIdentity());
                    newOrderItemGroupList.add(newOrderItemGroup);
                }
            });
            allNewOrderItemGroupList.addAll(newOrderItemGroupList);

        });

        List<Long> orderTaskIdList = deleteOldOrderTaskList.stream().map(e -> e.getId()).distinct().collect(Collectors.toList());

        List<EmcOrderTaskResultBean> delOrderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID");

        // 生成主业务流程数据
        orderTaskService.setDataByOrderList(orderList, allOldOrderTaskList, allNewOrderItemGroupList, EmcBizConstant.ORDER_TASK_LOG_SAMPLING);

        // 插入样品分析项组、样品分析项表
        // 上面orderTaskService.setDataByOrderList() 方法会插入样品分析项组数据，此处再插入会重复
        // orderItemGroupService.insert(orderList, allNewOrderItemGroupList, false);

        // 删除样品分析项组、样品分析项表
        orderItemGroupService.delete(allDeleteOrderItemGroupList);

        // 插入样品测试记录表
        orderTaskLogService.insert(deleteOldOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_DELETE_TEST, EmcBizConstant.ORDER_TASK_LOG_SAMPLING);

        // 删除样品测试
        orderTaskService.getDao().deleteByIdList(orderTaskIdList);

        // 删除样品测试结果
        orderTaskResultService.getDao().deleteByIdList(delOrderTaskResultList.stream().map(e -> e.getId()).collect(Collectors.toList()));

    }

    @Override
    public void addCheckProjectItemGroup(Long id, RestJsonWrapperBean wrapper) {

        List<EmcOrderItemGroupBean> newOrderItemGroupList = wrapper.parse(EmcOrderItemGroupBean.class);
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
/*
        EmcOrderBean order = getDao().selectById(id);
        ArrayList<EmcOrderBean> orderList = new ArrayList<>();
        orderList.add(order);*/
        orderList.forEach(o -> {
            newOrderItemGroupList.forEach(e -> {
                e.setId(ApplicationContextHelper.getNextIdentity());
                e.setOrderId(o.getId());
                e.setMonitorTmplId(o.getMonitorTmplId());
                e.setOrderCategory(o.getOrderCategory());
            });
            orderTaskService.setDataByOrderList(orderList, new ArrayList<>(), newOrderItemGroupList, EmcBizConstant.ORDER_TASK_LOG_CHECK_PROJECT_EDIT,
                    EmcBizConstant.ORDERTASK_STATUS_LOGGED);
            //是否插入orderItem表
            orderItemGroupService.insertByCheckProjectItemGroup(orderList, newOrderItemGroupList, false);
        });
    }

    @Override
    public void copyOrderItemGroup(List<EmcOrderItemGroupBean> copiedOrderItemGroupList, EmcOrderBean copiedOrder, Long newOrderId, List<EmcOrderItemGroupBean> newOrderItemGroupList) {
        List<EmcOrderItemGroupBean> currentOrderItemGroupList = copiedOrderItemGroupList.stream().filter(e -> ObjectUtils.equals(copiedOrder.getId(), e.getOrderId())).collect(Collectors.toList());
        currentOrderItemGroupList.forEach(copiedOrderItemGroup -> {
            EmcOrderItemGroupBean newOrderItemGroup = new EmcOrderItemGroupBean();
            com.sunwayworld.framework.utils.BeanUtils.copyProperties(copiedOrderItemGroup, newOrderItemGroup, EmcConstant.CREATED_COLUMNS);
            newOrderItemGroup.setId(ApplicationContextHelper.getNextIdentity());
            newOrderItemGroup.setOrderId(newOrderId);
            newOrderItemGroupList.add(newOrderItemGroup);
        });
    }

    @Override
    @ValidateDataWith(PfDeleteItemGroupValidator.class)
    public void delCheckProjectItemGroup(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<EmcOrderItemGroupBean> deleteOrderItemGroupList = new ArrayList<>();
        orderTaskList.forEach(task -> {
            EmcOrderItemGroupBean deleteOrderItemGroup = new EmcOrderItemGroupBean();
            deleteOrderItemGroup.setOrderId(task.getOrderId());
            deleteOrderItemGroup.setTestMethodId(task.getTestMethodId());
            deleteOrderItemGroup.setItemGroup(task.getExt$Item("itemGroup"));
            deleteOrderItemGroupList.add(deleteOrderItemGroup);
        });
        orderItemGroupService.getDao().deleteBy(deleteOrderItemGroupList, "orderId", "testMethodId", "itemGroup");

        List<Long> orderTaskIdList = orderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        orderTaskService.getDao().deleteByIdList(orderTaskIdList);

        List<EmcOrderTaskResultBean> deleteResultList = new ArrayList<>(orderTaskIdList.size());
        orderTaskIdList.forEach(taskId -> {
            EmcOrderTaskResultBean deleteResult = new EmcOrderTaskResultBean();
            deleteResult.setOrderTaskId(taskId);
            deleteResultList.add(deleteResult);
        });
        orderTaskResultService.getDao().deleteBy(deleteResultList, "orderTaskId");
    }

    //删除t_emc_label表要删除的数据并更新样品表的扫描状态
    @Override
    public void updateScanStatus(RestJsonWrapperBean wrapper) {
    	//要删除的标签表数据
    	List<EmcOrderLabelBean> emcOrderLabelList = wrapper.parse(EmcOrderLabelBean.class);
        List<String> labelCodeList =  emcOrderLabelList.stream().map(a -> a.getLabelCode()).collect(Collectors.toList());
        //根据标签匹配样品更新样品的扫描状态为空
        List<EmcOrderBean> updateList = getDao().selectListByOneColumnValues(labelCodeList, "LABELNO");
        if (!updateList.isEmpty()) {
        	updateList.forEach(t -> t.setExt$Item("SCANSTATUS", ""));
        	getDao().update(updateList, "SCANSTATUS");
        }
        //删除标签表数据
        orderLabelService.getDao().deleteBy(emcOrderLabelList);
    }

    @Override
    public void undoScanned(RestJsonWrapperBean wrapper) {
    	//要删除的标签表数据
    	List<EmcOrderBean> updateList = wrapper.parse(EmcOrderBean.class);
        List<String> labelNoList =  updateList.stream().map(a -> a.getLabelNo()).collect(Collectors.toList());
        //根据标签匹配样品更新样品的扫描状态为空
        if (!updateList.isEmpty()) {
        	updateList.forEach(t -> t.setExt$Item("SCANSTATUS", ""));
        	getDao().update(updateList, "SCANSTATUS");
        }
        //删除标签表数据
    	List<EmcOrderLabelBean> orderLabelList = orderLabelService.getDao().selectListByOneColumnValues(labelNoList, "labelCode");

        orderLabelService.getDao().deleteBy(orderLabelList);

    }

    @Override
    @Transactional
    @ValidateDataWith(PfSyncNewOrderValidator.class)
    public void syncNewOrder(RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        List<EmcOrderBean> newOrderList = orderList.stream().filter(order -> StringUtils.equals(order.getExt$Item("syncStatus"), Constant.YES)).collect(Collectors.toList());

        if (newOrderList.isEmpty()) {
            return;
        }

        // 如果新增了样品类型，则向报告表增加一条数据
        List<EmcReportBean> reportList = reportService.getDao().selectListByOneColumnValue(newOrderList.get(0).getProjId(), "projId", Arrays.asList("id", "monitorTmplId"));
        List<String> reportMonitorTmplIdList = reportList.stream().filter(r -> !StringUtils.isEmpty(r.getMonitorTmplId())).map(EmcReportBean::getMonitorTmplId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> newOrderCategoryOrderList = newOrderList.stream().filter(order -> !reportMonitorTmplIdList.contains(order.getMonitorTmplId())).collect(Collectors.toList());

        if (!newOrderCategoryOrderList.isEmpty()) {
            List<EmcReportBean> newReportList = new ArrayList<>(newOrderCategoryOrderList.size());
            newOrderCategoryOrderList.forEach(order -> {
                EmcReportBean newReport = new EmcReportBean();
                newReport.setId(ApplicationContextHelper.getNextIdentity());
                newReport.setProjId(newOrderList.get(0).getProjId());
                newReport.setMonitorTmplId(order.getMonitorTmplId());
                newReport.setOrderCategory(order.getOrderCategory());
                newReport.setStatus("draft");
                newReport.setProcessStatus("draft");
                newReportList.add(newReport);
            });
            reportService.getDao().insert(newReportList);
        }

        List<Long> newOrderIdList = newOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(newOrderIdList, "orderId", Collections.singletonList("id"));

        orderTaskService.insertByOrder(newOrderList);

        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(newOrderIdList, "orderId", Collections.singletonList("id"));
        orderTaskList.forEach(task -> task.setStatus(EmcBizConstant.ORDERTASK_STATUS_PRELOGGED));
        orderTaskService.getDao().update(orderTaskList, "status");

        newOrderList.forEach(order -> order.setExt$Item("syncStatus", null));
        orderItemGroupList.forEach(group -> group.setExt$Item("syncStatus", null));

        getDao().update(newOrderList, "syncStatus");
        orderItemGroupService.getDao().update(orderItemGroupList, "syncStatus");
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
        editLogBean.setOperateInfo("项目方案修改-同步新增样品");
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
    @ValidateDataWith(PfSyncNewOrderItemGroupValidator.class)
    public void syncNewTest(RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());

        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "orderId");
        List<EmcOrderItemGroupBean> newOrderItemGroupList = orderItemGroupList.stream().filter(group -> StringUtils.equals(group.getExt$Item("syncStatus"), Constant.YES)).collect(Collectors.toList());

        if (newOrderItemGroupList.isEmpty()) {
            return;
        }

        List<EmcOrderTaskBean> insertOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> insertOrderTaskResultList = new ArrayList<>();

        // 查询分析科室信息
        List<Long> capacityItemGroupIdList = newOrderItemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
        List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByOneColumnValues(capacityItemGroupIdList, "id", Arrays.asList("id", "testCapacityId"));
        List<Long> testCapacityIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getTestCapacityId).distinct().collect(Collectors.toList());
        List<EmcTestCapacityBean> testCapacityList = testCapacityService.getDao().selectListByOneColumnValues(testCapacityIdList, "id", Arrays.asList("id", "analysisOrgId", "analysisOrgName"));
        Map<Long, EmcTestCapacityBean> testCapacityMap = testCapacityList.stream().collect(Collectors.toMap(EmcTestCapacityBean::getId, Function.identity()));

        // 查询采集编号后缀信息
        List<Long> testMethodIdList = newOrderItemGroupList.stream().map(EmcOrderItemGroupBean::getTestMethodId).distinct().collect(Collectors.toList());
        List<EmcTestMethodBean> testMethodList = testMethodService.getDao().selectListByOneColumnValues(testMethodIdList, "id", Arrays.asList("id", "acqNoSuffix"));
        Map<Long, String> acqNoSuffixMap = testMethodList.stream().filter(t -> !StringUtils.isEmpty(t.getAcqNoSuffix())).collect(Collectors.toMap(EmcTestMethodBean::getId, EmcTestMethodBean::getAcqNoSuffix));

        List<EmcOrderItemBean> orderItemList = orderItemService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");
        List<Long> uniqueItemIdList = orderItemList.stream().map(EmcOrderItemBean::getItemId).distinct().collect(Collectors.toList());
        List<EmcItemBean> itemList = itemService.getDao().selectListByOneColumnValues(uniqueItemIdList, "ID");

        List<Long> newOrderIdList = newOrderItemGroupList.stream().map(EmcOrderItemGroupBean::getOrderId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(newOrderIdList, "orderId", Arrays.asList("id", "orderId", "status"));

        List<String> defaultTaskStatusList = Arrays.asList("draft", "preLogged", "logged");

        Map<Long, List<EmcOrderItemGroupBean>> newOrderItemGroupMap = newOrderItemGroupList.stream().collect(Collectors.groupingBy(EmcOrderItemGroupBean::getOrderId));
        newOrderItemGroupMap.forEach((orderId, curGroupList) -> {
            EmcOrderBean curOrder = orderList.stream().filter(order -> ObjectUtils.equals(order.getId(), orderId)).findFirst().orElse(null);
            if (null == curOrder) {
                return;
            }

            // 当前样品下测试的状态
            List<String> taskStatusList = orderTaskList.stream().filter(task -> ObjectUtils.equals(task.getOrderId(), orderId)).map(EmcOrderTaskBean::getStatus).distinct().collect(Collectors.toList());
            List<Integer> statusIndexList = taskStatusList.stream().mapToInt(defaultTaskStatusList::indexOf).sorted().boxed().collect(Collectors.toList());
            String newTaskStatus = (statusIndexList.isEmpty() || statusIndexList.get(0) == -1) ? "logged" : defaultTaskStatusList.get(statusIndexList.get(0));

            ArrayList<EmcOrderItemGroupBean> uniqueGroupList = curGroupList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getTestId() + "#" + o.getMethodId()))), ArrayList::new));
            uniqueGroupList.forEach(orderItemGroup -> {

                List<EmcOrderItemGroupBean> testMethodOrderItemGroupList = curGroupList.stream().filter(e -> ObjectUtils.equals(orderItemGroup.getTestId(), e.getTestId())
                        && ObjectUtils.equals(orderItemGroup.getMethodId(), e.getMethodId())).collect(Collectors.toList());

                List<Long> testMethodOrderItemGroupIdList = testMethodOrderItemGroupList.stream().map(EmcOrderItemGroupBean::getId).collect(Collectors.toList());
                List<EmcOrderItemBean> filterOrderItemList = orderItemList.stream().filter(e -> testMethodOrderItemGroupIdList.contains(e.getOrderItemGroupId())).collect(Collectors.toList());
                List<Long> itemIdList = filterOrderItemList.stream().map(EmcOrderItemBean::getItemId).distinct().collect(Collectors.toList());
                List<EmcItemBean> filterItemList = itemList.stream().filter(e -> itemIdList.contains(e.getId()) && Constant.YES.equals(e.getActivatedFlag())).collect(Collectors.toList());

                // 由于是送样的样品类型，此处只插入分析类型
                List<String> analysisCategoryList = filterItemList.stream().map(EmcItemBean::getAnalysisCategory).distinct()
                        .filter(category -> StringUtils.equals(PfBizConstant.ANALYSISCATEGORY_ANALYSIS, category)).collect(Collectors.toList());
                analysisCategoryList.forEach(analysisCategory -> {
                    // 样品测试
                    EmcOrderTaskBean orderTask = new EmcOrderTaskBean();

                    BeanUtils.copyProperties(orderItemGroup, orderTask);

                    orderTask.setId(ApplicationContextHelper.getNextIdentity());
                    orderTask.setAnalysisType(analysisCategory);
                    orderTask.setFolderId(curOrder.getFolderId());
                    orderTask.setProjId(curOrder.getProjId());
                    orderTask.setMonitorTmplId(curOrder.getMonitorTmplId());
                    orderTask.setOrderCategory(curOrder.getOrderCategory());

                    boolean isSolidWaste = isSolidWaste(curOrder);
                    String acqNoSuffix = acqNoSuffixMap.get(orderTask.getTestMethodId());
                    String acqNo = (isSolidWaste && acqNoSuffix != null) ? String.join("-", curOrder.getOrderNo(), acqNoSuffix) : curOrder.getOrderNo();

                    orderTask.setEquiptAcquNo(acqNo);
                    orderTask.setExt$Item("relatedTest", orderItemGroup.getExt$Item("relatedTest"));
                    orderTask.setStatus(newTaskStatus);

                    if (StringUtils.isEmpty(orderTask.getAnalysisOrgId())) {
                        capacityItemGroupList.stream().filter(cig -> ObjectUtils.equals(cig.getId(), orderItemGroup.getItemGroupId())).findFirst().ifPresent(cig -> Optional.ofNullable(testCapacityMap.get(cig.getTestCapacityId())).ifPresent(tc -> {
                            orderTask.setAnalysisOrgId(tc.getExt$Item("analysisOrgId"));
                            orderTask.setAnalysisOrgName(tc.getExt$Item("analysisOrgName"));
                        }));
                    }

                    insertOrderTaskList.add(orderTask);

                    // 样品测试结果
                    List<EmcItemBean> analysisCategoryItemList = filterItemList.stream().filter(e -> ObjectUtils.equals(e.getAnalysisCategory(), analysisCategory)).collect(Collectors.toList());

                    insertOrderTaskResultList.addAll(orderTaskService.getInsertOrderTaskResultList(orderTask, analysisCategoryItemList, curOrder));
                });
            });
        });

        newOrderItemGroupList.forEach(group -> group.setExt$Item("syncStatus", null));

        orderItemGroupService.getDao().update(newOrderItemGroupList, "syncStatus");
        orderTaskService.getDao().insert(insertOrderTaskList);
        orderTaskResultService.getDao().insert(insertOrderTaskResultList);

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
        editLogBean.setOperateInfo("项目方案修改-同步新增测试");
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
    @ValidateDataWith(PfDeleteOrderValidator.class)
    public void deleteSyncOrder(RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());

        // 删除点位数据
        List<Long> folderIdList = orderList.stream().map(EmcOrderBean::getFolderId).collect(Collectors.toList());
        folderService.getDao().deleteByIdList(folderIdList);

        // 删除样品数据
        getDao().deleteByIdList(orderIdList);

        // 删除样品分析项组、样品分析项数据
        List<EmcOrderItemGroupBean> deleteOrderItemGroupList = new ArrayList<>(orderIdList.size());
        List<EmcOrderItemBean> deleteOrderItemList = new ArrayList<>(orderIdList.size());
        orderIdList.forEach(orderId -> {
            EmcOrderItemGroupBean deleteOrderItemGroup = new EmcOrderItemGroupBean();
            deleteOrderItemGroup.setOrderId(orderId);
            deleteOrderItemGroupList.add(deleteOrderItemGroup);

            EmcOrderItemBean deleteOrderItem = new EmcOrderItemBean();
            deleteOrderItem.setOrderId(orderId);
            deleteOrderItemList.add(deleteOrderItem);
        });
        orderItemGroupService.getDao().deleteBy(deleteOrderItemGroupList, "orderId");
        orderItemService.getDao().deleteBy(deleteOrderItemList, "orderId");

        // 删除样品测试、样品测试结果数据，未同步的样品，表示是方案修改新增的，其他为之前已存在的样品
        List<EmcOrderBean> originalOrderList = orderList.stream().filter(order -> !StringUtils.equals(order.getExt$Item("syncStatus"), Constant.YES)).collect(Collectors.toList());
        List<Long> originalOrderIdList = originalOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());

        List<EmcOrderTaskBean> deleteOrderTaskList = new ArrayList<>(originalOrderIdList.size());
        List<EmcOrderTaskResultBean> deleteResultList = new ArrayList<>(originalOrderIdList.size());
        originalOrderIdList.forEach(orderId -> {
            EmcOrderTaskBean deleteOrderTask = new EmcOrderTaskBean();
            deleteOrderTask.setOrderId(orderId);
            deleteOrderTaskList.add(deleteOrderTask);

            EmcOrderTaskResultBean deleteResult = new EmcOrderTaskResultBean();
            deleteResult.setOrderId(orderId);
            deleteResultList.add(deleteResult);
        });

        orderTaskService.getDao().deleteBy(deleteOrderTaskList, "orderId");
        orderTaskResultService.getDao().deleteBy(deleteResultList, "orderId");

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
        editLogBean.setOperateInfo("项目方案修改-删除并同步样品");
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
    @ValidateDataWith(PfDeleteOrderItemGroupValidator.class)
    public void deleteSyncTest(RestJsonWrapperBean wrapper) {
        List<EmcOrderItemGroupBean> orderItemGroupList = wrapper.parse(EmcOrderItemGroupBean.class);
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);

        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).distinct().collect(Collectors.toList());
        List<Long> itemGroupIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());

        List<EmcOrderItemGroupBean> deleteOrderItemGroupList = orderItemGroupService.selectListByFilter(SearchFilter.instance().match("orderId", orderIdList).filter(MatchPattern.OR).match("itemGroupId", itemGroupIdList).filter(MatchPattern.OR));
        deleteSyncTest(deleteOrderItemGroupList);

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
        editLogBean.setOperateInfo("项目方案修改-删除并同步测试");
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
    public void deleteSyncTest(List<EmcOrderItemGroupBean> orderItemGroupList) {
        // 删除样品分析项组数据
        List<Long> orderItemGroupIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean::getId).collect(Collectors.toList());
        orderItemGroupService.getDao().deleteByIdList(orderItemGroupIdList);

        List<EmcOrderItemGroupBean> originalOrderItemGroupList = orderItemGroupList.stream().filter(group -> !StringUtils.equals(group.getExt$Item("syncStatus"), Constant.YES)).collect(Collectors.toList());

        List<EmcOrderItemBean> deleteOrderItemList = new ArrayList<>(originalOrderItemGroupList.size());
        List<EmcOrderTaskBean> deleteOrderTaskList = new ArrayList<>(originalOrderItemGroupList.size());
        List<EmcOrderTaskResultBean> deleteResultList = new ArrayList<>(originalOrderItemGroupList.size());

        originalOrderItemGroupList.forEach(group -> {
            EmcOrderItemBean deleteItem = new EmcOrderItemBean();
            deleteItem.setOrderId(group.getOrderId());
            deleteItem.setOrderItemGroupId(group.getId());
            deleteOrderItemList.add(deleteItem);

            EmcOrderTaskBean deleteOrderTask = new EmcOrderTaskBean();
            deleteOrderTask.setOrderId(group.getOrderId());
            deleteOrderTask.setTestMethodId(group.getTestMethodId());
            deleteOrderTaskList.add(deleteOrderTask);

            EmcOrderTaskResultBean deleteResult = new EmcOrderTaskResultBean();
            deleteResult.setOrderId(group.getOrderId());
            deleteResult.setTestMethodId(group.getTestMethodId());
            deleteResultList.add(deleteResult);
        });

        orderItemService.getDao().deleteBy(deleteOrderItemList, "orderId", "orderItemGroupId");
        orderTaskService.getDao().deleteBy(deleteOrderTaskList, "orderId", "testMethodId");
        orderTaskResultService.getDao().deleteBy(deleteResultList, "orderId", "testMethodId");
    }

    @Override
    public void fixDuplicateOrderTask(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> selectedOrderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        String runId = selectedOrderTaskList.get(0).getRunId();
        List<Long> orderIdList = selectedOrderTaskList.stream().map(EmcOrderTaskBean::getOrderId).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "orderId");
        List<EmcOrderTaskResultBean> taskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderIdList, "orderId");

        List<Long> deleteTaskIdList = new ArrayList<>();
        List<Long> deleteResultIdList = new ArrayList<>();
        List<EmcOrderTaskResultBean> updateResultList = new ArrayList<>();

        Map<Long, Long> replacedTaskIdMap = new HashMap<>();

        Map<String, List<EmcOrderTaskBean>> testMethodTaskMap = orderTaskList.stream().collect(Collectors.groupingBy(task -> task.getOrderId() + "$" + task.getTestMethodId()));
        testMethodTaskMap.forEach((key, taskList) -> {
            if (taskList.size() == 1) {
                return;
            }

            List<String> analysisTypeList = taskList.stream().map(EmcOrderTaskBean::getAnalysisType).distinct().collect(Collectors.toList());
            if (taskList.size() == 2 && analysisTypeList.size() == 2) {
                return;
            }

            List<Long> taskIdList = taskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
            List<EmcOrderTaskResultBean> curResultList = taskResultList.stream().filter(r -> taskIdList.contains(r.getOrderTaskId())).collect(Collectors.toList());
            Map<Long, List<EmcOrderTaskResultBean>> curResultMap = curResultList.stream().collect(Collectors.groupingBy(EmcOrderTaskResultBean::getOrderTaskId));

            Long baseTaskId = taskIdList.get(0);
            List<Long> repeatedTaskIdList = taskIdList.stream().filter(taskId -> !baseTaskId.equals(taskId)).collect(Collectors.toList());
            repeatedTaskIdList.forEach(repeatedTaskId -> replacedTaskIdMap.put(repeatedTaskId, baseTaskId));
            deleteTaskIdList.addAll(repeatedTaskIdList);

            List<EmcOrderTaskResultBean> baseResultList = curResultMap.get(baseTaskId);
            List<Long> curItemIdList = baseResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());

            curResultMap.forEach((taskId, resultList) -> {
                if (baseTaskId.equals(taskId)) {
                    return;
                }

                List<Long> repeatResultIdList = resultList.stream().filter(r -> curItemIdList.contains(r.getItemId())).map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
                deleteResultIdList.addAll(repeatResultIdList);

                List<EmcOrderTaskResultBean> curUpdateResultList = resultList.stream().filter(r -> !repeatResultIdList.contains(r.getId())).collect(Collectors.toList());
                curUpdateResultList.forEach(r -> r.setOrderTaskId(baseTaskId));
                updateResultList.addAll(curUpdateResultList);
            });
        });

        Set<Long> replacedTaskIdSet = replacedTaskIdMap.keySet();

        List<EmcOrderTaskBean> runTaskList = orderTaskService.getDao().selectListByOneColumnValue(runId, "runId", Arrays.asList("id", "parentId"));
        List<EmcOrderTaskBean> hasParentTaskList = runTaskList.stream().filter(task -> !ObjectUtils.isEmpty(task.getParentId()) && replacedTaskIdSet.contains(task.getParentId())).collect(Collectors.toList());
        hasParentTaskList.forEach(task -> task.setParentId(replacedTaskIdMap.get(task.getParentId())));

        orderTaskService.getDao().deleteByIdList(deleteTaskIdList);
        orderTaskResultService.getDao().deleteByIdList(deleteResultIdList);
        orderTaskResultService.getDao().update(updateResultList, "orderTaskId");
    }

    @Override
    public void afterDeleteTest(RestJsonWrapperBean wrapper) {
    	List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
    	List<Long> orderIdList = orderList.stream().map(EmcOrderBean :: getId).distinct().collect(Collectors.toList());
    	List<EmcOrderBean> orderAfterList = getDao().selectListByIds(orderIdList);
    	if(orderAfterList.isEmpty()){
    	    return;
        }
    	List<Long> projIdList = orderAfterList.stream().map(EmcOrderBean :: getProjId).distinct().collect(Collectors.toList());
    	Long projId = projIdList.get(0);
    	if(projId!=null){
        	EmcProjectBean projectBean = projectService.selectById(projId);
    		List<EmcOrderTaskBean> orderTaskBeanList = orderTaskService.getDao().selectListByOneColumnValue(projId, "projId");
    		List<String> statusList =orderTaskBeanList.stream().map(EmcOrderTaskBean ::getStatus).distinct().collect(Collectors.toList());
    		if(statusList.size()==1 && statusList.get(0).equals("done")){
    			//这种情况可以将项目的点位状态都置为done，项目状态置为报告编制
    			List<Long> folderIdList  = orderTaskBeanList.stream().map(EmcOrderTaskBean :: getFolderId).distinct().collect(Collectors.toList());
    			List<EmcFolderBean> folderList = folderService.getDao().selectListByIds(folderIdList);
    			folderList.forEach(f->{
    				f.setStatus("done");
    			});
    			projectBean.setStatus("reportEdit");
        		projectService.getDao().update(projectBean, "status");
        		folderService.getDao().update(folderList, "status");
    		}
    	}
    }

    @Override
    @ValidateDataWith(PfSubpackageTestValidator.class)
    @Transactional
    public void subpackageTest(RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        List<EmcOrderItemGroupBean> itemGroupList = wrapper.parse(EmcOrderItemGroupBean.class);

        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<Long> itemGroupIdList = itemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());

        List<EmcOrderItemGroupBean> targetItemGroupList = orderItemGroupService.selectListByFilter(SearchFilter.instance().match("orderId", orderIdList).filter(MatchPattern.OR).match("itemGroupId", itemGroupIdList).filter(MatchPattern.OR));
        Long subcontractorId = ApplicationContextHelper.getNextIdentity();
        String subpackageNo = ApplicationContextHelper.getNextSequence(orderItemGroupService.getDao().getTable());
        targetItemGroupList.forEach(group -> {
            group.setSubcontractorId(subcontractorId);
            group.setSubPackageNo(subpackageNo);
            group.setExt$Item("subpackageType", "orderSubpackage");
        });
        orderItemGroupService.getDao().update(targetItemGroupList, "subcontractorId", "subpackageNo", "subpackageType");

        List<Long> testMethodIdList = itemGroupList.stream().map(EmcOrderItemGroupBean::getTestMethodId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("orderId", orderIdList).filter(MatchPattern.OR).match("testMethodId", testMethodIdList).filter(MatchPattern.OR));

        List<Long> orderTaskIdList = orderTaskList.stream().map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());
        // 测试分包记录
        List<EmcSubcontractRecordBean> recordList = new ArrayList<>(orderTaskIdList.size());
        orderTaskIdList.forEach(taskId -> {
            EmcSubcontractRecordBean record = new EmcSubcontractRecordBean();
            record.setId(ApplicationContextHelper.getNextIdentity());
            record.setOrderTaskId(taskId);
            record.setSubcontractorId(subcontractorId);
            recordList.add(record);
        });
        subcontractRecordService.getDao().insert(recordList);

        orderTaskList.forEach(task -> {
            task.setSubPackageNo(subpackageNo);
            task.setSubcontractorId(subcontractorId);
            task.setSamplingSubpackage("1");
            task.setStatus(PfBizConstant.ORDERTASK_STATUS_DONE);
        });

        orderTaskService.getDao().update(orderTaskList, "status","samplingsubpackage", "subcontractorId", "subpackageNo");
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
    public void cancelSubpackageTest(RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        List<EmcOrderItemGroupBean> itemGroupList = wrapper.parse(EmcOrderItemGroupBean.class);

        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<Long> itemGroupIdList = itemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());

        List<EmcOrderItemGroupBean> targetItemGroupList = orderItemGroupService.selectListByFilter(SearchFilter.instance().match("orderId", orderIdList).filter(MatchPattern.OR).match("itemGroupId", itemGroupIdList).filter(MatchPattern.OR));
        targetItemGroupList.forEach(group -> {
            group.setSubcontractorId(null);
            group.setSubPackageNo(null);
            group.setExt$Item("subpackageType", null);
        });
        orderItemGroupService.getDao().update(targetItemGroupList, "subcontractorId", "subpackageNo", "subpackageType");

        List<Long> testMethodIdList = itemGroupList.stream().map(EmcOrderItemGroupBean::getTestMethodId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> targetOrderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("orderId", orderIdList).filter(MatchPattern.OR).match("testMethodId", testMethodIdList).filter(MatchPattern.OR));

        List<Long> targetOrderTaskIdList = targetOrderTaskList.stream().map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());
        // 删除测试分包记录
        List<EmcSubcontractRecordBean> recordList = new ArrayList<>(targetOrderTaskIdList.size());
        targetOrderTaskIdList.forEach(taskId -> {
            EmcSubcontractRecordBean record = new EmcSubcontractRecordBean();
            record.setOrderTaskId(taskId);
            recordList.add(record);
        });
        subcontractRecordService.getDao().deleteBy(recordList, "orderTaskId");

        List<String> defaultTaskStatusList = Arrays.asList("draft", "preLogged", "logged");
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "orderId", Arrays.asList("id", "status"));
        List<String> taskStatusList = orderTaskList.stream().filter(task -> !targetOrderTaskIdList.contains(task.getId())).map(EmcOrderTaskBean::getStatus).distinct().collect(Collectors.toList());
        List<Integer> statusIndexList = taskStatusList.stream().mapToInt(defaultTaskStatusList::indexOf).sorted().boxed().collect(Collectors.toList());
        String newTaskStatus = (statusIndexList.isEmpty() || statusIndexList.get(0) == -1) ? "logged" : defaultTaskStatusList.get(statusIndexList.get(0));
        targetOrderTaskList.forEach(task -> {
            task.setSubPackageNo(null);
            task.setSubcontractorId(null);
            task.setSamplingSubpackage(null);
            task.setStatus(newTaskStatus);
        });

        orderTaskService.getDao().update(targetOrderTaskList, "status","samplingsubpackage", "subcontractorId", "subpackageNo");

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
    @ValidateDataWith(PfEditOrderQcValidator.class)
    public void editQc(RestJsonWrapperBean wrapper) {
    	List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
    	if(orderList.isEmpty()){
    		return;
    	}
    	String qcName = orderList.get(0).getQcName();
    	Long qcCategoryId = orderList.get(0).getQcCategoryId();
    	String qcCategoryCode = orderList.get(0).getQcCategoryCode();
    	List<Long> deleteWaterContentOrderIdList = new ArrayList<>();
        //修改QC类型时候，判断如果样品类型是土壤,如果原来有水分含量测试需要删掉
        if(qcName.contains(PfConstant.QC_BLANK_KEYWORD)){
            orderList.forEach(o ->{
                if(o.getOrderCategory().contains(PfConstant.DIOXIN_SOIL_KEYWORDS[0])){
                    deleteWaterContentOrderIdList.add(o.getId());
                }
            });
        }

        if(!deleteWaterContentOrderIdList.isEmpty()){
            //删除下面三表对应的水分含量数据
            List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(deleteWaterContentOrderIdList, "orderId");
            List<EmcOrderItemGroupBean> filterOrderItemGroupList = orderItemGroupList.stream().filter(oig ->ObjectUtils.equals(730127543771200L,oig.getTestMethodId())).distinct().collect(Collectors.toList());
            List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(deleteWaterContentOrderIdList, "orderId");
            List<EmcOrderTaskBean> filterOrderTaskList = orderTaskList.stream().filter(ok ->ObjectUtils.equals(730127543771200L,ok.getTestMethodId())).distinct().collect(Collectors.toList());
            List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(deleteWaterContentOrderIdList, "orderId");
            List<EmcOrderTaskResultBean> filterOrderTaskResultList = orderTaskResultList.stream().filter(res ->ObjectUtils.equals(730127543771200L,res.getTestMethodId())).distinct().collect(Collectors.toList());
            orderItemGroupService.getDao().deleteBy(filterOrderItemGroupList);
            orderTaskService.getDao().deleteBy(filterOrderTaskList);
            orderTaskResultService.getDao().deleteBy(filterOrderTaskResultList);

        }
    	List<Long> orderIdList = orderList.stream().map(EmcOrderBean :: getId).distinct().collect(Collectors.toList());
    	if(orderIdList.isEmpty()){
    		return;
    	}

    	getDao().update(orderList, "qcCategoryId","qcCategoryCode","qcName");
    	List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "orderId");
    	if(!orderItemGroupList.isEmpty()){
        	orderItemGroupList.forEach(oig ->{
        		oig.setQcCategoryCode(qcCategoryCode);
        		oig.setQcCategoryId(qcCategoryId);
        		oig.setQcName(qcName);
        	});
        	orderItemGroupService.getDao().update(orderItemGroupList, "qcCategoryId","qcCategoryCode","qcName");
    	}

    	List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "orderId");
    	if(!orderTaskList.isEmpty()){
        	orderTaskList.forEach(ok ->{
        		ok.setQcCategoryCode(qcCategoryCode);
        		ok.setQcCategoryId(qcCategoryId);
        		ok.setQcName(qcName);
        	});
        	orderTaskService.getDao().update(orderTaskList, "qcCategoryId","qcCategoryCode","qcName");
    	}

    	List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderIdList, "orderId");
    	if(!orderTaskResultList.isEmpty()){
        	orderTaskResultList.forEach(okr ->{
        		okr.setQcCategoryCode(qcCategoryCode);
        		okr.setQcCategoryId(qcCategoryId);
        		okr.setQcName(qcName);
        	});
        	orderTaskResultService.getDao().update(orderTaskResultList, "qcCategoryId","qcCategoryCode","qcName");
    	}

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
        editLogBean.setOperateInfo("项目方案修改-修改QC类型");
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
    public void editParentOrderNo(RestJsonWrapperBean wrapper) {
    	//选中的样品
    	String orderIdB = wrapper.getFilterValue("orderId");
    	Long orderId = Long.parseLong(orderIdB);
    	//选中的父样
    	EmcOrderBean order = wrapper.parseUnique(EmcOrderBean.class);
    	//父样编号修改了，选中样的父样编号需要修改，选中样的测试的parentId需要修改
    	EmcOrderBean updateOrder = getDao().selectById(orderId);
    	if(ObjectUtils.isEmpty(order)){
    		return;
    	}
    	updateOrder.setParentOrderNo(order.getOrderNo());
    	getDao().update(updateOrder, "parentOrderNo");
    	//样品测试的parentid与父样的ordertaskid相同
    	List<EmcOrderTaskBean> orderUpdateTaskList = orderTaskService.getDao().selectListByOneColumnValue(orderId, "orderId");
    	//选中样ordertask
    	List<EmcOrderTaskBean> orderChooseTaskList = orderTaskService.getDao().selectListByOneColumnValue(order.getId(), "orderId");
    	orderUpdateTaskList.stream().forEach(ok ->{
    		List<EmcOrderTaskBean> orderChooseMatchTaskList = orderChooseTaskList.stream()
    				.filter(oc ->ObjectUtils.equals(oc.getTestMethodId(), ok.getTestMethodId())).collect(Collectors.toList());
    		ok.setParentId(orderChooseMatchTaskList.get(0).getId());
    	});
    	orderTaskService.getDao().update(orderUpdateTaskList, "parentId");
        //给日志信息
        //登陆人信息
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        //项目信息(需要替换projID)
        long projectEditMatchId = order.getProjId() ;
        if(ObjectUtils.isEmpty(projectEditMatchId)){
            return;
        }
        EmcProjectBean projectEditMatch = projectService.getDao().selectById(projectEditMatchId);
        //操作信息(根据业务种类不同区分)
        //resource层的备注 +
        EmcProjectEditLogBean editLogBean = new EmcProjectEditLogBean();
        editLogBean.setId(ApplicationContextHelper.getNextIdentity());
        editLogBean.setIsSample(projectEditMatch.getSample());
        editLogBean.setOperateInfo("项目方案修改-修改父样编号");
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
    public void resetParentOrderNo(RestJsonWrapperBean wrapper) {
    	//选中的样品
    	String orderIdB = wrapper.getFilterValue("orderId");
    	Long orderId = Long.parseLong(orderIdB);
    	List<EmcOrderTaskBean> orderUpdateTaskList = orderTaskService.getDao().selectListByOneColumnValue(orderId, "orderId");
    	orderUpdateTaskList.stream().forEach(ok ->{
    		ok.setParentId(null);
    	});
    	orderTaskService.getDao().update(orderUpdateTaskList, "parentId");
        //给日志信息
        //登陆人信息
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        //项目信息(需要替换projID)
        long projectEditMatchId = orderUpdateTaskList.get(0).getProjId() ;
        if(ObjectUtils.isEmpty(projectEditMatchId)){
            return;
        }
        EmcProjectBean projectEditMatch = projectService.getDao().selectById(projectEditMatchId);
        //操作信息(根据业务种类不同区分)
        //resource层的备注 +
        EmcProjectEditLogBean editLogBean = new EmcProjectEditLogBean();
        editLogBean.setId(ApplicationContextHelper.getNextIdentity());
        editLogBean.setIsSample(projectEditMatch.getSample());
        editLogBean.setOperateInfo("项目方案修改-重置父样编号");
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
    public Page<EmcOrderItemGroupBean> selectItemGroupPagationByProjId(Long id, RestJsonWrapperBean wrapper) {
        return orderItemGroupService.selectPaginationByFilter(SearchFilter.instance().match("PROJID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public Page<EmcOrderItemGroupBean> selectItemGroupPagationByOrderId(Long id, RestJsonWrapperBean wrapper) {
        return orderItemGroupService.selectPaginationByFilter(SearchFilter.instance().match("ORDERID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public void changeIsConvert(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskBeans = wrapper.parse(EmcOrderTaskBean.class);
        List<Long> orderIdList = orderTaskBeans.stream().map(EmcOrderTaskBean :: getOrderId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> orderList = selectListByFilter(SearchFilter.instance().match("id", orderIdList).filter(MatchPattern.OR));
        if(!orderList.isEmpty()){
            orderList.forEach(o ->{
                Boolean isconvert = StringUtils.equals(o.getExt$Item("isconvert"),"1");
                o.setExt$Item("isconvert",isconvert?"0":"1");
            });
            getDao().update(orderList,"isconvert");
        }

    }

    @Override
    public void addPhFlag(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskBeans = wrapper.parse(EmcOrderTaskBean.class);
        List<Long> orderIdList = orderTaskBeans.stream().map(EmcOrderTaskBean :: getOrderId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> orderList = selectListByFilter(SearchFilter.instance().match("id", orderIdList).filter(MatchPattern.OR));
        if(!orderList.isEmpty()){
            orderList.forEach(o ->{
                o.setExt$Item("phflag","1");
            });
            getDao().update(orderList,"phflag");
        }

    }

    @Override
    public void delPhFlag(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskBeans = wrapper.parse(EmcOrderTaskBean.class);
        List<Long> orderIdList = orderTaskBeans.stream().map(EmcOrderTaskBean :: getOrderId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> orderList = selectListByFilter(SearchFilter.instance().match("id", orderIdList).filter(MatchPattern.OR));
        if(!orderList.isEmpty()){
            orderList.forEach(o ->{
                o.setExt$Item("phflag","");
            });
            getDao().update(orderList,"phflag");
        }

    }

    @Override
    public Page<EmcOrderContainerBean> selectContainerPagination(RestJsonWrapperBean wrapper) {
        return orderContainerService.selectOrderContainerList(wrapper);
    }

    @Override
    public void addParamOrder(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> taskList = wrapper.parse(EmcOrderTaskBean.class);
        List<Long> orderIdList = taskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> orderList = getDao().selectListByIds(orderIdList);
        EmcOrderBean copiedOrder = orderList.get(0);
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "orderId");
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderIdList, "orderId");
        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "orderId");

        EmcFolderBean folder = folderService.selectById(copiedOrder.getFolderId());
        List<EmcFolderBean> folderList = folderService.getDao().selectListByOneColumnValue(folder.getParamProjId(), "paramProjId");
        List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
        List<EmcOrderBean> folderOrderList = getDao().selectListByOneColumnValues(folderIdList, "folderId");
        long maxNo = folderOrderList.stream().map(EmcOrderBean::getSampleParamOrderNo).filter(NumberUtils::isNumber).map(NumberUtils::parseLong).max(Comparator.comparing(Function.identity())).orElse(0L) + 1L;

        EmcOrderBean newOrder = new EmcOrderBean();
        BeanUtils.copyProperties(copiedOrder, newOrder, EmcConstant.CREATED_COLUMNS);
        newOrder.setId(ApplicationContextHelper.getNextIdentity());
        newOrder.setOrderNo(null);
        newOrder.setSampleParamOrderNo(maxNo + "");
        getDao().insert(newOrder);

        List<EmcOrderTaskBean> newOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> newTaskResultList = new ArrayList<>();
        List<EmcOrderItemGroupBean> newOrderItemGroupList = new ArrayList<>();

        // 复制分析项组
        copyOrderItemGroup(orderItemGroupList, copiedOrder, newOrder.getId(), newOrderItemGroupList);

        // 复制样品测试和结果
        orderTaskList.stream().filter(task -> ObjectUtils.equals(copiedOrder.getId(), task.getOrderId())).forEach(task -> {
            EmcOrderTaskBean newTask = new EmcOrderTaskBean();
            BeanUtils.copyProperties(task, newTask, EmcConstant.CREATED_COLUMNS);
            newTask.setId(ApplicationContextHelper.getNextIdentity());
            newTask.setOrderId(newOrder.getId());
            newOrderTaskList.add(newTask);

            orderTaskResultList.stream().filter(result -> ObjectUtils.equals(result.getOrderTaskId(), task.getId())).forEach(result -> {
                EmcOrderTaskResultBean newResult = new EmcOrderTaskResultBean();
                BeanUtils.copyProperties(result, newResult, EmcConstant.CREATED_COLUMNS);
                newResult.setId(ApplicationContextHelper.getNextIdentity());
                newResult.setOrderTaskId(newTask.getId());
                newResult.setOrderId(newTask.getOrderId());
                newTaskResultList.add(newResult);
            });
        });

        orderTaskService.getDao().insert(newOrderTaskList);
        orderTaskResultService.getDao().insert(newTaskResultList);
        orderItemGroupService.getDao().insert(newOrderItemGroupList);
    }

    @Override
    public void deleteParamOrder(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> taskList = wrapper.parse(EmcOrderTaskBean.class);
        List<Long> orderIdList = taskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> orderList = getDao().selectListByIds(orderIdList);

        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "orderId", Collections.singletonList("id"));
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderIdList, "orderId", Collections.singletonList("id"));
        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "orderId", Collections.singletonList("id"));

        getDao().deleteByIdList(orderIdList);
        orderTaskService.getDao().deleteBy(orderTaskList, "id");
        orderTaskResultService.getDao().deleteBy(orderTaskResultList, "id");
        orderItemGroupService.getDao().deleteBy(orderItemGroupList, "id");

        // 如果点位下的样品都删除了，则需要一并删除点位
        List<Long> folderIdList = orderList.stream().map(EmcOrderBean::getFolderId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> folderOrderList = getDao().selectListByOneColumnValues(folderIdList, "folderId", Arrays.asList("id", "folderId"));
        List<Long> deleteFolderIdList = new ArrayList<>();
        folderIdList.forEach(folderId -> {
            List<EmcOrderBean> curOrderList = folderOrderList.stream().filter(order -> ObjectUtils.equals(order.getFolderId(), folderId)).collect(Collectors.toList());
            if (curOrderList.isEmpty()) {
                deleteFolderIdList.add(folderId);
            }
        });
        folderService.getDao().deleteByIdList(deleteFolderIdList);
    }

    @Override
    public void updateOrderIndex(RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> targetOrderList = wrapper.parse(EmcOrderBean.class);
        List<Long> orderIdList = targetOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());

        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "orderId");
        List<Long> capacityItemGroupIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());

        List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.selectListByIds(capacityItemGroupIdList);
        // 样品指标
        EmcOrderIndexBean orderIndex = wrapper.parse(EmcOrderIndexBean.class).get(0);
        // 指标设置
        List<EmcIndexSetBean> indexSetList = indexSetService.selectListByFilter(SearchFilter.instance().match("indexId", orderIndex.getId()).filter(MatchPattern.EQ));
        // 指标项目
        List<Long> indexItemIdList = indexSetList.stream().map(EmcIndexSetBean::getIndexItemId).collect(Collectors.toList());
        // 指标项目分析项
        List<EmcIndexTestItemBean> indexTestItemList = indexTestItemService.selectListByFilter(SearchFilter.instance().match("indexItemId", indexItemIdList).filter(MatchPattern.OR));
        // 分析项
        List<Long> itemIdList = indexTestItemList.stream().map(EmcIndexTestItemBean::getItemId).distinct().collect(Collectors.toList());
        // 分析项组分析项
        List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemIdList, "itemId");
        // 分析项组
        List<Long> itemGroupIdList = groupItemList.stream().map(EmcGroupItemBean::getItemGroupId).distinct().collect(Collectors.toList());

        List<Long> targetCapacityItemGroupIdList = capacityItemGroupList.stream().filter(cig -> itemGroupIdList.contains(cig.getItemGroupId())).map(EmcCapacityItemGroupBean::getId).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> updateOrderItemGroupList = orderItemGroupList.stream().filter(oig -> targetCapacityItemGroupIdList.contains(oig.getItemGroupId())).collect(Collectors.toList());

        updateOrderItemGroupList.forEach(oig -> {
            oig.setIndexCategoryId(orderIndex.getIndexCategoryId());
            oig.setIndexId(orderIndex.getId());
        });

        orderItemGroupService.getDao().update(updateOrderItemGroupList, "indexCategoryId", "indexId");
    }

    private boolean isSolidWaste(EmcOrderBean order) {
        return Arrays.stream(PfConstant.DIOXIN_SOLID_WASTE_ORDER_CATEGORIES).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));
    }
    @Transactional
    public Long updateItemGroupOrderContainer(RestJsonWrapperBean wrapper) {
        List<EmcOrderItemGroupBean> wrapperOrderItemGroupList = wrapper.parse(EmcOrderItemGroupBean.class);
        List<EmcOrderBean> wrapperOrderList = wrapper.parse(EmcOrderBean.class);
        EmcOrderTaskBean wrapperOrderTask = wrapper.parseUnique(EmcOrderTaskBean.class);
        Long containerCategoryId = wrapperOrderTask.getContainerCategoryId();
        String containerCategoryCode = wrapperOrderTask.getContainerCategoryCode();
        String containerCategoryName = wrapperOrderTask.getContainerCategoryName();
        String containerGroup = wrapperOrderTask.getContainerGroup();
        String fixative = wrapperOrderTask.getFixative();
        String storeCondition = wrapperOrderTask.getStoreCondition();
        EmcOrderItemGroupBean updateOrderItemGroup = new EmcOrderItemGroupBean();
        updateOrderItemGroup.setContainerCategoryId(containerCategoryId);
        updateOrderItemGroup.setContainerCategoryCode(containerCategoryCode);
        updateOrderItemGroup.setContainerCategoryName(containerCategoryName);
        updateOrderItemGroup.setContainerGroup(containerGroup);
        updateOrderItemGroup.setFixative(fixative);
        updateOrderItemGroup.setStoreCondition(storeCondition);
        List<Long> itemGroupIdList = wrapperOrderItemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
        List<Long> orderIdList = wrapperOrderList.stream().map(EmcOrderBean::getId).distinct().collect(Collectors.toList());
        List<EmcOrderItemGroupBean> updateOrderItemGroupList = this.orderItemGroupService.selectListByFilter(SearchFilter.instance().match("orderId", orderIdList).filter(MatchPattern.OR).match("itemGroupId", itemGroupIdList).filter(MatchPattern.OR), new Order[0]);
        List<Long> updateOrderItemGroupIdList = updateOrderItemGroupList.stream().map(EmcOrderItemGroupBean::getId).distinct().collect(Collectors.toList());
        this.orderItemGroupService.getDao().updateByIds(updateOrderItemGroup, updateOrderItemGroupIdList, "ContainerCategoryId", "ContainerCategoryCode", "ContainerCategoryName", "ContainerGroup", "Fixative", "StoreCondition");
        List<EmcOrderBean> orderList = this.selectListByIds(orderIdList);
        this.orderContainerService.deleteContainer(orderList);
        List delOrderTaskList = this.orderTaskService.selectListByFilter(SearchFilter.instance().match("orderId", orderIdList).filter(MatchPattern.OR), new Order[0]);
        List delOrderTaskResultList = this.orderTaskResultService.selectListByFilter(SearchFilter.instance().match("orderId", orderIdList).filter(MatchPattern.OR), new Order[0]);
        this.orderTaskService.getDao().deleteBy(delOrderTaskList, new String[0]);
        this.orderTaskResultService.getDao().deleteBy(delOrderTaskResultList, new String[0]);
        this.orderTaskService.insertByOrder(orderList);
        List<EmcOrderTaskBean> orderTaskList = this.orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID", new Order[0]);
        orderTaskList.forEach(a2 -> a2.setStatus("preLogged"));
        this.orderTaskService.getDao().update(orderTaskList, new String[]{"STATUS"});
        return 1L;
    }

    public List<EmcOrderTaskResultBean> getInsertOrderTaskResultList(EmcOrderTaskBean orderTask,
            List<EmcItemBean> itemList, EmcOrderBean order, List<EmcOrderTaskResultBean> deleteResultList) {
        List<EmcOrderTaskResultBean> resultList = new ArrayList<>();
        String volume = order.getExt$Item("volume");
        if (!StringUtils.isEmpty(volume)) volume = volume.replaceAll("[^0-9.]", "");
        for (EmcItemBean item : itemList) {
            int index = 1;
            while ((long) index <= item.getRepeatItem()) {
                EmcOrderTaskResultBean result = new EmcOrderTaskResultBean();
                BeanUtils.copyProperties(item, result, new String[0]);
                result.setId(ApplicationContextHelper.getNextIdentity());
                result.setFolderId(orderTask.getFolderId());
                result.setOrderId(orderTask.getOrderId());
                result.setProjId(orderTask.getProjId());
                result.setOrderTaskId(orderTask.getId());
                result.setTestMethodId(orderTask.getTestMethodId());
                result.setMethodId(orderTask.getMethodId());
                result.setMethodVersionId(orderTask.getMethodVersionId());
                result.setItemId(item.getId());
                result.setItemOrderNo(item.getOrderNo());
                result.setRepeatNo((long) index);
                result.setRepeatQty(item.getRepeatItem());
                result.setQcCategoryId(orderTask.getQcCategoryId());
                result.setQcCategoryCode(orderTask.getQcCategoryCode());
                result.setQcName(orderTask.getQcName());
                result.setTestId(orderTask.getTestId());
                result.setScriptFlag(item.getScriptFlag());
                result.setResultCategory(item.getResultCategory());
                result.setCycle(order.getCycle());
                result.setOrderNo(order.getOrderNo());
                result.setParentOrderNo(order.getParentOrderNo());
                result.setFrequency(order.getFrequency());
                result.setAnalysisType(item.getAnalysisCategory());
                if (!StringUtils.isEmpty(item.getExt$Item("ISSENDTORESULT")) && !StringUtils.isEmpty(volume)
                        && "1".equals(item.getExt$Item("ISSENDTORESULT"))) {
                    if (!StringUtils.isEmpty(item.getExt$Item("SENDCOEFFICIENT"))) {
                        String value = String.valueOf(ArithUtils.div(volume.trim(), item.getExt$Item("SENDCOEFFICIENT")));
                        result.setOriginalResult(value); result.setReportResult(value);
                    } else {
                        result.setOriginalResult(volume); result.setReportResult(volume);
                    }
                }
                EmcOrderTaskResultBean old = deleteResultList.stream()
                        .filter(existing -> ObjectUtils.equals(existing.getItemName(), item.getItemName()))
                        .findFirst().orElse(null);
                if (old != null) {
                    result.setOriginalResult(old.getOriginalResult());
                    result.setReportResult(old.getReportResult());
                }
                resultList.add(result);
                ++index;
            }
        }
        return resultList;
    }

    @Transactional
    public Long syncOrderItemGroupItem(RestJsonWrapperBean wrapper) {
        List<EmcOrderItemGroupBean> orderItemGroupList = wrapper.parse(EmcOrderItemGroupBean.class);
        List<Long> capacityItemGroupIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
        List<EmcCapacityItemGroupBean> capacityItemGroupList = this.capacityItemGroupService.selectListByIds(capacityItemGroupIdList);
        List<Long> itemGroupIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId).collect(Collectors.toList());
        List<EmcGroupItemBean> groupItemList = this.groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID", new Order[0]);
        ArrayList<EmcOrderItemBean> orderItemList = new ArrayList<EmcOrderItemBean>();
        ArrayList<EmcOrderItemBean> delOrderItemFilterList = new ArrayList<EmcOrderItemBean>();
        for (EmcOrderItemGroupBean orderItemGroup : orderItemGroupList) {
            EmcOrderItemBean delOrderItemFilter = new EmcOrderItemBean();
            delOrderItemFilter.setOrderItemGroupId(orderItemGroup.getId());
            delOrderItemFilterList.add(delOrderItemFilter);
            Long capacityItemGroupId = orderItemGroup.getItemGroupId();
            EmcCapacityItemGroupBean capacityItemGroup = capacityItemGroupList.stream().filter(e -> ObjectUtils.equals(e.getId(), capacityItemGroupId)).findFirst().orElse(null);
            if (capacityItemGroup == null) continue;
            Long itemGroupId = capacityItemGroup.getItemGroupId();
            List<EmcGroupItemBean> curGroupItemList = groupItemList.stream().filter(e -> ObjectUtils.equals(itemGroupId, e.getItemGroupId())).collect(Collectors.toList());
            for (EmcGroupItemBean groupItem : curGroupItemList) {
                EmcOrderItemBean orderItemBean = new EmcOrderItemBean();
                orderItemBean.setId(ApplicationContextHelper.getNextIdentity());
                orderItemBean.setOrderItemGroupId(orderItemGroup.getId());
                orderItemBean.setOrderId(orderItemGroup.getOrderId());
                orderItemBean.setItemId(groupItem.getItemId());
                orderItemList.add(orderItemBean);
            }
        }
        this.orderItemService.getDao().deleteBy(delOrderItemFilterList, new String[]{"ORDERITEMGROUPID"});
        this.orderItemService.getDao().insert(orderItemList);
        return 1L;
    }

    @Transactional
    @ValidateDataWith(value=PfDeleteCheckProjectOrderItemGroupValidator.class)
    public void delCheckProjectOrderItemGroup(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        ArrayList deleteOrderItemGroupList = new ArrayList();
        orderTaskList.forEach(task -> {
            EmcOrderItemGroupBean deleteOrderItemGroup = new EmcOrderItemGroupBean();
            deleteOrderItemGroup.setOrderId(task.getOrderId());
            deleteOrderItemGroup.setTestMethodId(task.getTestMethodId());
            deleteOrderItemGroup.setItemGroup(task.getExt$Item("itemGroup"));
            deleteOrderItemGroupList.add(deleteOrderItemGroup);
        });
        this.orderItemGroupService.getDao().deleteBy(deleteOrderItemGroupList, new String[]{"orderId", "testMethodId", "itemGroup"});
    }

    @Transactional
    public void delOrderItemGroup(RestJsonWrapperBean wrapper) {
        this.orderItemGroupService.delete(wrapper);
    }


}
