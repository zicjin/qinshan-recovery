package com.sunwayworld.emc.module.bp.order.orderreceipt.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderContainerBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderContainerService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.order.orderlabel.bean.EmcOrderLabelBean;
import com.sunwayworld.emc.module.bp.order.orderlabel.service.EmcOrderLabelService;
import com.sunwayworld.emc.module.bp.order.orderreceipt.validator.EmcDeleteOrderValidator;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean.EmcQcCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.service.EmcQcCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcRelatedTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcTestCapacityBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcRelatedTestMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcTestCapacityService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;

/**
 * @author zhengxy@sunwayworld.com 2022年07月21日
 */
@Repository
@GikamBean
public class PfOrderReceiptServiceImpl extends EmcOrderReceiptServiceImpl {

    @Autowired
    private EmcItemService itemService;

    @Autowired
    @Lazy
    private EmcProjectService projectService;

    @Autowired
    private EmcFolderService folderService;

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Autowired
    private EmcOrderService orderService;

    @Autowired
    @Lazy
    private EmcOrderItemGroupService orderItemGroupService;

    @Autowired
    private EmcQcCategoryService qcCategoryService;

    @Autowired
    private EmcOrderContainerService orderContainerService;

    @Autowired
    private EmcOrderLabelService orderLabelService;

    @Autowired
    private EmcTestMethodService testMethodService;

    @Autowired
    @Lazy
    private EmcTestCapacityService testCapacityService;

    @Autowired
    @Lazy
    private EmcCapacityItemGroupService capacityItemGroupService;

    @Autowired
    private EmcGroupItemService groupItemService;

    @Autowired
    private EmcRelatedTestMethodService relatedTestMethodService;

    @Override
    @Transactional
    public void copyQcOrder(RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> copiedOrderList = wrapper.parse(EmcOrderBean.class, 0);
        List<EmcQcCategoryBean> copiedQcCategoryList = wrapper.parse(EmcQcCategoryBean.class, 1);

        List<Long> copiedOrderIdList = copiedOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<Long> copiedFolderIdList = copiedOrderList.stream().map(EmcOrderBean::getFolderId).distinct().collect(Collectors.toList());

        List<EmcFolderBean> copiedFolderList = folderService.getDao().selectListByIds(copiedFolderIdList);
        List<EmcOrderTaskBean> copiedOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(copiedOrderIdList, "ORDERID");
        List<EmcOrderTaskResultBean> copiedOrderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(copiedOrderIdList, "ORDERID");
        List<Long> copiedItemIdList = copiedOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
        List<EmcItemBean> copiedItemList = itemService.getDao().selectListByIds(copiedItemIdList);
        List<EmcOrderItemGroupBean> copiedOrderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(copiedOrderIdList, "ORDERID");
        List<Long> testMethodIdList = copiedOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getTestMethodId).distinct().collect(Collectors.toList());
        List<EmcRelatedTestMethodBean> relatedTestMethodList = relatedTestMethodService.selectListByFilter(
                SearchFilter.instance().match("testMethodId", testMethodIdList).filter(MatchPattern.OR)
                        .match("replaceFlag", "1").filter(MatchPattern.EQ), new Order[0]);

        List<EmcOrderBean> newOrderList = new ArrayList<>();
        List<EmcFolderBean> newFolderList = new ArrayList<>();
        List<EmcOrderItemGroupBean> newOrderItemGroupList = new ArrayList<>();
        List<EmcOrderTaskBean> newOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> newOrderTaskResultList = new ArrayList<>();

        List<EmcOrderTaskBean> updateTaskList = new ArrayList<>();
        List<EmcOrderBean> reloadOrderList = new ArrayList<>();

        Long projectId = copiedOrderList.get(0).getProjId();
        List<EmcQcCategoryBean> addFolderQcCategoryList = copiedQcCategoryList.stream().filter(qc -> StringUtils.equals(qc.getAddFolder(), Constant.YES)).collect(Collectors.toList());

        if (!addFolderQcCategoryList.isEmpty()) {
            long totalNum = addFolderQcCategoryList.stream().mapToLong(qc -> Optional.ofNullable(qc.getAddNum()).orElse(1L)).sum();
            List<String> newFolderNoList = folderService.generateFolderNosByProjId(projectId, projectService.selectDetail(projectId).getProjNo(), copiedFolderList.get(0).getMonitorTmplId(), (int) totalNum);

            AtomicInteger folderNoIndex = new AtomicInteger(-1);
            copiedOrderList.forEach(copiedOrder -> addFolderQcCategoryList.forEach(copiedQc -> {
                Long addNum = Optional.ofNullable(copiedQc.getAddNum()).orElse(1L);
                List<EmcOrderTaskBean> currentOrderTaskList = copiedOrderTaskList.stream().filter(task -> ObjectUtils.equals(task.getOrderId(), copiedOrder.getId())).collect(Collectors.toList());

                LongStream.range(0, addNum).forEach(index -> {
                    // 复制样品
                    EmcOrderBean newOrder = new EmcOrderBean();
                    BeanUtils.copyProperties(copiedOrder, newOrder, EmcConstant.CREATED_COLUMNS);

                    Long newOrderId = ApplicationContextHelper.getNextIdentity();
                    Long newFolderId = ApplicationContextHelper.getNextIdentity();
                    String newFolderNo = newFolderNoList.get(folderNoIndex.incrementAndGet());
                    String newOrderNo = newFolderNo + "001";
                    if ("P-BLANK".equals(copiedQc.getQcCategoryId())) {
                        newOrderNo = newFolderNo.substring(0, newFolderNo.length() - 3) + "QK" + newFolderNo.substring(newFolderNo.length() - 3) + "001";
                    }
                    if ("P-YSKB".equals(copiedQc.getQcCategoryId())) {
                        newOrderNo = newFolderNo.substring(0, newFolderNo.length() - 3) + "YK" + newFolderNo.substring(newFolderNo.length() - 3) + "001";
                    }
                    if ("zcsbkb".equals(copiedQc.getQcCategoryId())) {
                        newOrderNo = newFolderNo.substring(0, newFolderNo.length() - 3) + "BK" + newFolderNo.substring(newFolderNo.length() - 3) + "001";
                    }
                    if ("Q-DUP".equals(copiedQc.getQcCategoryId())) {
                        newOrderNo = newFolderNo.substring(0, newFolderNo.length() - 3) + "MP" + newFolderNo.substring(newFolderNo.length() - 3) + "001";
                    }
                    if (newFolderNo.contains("-")) {
                        newOrderNo = newFolderNo + "-001";
                    }
                    copyOrder(copiedOrder, copiedQc, newFolderId, newOrder, newOrderId, newOrderNo, newOrderList);

                    // 复制点位
                    copyFolder(copiedFolderList, copiedOrder, copiedQc, newFolderId, newFolderNo, newFolderList);

                    // 复制样品测试和样品测试结果
                    if ("zcsbkb".equals(copiedQc.getQcCategoryId()) && !CollectionUtils.isEmpty(relatedTestMethodList)) {
                        List<EmcOrderItemGroupBean> relatedGroups = new ArrayList<>();
                        List<Long> testMethodIds = currentOrderTaskList.stream().map(EmcOrderTaskBean::getTestMethodId).distinct().collect(Collectors.toList());
                        List<EmcRelatedTestMethodBean> matchedRelatedMethods = relatedTestMethodList.stream()
                                .filter(method -> testMethodIds.contains(method.getTestMethodId())).collect(Collectors.toList());
                        List<Long> relatedTestMethodIds = matchedRelatedMethods.stream().map(EmcRelatedTestMethodBean::getRelatedTestMethodId)
                                .distinct().collect(Collectors.toList());
                        if (!relatedTestMethodIds.isEmpty()) {
                            for (Long relatedTestMethodId : relatedTestMethodIds) {
                                List<EmcTestMethodBean> testMethodList = testMethodService.selectListByFilter(
                                        SearchFilter.instance().match("id", relatedTestMethodId).filter(MatchPattern.SEQ), new Order[0]);
                                String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
                                List<EmcTestCapacityBean> relatedCapacityList = testCapacityService.selectListByFilter(
                                        SearchFilter.instance().match("testMethodId", relatedTestMethodId).filter(MatchPattern.SEQ)
                                                .match("orgId", parentOrgId).filter(MatchPattern.SEQ), new Order[0]);
                                List<Long> testCapacityIds = relatedCapacityList.stream().map(EmcTestCapacityBean::getId).collect(Collectors.toList());
                                Map<Long, EmcTestCapacityBean> relatedCapacityMap = relatedCapacityList.stream()
                                        .collect(Collectors.toMap(EmcTestCapacityBean::getId, Function.identity()));
                                List<EmcCapacityItemGroupBean> relatedItemGroupList = capacityItemGroupService.getDao()
                                        .selectListByOneColumnValues(testCapacityIds, "testCapacityId", new Order[0]);
                                List<Long> currentMethodIds = matchedRelatedMethods.stream()
                                        .filter(method -> ObjectUtils.equals(relatedTestMethodId, method.getRelatedTestMethodId()))
                                        .map(EmcRelatedTestMethodBean::getTestMethodId).distinct().collect(Collectors.toList());
                                List<EmcOrderItemGroupBean> currentCopiedGroups = copiedOrderItemGroupList.stream()
                                        .filter(group -> ObjectUtils.equals(group.getOrderId(), copiedOrder.getId())
                                                && currentMethodIds.contains(group.getTestMethodId())).collect(Collectors.toList());
                                List<String> groupNames = currentCopiedGroups.stream().map(EmcOrderItemGroupBean::getItemGroup)
                                        .distinct().collect(Collectors.toList());
                                List<EmcCapacityItemGroupBean> newRelatedItemGroupList = relatedItemGroupList.stream()
                                        .filter(group -> !currentCopiedGroups.contains(group.getId()) && groupNames.contains(group.getItemGroup()))
                                        .collect(Collectors.toList());
                                List<Long> newItemGroupIds = newRelatedItemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId)
                                        .distinct().collect(Collectors.toList());
                                List<EmcGroupItemBean> groupItemList = groupItemService.getDao()
                                        .selectListByOneColumnValues(newItemGroupIds, "itemGroupId", new Order[0]);
                                List<Long> itemIds = groupItemList.stream().map(EmcGroupItemBean::getItemId).distinct().collect(Collectors.toList());
                                List<EmcItemBean> itemList = itemService.getDao().selectListByOneColumnValues(itemIds, "id",
                                        Arrays.asList("id", "analysisCategory"), new Order[0]);
                                List<Long> validCapacityItemGroupIds = new ArrayList<>();
                                newRelatedItemGroupList.forEach(group -> {
                                    List<Long> currentItemIds = groupItemList.stream()
                                            .filter(groupItem -> ObjectUtils.equals(group.getItemGroupId(), groupItem.getItemGroupId()))
                                            .map(EmcGroupItemBean::getItemId).distinct().collect(Collectors.toList());
                                    boolean allSamplingItem = itemList.stream().filter(item -> currentItemIds.contains(item.getId()))
                                            .allMatch(item -> StringUtils.equals(item.getAnalysisCategory(), "sampling"));
                                    if (!allSamplingItem) {
                                        validCapacityItemGroupIds.add(group.getId());
                                    }
                                });
                                List<EmcCapacityItemGroupBean> validCapacityItemGroupList = newRelatedItemGroupList.stream()
                                        .filter(group -> validCapacityItemGroupIds.contains(group.getId())).collect(Collectors.toList());
                                validCapacityItemGroupList.forEach(related -> {
                                    EmcOrderItemGroupBean relatedOrderItemGroup = new EmcOrderItemGroupBean();
                                    relatedOrderItemGroup.setItemGroup(related.getItemGroup());
                                    relatedOrderItemGroup.setItemGroupId(related.getId());
                                    relatedOrderItemGroup.setSchemeName("default");
                                    testMethodList.stream().filter(method -> ObjectUtils.equals(related.getTestMethodId(), method.getId()))
                                            .findFirst().ifPresent(method -> {
                                                relatedOrderItemGroup.setMethodId(method.getMethodId());
                                                relatedOrderItemGroup.setTestId(method.getTestId());
                                                relatedOrderItemGroup.setTestMethodId(method.getId());
                                                relatedOrderItemGroup.setTestName(method.getExt$Item("testName"));
                                                relatedOrderItemGroup.setMethodName(method.getExt$Item("methodName"));
                                                relatedOrderItemGroup.setTestMethodBasis(method.getExt$Item("testMethodBasis"));
                                                String methodVersionId = method.getExt$Item("methodVersionId");
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
                                    relatedOrderItemGroup.setExt$Item("relatedTest", "1");
                                    relatedOrderItemGroup.setOrderId(newOrder.getId());
                                    Long orderItemGroupId = ApplicationContextHelper.getNextIdentity();
                                    relatedOrderItemGroup.setId(orderItemGroupId);
                                    relatedGroups.add(relatedOrderItemGroup);
                                });
                            }
                            orderItemGroupService.insert(Collections.singletonList(newOrder), relatedGroups);
                            reloadOrderList.add(newOrder);
                        }
                    } else {
                        currentOrderTaskList.forEach(copiedOrderTask -> {
                            Long newOrderTaskId = ApplicationContextHelper.getNextIdentity();
                            List<EmcOrderTaskResultBean> currentResultList = copiedOrderTaskResultList.stream()
                                    .filter(result -> ObjectUtils.equals(result.getOrderTaskId(), copiedOrderTask.getId()))
                                    .collect(Collectors.toList());
                            currentResultList.forEach(copiedOrderTaskResult -> {
                                EmcItemBean currentItem = copiedItemList.stream()
                                        .filter(item -> ObjectUtils.equals(item.getId(), copiedOrderTaskResult.getItemId()))
                                        .findFirst().orElse(null);
                                if (currentItem == null) {
                                    return;
                                }
                                if (!StringUtils.isEmpty(currentItem.getQcCategoryId())
                                        && Constant.YES.equals(currentItem.getActivatedFlag())
                                        && ArrayUtils.contains(StringUtils.split(currentItem.getQcCategoryId(), ","), "" + copiedQc.getId())) {
                                    copyOrderTaskResult(copiedOrderTaskResult, newFolderId, newOrderTaskId, newOrder,
                                            copiedOrder, copiedQc, newOrderTaskResultList);
                                    if (newOrderTaskList.stream().noneMatch(c -> ObjectUtils.equals(c.getId(), newOrderTaskId))) {
                                        copyOrderTask(copiedOrderTask, copiedQc, newOrderTaskId, newFolderId, newOrder, newOrderTaskList);
                                    }
                                }
                            });
                        });
                        List<EmcOrderItemGroupBean> currentCopiedGroups = copiedOrderItemGroupList.stream()
                                .filter(group -> newOrderTaskList.stream()
                                        .anyMatch(task -> ObjectUtils.equals(task.getTestMethodId(), group.getTestMethodId())))
                                .collect(Collectors.toList());
                        copyOrderItemGroup(currentCopiedGroups, copiedOrder, newOrderId, newOrderItemGroupList);
                    }
                });
            }));
        }

        if (!CollectionUtils.isEmpty(reloadOrderList)) {
            orderTaskService.insertByOrder(reloadOrderList);
        }

        List<EmcOrderBean> existedOrderList = orderService.getDao().selectListByOneColumnValues(copiedFolderIdList, "FOLDERID", Arrays.asList("ID", "FOLDERID", "ORDERNO", "QCCATEGORYID", "QCCATEGORYCODE"));
        List<EmcQcCategoryBean> notAddFolderQcCategoryList = copiedQcCategoryList.stream().filter(qc -> !StringUtils.equals(qc.getAddFolder(), Constant.YES)).collect(Collectors.toList());
        copiedOrderList.forEach(copiedOrder -> notAddFolderQcCategoryList.forEach(copiedQc -> {
            Long addNum = Optional.ofNullable(copiedQc.getAddNum()).orElse(1L);
            List<EmcOrderTaskBean> currentOrderTaskList = copiedOrderTaskList.stream().filter(task -> ObjectUtils.equals(task.getOrderId(), copiedOrder.getId())).collect(Collectors.toList());
            LongStream.range(0, addNum).forEach(index -> {
                List<EmcOrderBean> existedFolderOrderList = existedOrderList.stream()
                        .filter(order -> ObjectUtils.equals(copiedOrder.getFolderId(), order.getFolderId())).collect(Collectors.toList());
                if (existedFolderOrderList.isEmpty()) {
                    return;
                }

                // 复制样品
                EmcOrderBean newOrder = new EmcOrderBean();
                BeanUtils.copyProperties(copiedOrder, newOrder, EmcConstant.CREATED_COLUMNS);
                Long newOrderId = ApplicationContextHelper.getNextIdentity();
                String newOrderNo = genQcOrderNo(copiedQc, copiedOrder, existedFolderOrderList);
                copyOrder(copiedOrder, copiedQc, copiedOrder.getFolderId(), newOrder, newOrderId, newOrderNo, newOrderList);
                newOrder.setQcCategoryId(copiedQc.getId());
                newOrder.setQcCategoryCode(copiedQc.getQcCategoryId());
                newOrder.setQcName(copiedQc.getQcName());
                existedOrderList.add(newOrder);

                // 复制分析项组
                copyOrderItemGroup(copiedOrderItemGroupList, copiedOrder, newOrderId, newOrderItemGroupList);

                // 复制样品测试和样品测试结果
                currentOrderTaskList.forEach(copiedOrderTask -> {
                    Long newOrderTaskId = ApplicationContextHelper.getNextIdentity();
                    List<EmcOrderTaskResultBean> currentResultList = copiedOrderTaskResultList.stream().filter(result -> ObjectUtils.equals(result.getOrderTaskId(), copiedOrderTask.getId())).collect(Collectors.toList());
                    currentResultList.forEach(copiedOrderTaskResult -> {
                        EmcItemBean currentItem = copiedItemList.stream().filter(item -> ObjectUtils.equals(item.getId(), copiedOrderTaskResult.getItemId())).findFirst().orElse(null);
                        if (currentItem == null) {
                            return;
                        }

                        if (!StringUtils.isEmpty(currentItem.getQcCategoryId()) && Constant.YES.equals(currentItem.getActivatedFlag()) && ArrayUtils.contains(StringUtils.split(currentItem.getQcCategoryId(), ","), "" + copiedQc.getId())) {
                            copyOrderTaskResult(copiedOrderTaskResult, copiedOrder.getFolderId(), newOrderTaskId, newOrder, copiedOrder, copiedQc, newOrderTaskResultList);
                            if (newOrderTaskList.stream().noneMatch(c -> ObjectUtils.equals(c.getId(), newOrderTaskId))) {
                                copyOrderTask(copiedOrderTask, copiedQc, newOrderTaskId, copiedOrder.getFolderId(), newOrder, newOrderTaskList);
                            }
                        }
                    });
                });
            });
        }));

        // 生成样品容器信息
        orderContainerService.createOrderContainer(newOrderList, newOrderTaskList);

        // 新增
        orderService.getDao().insert(newOrderList);
        folderService.getDao().insert(newFolderList);
        orderTaskService.getDao().insert(newOrderTaskList);
        orderTaskResultService.getDao().insert(newOrderTaskResultList);
        orderItemGroupService.getDao().insert(newOrderItemGroupList);

        // 更新
        orderTaskService.getDao().update(updateTaskList, "equiptAcquNo");
    }

    @Override
    @Transactional
    public void deleteQcOrder(RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskBeanList = new ArrayList<>();
        List<EmcOrderTaskResultBean> orderTaskResultBeanList = new ArrayList<>();
        List<EmcOrderItemGroupBean> orderItemGroupList = new ArrayList<>();

        orderIdList.forEach(t -> {
            EmcOrderTaskBean taskBean = new EmcOrderTaskBean();
            taskBean.setOrderId(t);
            orderTaskBeanList.add(taskBean);

            EmcOrderTaskResultBean resultBean = new EmcOrderTaskResultBean();
            resultBean.setOrderId(t);
            orderTaskResultBeanList.add(resultBean);

            EmcOrderItemGroupBean orderItemGroup = new EmcOrderItemGroupBean();
            orderItemGroup.setOrderId(t);
            orderItemGroupList.add(orderItemGroup);
        });

        orderList = orderService.selectListByIds(orderIdList);

        List<Long> qcIdList = orderList.stream().map(EmcOrderBean::getQcCategoryId).collect(Collectors.toList());
        List<EmcQcCategoryBean> qcList = qcCategoryService.selectListByIds(qcIdList);
        List<EmcOrderBean> neworderList = new ArrayList<>();
        for (EmcQcCategoryBean qcBean : qcList) {
            for (EmcOrderBean orderBean : orderList) {
                if (orderBean.getQcCategoryId().equals(qcBean.getId()) && "1".equals(qcBean.getAddFolder())) {
                    neworderList.add(orderBean);
                }
            }
        }
        List<Long> folderIdList = neworderList.stream().map(EmcOrderBean::getFolderId).collect(Collectors.toList());
        orderService.getDao().deleteByIdList(orderIdList);
        folderService.getDao().deleteByIdList(folderIdList);
        orderTaskService.getDao().deleteBy(orderTaskBeanList, "ORDERID");
        orderTaskResultService.getDao().deleteBy(orderTaskResultBeanList, "ORDERID");
        orderItemGroupService.getDao().deleteBy(orderItemGroupList, "ORDERID");

        // 删除容器信息
        orderContainerService.deleteContainer(orderList);

        // 若点位下没有样品，则删除此点位
        List<Long> folderIds = orderList.stream().map(EmcOrderBean::getFolderId).collect(Collectors.toList());
        List<Long> deleteFolderIds = new ArrayList<>();
        for(Long folderId: folderIds) {
            List<EmcOrderBean> list = orderService.getDao().selectListByOneColumnValue(folderId, "FOLDERID");
            if (list == null || list.size() == 0) {
                deleteFolderIds.add(folderId);
            }
        }
        folderService.getDao().deleteByIdList(deleteFolderIds);

        // 判断删除的是否为后管或者尘氟样品，则需要更新父样的仪器采集编号
        List<String> blabOrderNoList = orderList.stream().filter(order -> PfConstant.QC_L_BLAB.equals(order.getQcCategoryCode())
                || PfConstant.QC_P_CF.equals(order.getQcCategoryCode())).map(EmcOrderBean::getOrderNo).collect(Collectors.toList());
        if (!blabOrderNoList.isEmpty()) {
            List<String> parentOrderNoList = blabOrderNoList.stream().map(orderNo -> StringUtils.replace(orderNo, "-" + PfConstant.QC_L_BLAB_ORDER_NO_SUFFIX, ""))
                    .map(orderNo -> StringUtils.replace(orderNo, "-" + PfConstant.QC_P_CF_ORDER_NO_SUFFIX, "")).collect(Collectors.toList());

            List<EmcOrderBean> parentOrderList = orderService.getDao().selectListByOneColumnValues(parentOrderNoList, "orderNo", Arrays.asList("id", "orderNo"));
            List<Long> parentOrderIdList = parentOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
            List<EmcOrderTaskBean> parentOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(parentOrderIdList, "orderId", Arrays.asList("id", "equiptAcquNo"));
            String acqNoSuffix = "-" + PfConstant.QC_L_ALAB_ORDER_NO_SUFFIX;
            List<EmcOrderTaskBean> updateTaskList = parentOrderTaskList.stream().filter(task -> StringUtils.endsWith(task.getEquiptAcquNo(), acqNoSuffix)).collect(Collectors.toList());
            updateTaskList.forEach(task -> task.setEquiptAcquNo(StringUtils.replace(task.getEquiptAcquNo(), acqNoSuffix, "")));
            orderTaskService.getDao().update(updateTaskList, "equiptAcquNo");
        }
    }

    @Override
    @Transactional
    @ValidateDataWith(EmcDeleteOrderValidator.class)
    public void deleteOrder(RestJsonWrapperBean wrapper) {
        super.deleteOrder(wrapper);

        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        orderContainerService.deleteContainer(orderList);
    }

    @Override
    public Integer matchBarcode(RestJsonWrapperBean wrapper) {
        List<EmcSampleBean> emcSampleBeanList = wrapper.parse(EmcSampleBean.class);
        List<Long> sampleIdList = emcSampleBeanList.stream().map(EmcSampleBean::getId).collect(Collectors.toList());
        List<EmcOrderBean> emcOrderList = orderService.selectListByFilter(SearchFilter.instance().match("SAMPLEID", sampleIdList).filter(MatchPattern.OR));
        List<Long> orderIdList = emcOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());

        List<EmcOrderContainerBean> orderContainerList = orderContainerService.getDao().selectListByOneColumnValues(orderIdList, "orderId");
        List<String> orderContainerNoList = orderContainerList.stream().filter(oc -> !Constant.YES.equals(oc.getIsScan())).map(EmcOrderContainerBean::getOrderContainerNo).collect(Collectors.toList());
        if (!orderContainerNoList.isEmpty()) {
            List<EmcOrderLabelBean> labelList = orderLabelService.selectListByFilter(SearchFilter.instance().match("LABELCODE", orderContainerNoList).filter(MatchPattern.OR));
            if (!labelList.isEmpty()) {
                List<String> labelCodeList = labelList.stream().map(EmcOrderLabelBean::getLabelCode).collect(Collectors.toList());
                List<EmcOrderContainerBean> updateOrderContainerList = orderContainerList.stream().filter(oc -> labelCodeList.contains(oc.getOrderContainerNo())).collect(Collectors.toList());
                updateOrderContainerList.forEach(oc -> {
                    oc.setIsScan(Constant.YES);
                    oc.setAcceptedDate(LocalDateTime.now());
                    oc.setAcceptedUserId(LocalContextHelper.getLoginUserId());
                    oc.setAcceptedUserName(LocalContextHelper.getLoginUserName());
                });

                orderContainerService.getDao().update(updateOrderContainerList, "isScan", "acceptedDate", "acceptedUserId", "acceptedUserName");

                return updateOrderContainerList.size();
            }
        }

        return 0;
    }
    private static int getQCNextOrderNum(List<EmcOrderBean> existedOrderList, String qcShortHand) {
        int nextOrderNum = 0;
        List<EmcOrderBean> existedQcOrderList = existedOrderList.stream()
                .filter(order -> order.getOrderNo().contains(qcShortHand)).collect(Collectors.toList());
        if (!existedQcOrderList.isEmpty()) {
            EmcOrderBean maxExistedQcOrder = existedQcOrderList.stream()
                    .max(Comparator.comparing(EmcOrderBean::getOrderNo)).orElse(null);
            nextOrderNum = NumberUtils.parseInt(maxExistedQcOrder.getOrderNo()
                    .substring(maxExistedQcOrder.getOrderNo().length() - 3));
        }
        return ++nextOrderNum;
    }

    private static int getNormalNextOrderNum(List<EmcOrderBean> existedOrderList) {
        int nextOrderNum = 0;
        List<EmcOrderBean> normalOrders = existedOrderList.stream()
                .filter(order -> order.getQcCategoryId() == null || "Q-DUP".equals(order.getQcCategoryCode()))
                .collect(Collectors.toList());
        if (!normalOrders.isEmpty()) {
            EmcOrderBean maxOrder = normalOrders.stream().max(Comparator.comparing(EmcOrderBean::getOrderNo)).orElse(null);
            nextOrderNum = NumberUtils.parseInt(maxOrder.getOrderNo().substring(maxOrder.getOrderNo().length() - 3));
        }
        return ++nextOrderNum;
    }

    public String genQcOrderNo(EmcQcCategoryBean qcCategory, EmcOrderBean copyOrder, List<EmcOrderBean> existedOrderList) {
        if ("P-DUP".equals(qcCategory.getQcCategoryId())) {
            String qcShortHand = "XP";
            if (!StringUtils.isEmpty(qcCategory.getQcShortHand())) {
                qcShortHand = qcCategory.getQcShortHand();
            }
            return copyOrder.getOrderNo() + qcShortHand;
        }
        if ("P-VOCS".equals(qcCategory.getQcCategoryId())) {
            String qcShortHand = "CT";
            if (!StringUtils.isEmpty(qcCategory.getQcShortHand())) {
                qcShortHand = qcCategory.getQcShortHand();
            }
            return copyOrder.getOrderNo() + qcShortHand;
        }
        if ("P-BLANK".equals(qcCategory.getQcCategoryId())) {
            String qcShortHand = "QK";
            if (!StringUtils.isEmpty(qcCategory.getQcShortHand())) {
                qcShortHand = qcCategory.getQcShortHand();
            }
            int nextOrderNum = getQCNextOrderNum(existedOrderList, qcShortHand);
            String folderNum = copyOrder.getOrderNo().substring(copyOrder.getOrderNo().length() - 6, copyOrder.getOrderNo().length() - 3);
            return copyOrder.getExt$Item("projno") + copyOrder.getExt$Item("ordercategorycode") + qcShortHand + folderNum + StringUtils.leftPad((nextOrderNum + ""), 3);
        }
        if ("P-YSKB".equals(qcCategory.getQcCategoryId())) {
            String qcShortHand = "YK";
            if (!StringUtils.isEmpty(qcCategory.getQcShortHand())) {
                qcShortHand = qcCategory.getQcShortHand();
            }
            int nextOrderNum = getQCNextOrderNum(existedOrderList, qcShortHand);
            String folderNum = copyOrder.getOrderNo().substring(copyOrder.getOrderNo().length() - 6, copyOrder.getOrderNo().length() - 3);
            return copyOrder.getExt$Item("projno") + copyOrder.getExt$Item("ordercategorycode") + qcShortHand + folderNum + StringUtils.leftPad((nextOrderNum + ""), 3);
        }
        if ("zcsbkb".equals(qcCategory.getQcCategoryId())) {
            String qcShortHand = "BK";
            if (!StringUtils.isEmpty(qcCategory.getQcShortHand())) {
                qcShortHand = qcCategory.getQcShortHand();
            }
            int nextOrderNum = getQCNextOrderNum(existedOrderList, qcShortHand);
            String folderNum = copyOrder.getOrderNo().substring(copyOrder.getOrderNo().length() - 6, copyOrder.getOrderNo().length() - 3);
            return copyOrder.getExt$Item("projno") + copyOrder.getExt$Item("ordercategorycode") + qcShortHand + folderNum + StringUtils.leftPad((nextOrderNum + ""), 3);
        }
        if ("Q-DUP".equals(qcCategory.getQcCategoryId())) {
            String qcShortHand = "MP";
            if (!StringUtils.isEmpty(qcCategory.getQcShortHand())) {
                qcShortHand = qcCategory.getQcShortHand();
            }
            int nextOrderNum = getQCNextOrderNum(existedOrderList, qcShortHand);
            String folderNum = copyOrder.getOrderNo().substring(copyOrder.getOrderNo().length() - 6, copyOrder.getOrderNo().length() - 3);
            return copyOrder.getExt$Item("projno") + copyOrder.getExt$Item("ordercategorycode") + qcShortHand + folderNum + StringUtils.leftPad((nextOrderNum + ""), 3);
        }
        return copyOrder.getOrderNo().substring(0, copyOrder.getOrderNo().length() - 3)
                + StringUtils.leftPad((getNormalNextOrderNum(existedOrderList) + ""), 3);
    }
}
