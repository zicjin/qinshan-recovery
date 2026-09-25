package com.sunwayworld.emc.module.bp.order.orderreceipt.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
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
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.BeanUtils;
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
        List<Long> copiedItemIdList = copiedOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getItemId).collect(Collectors.toList());
        List<EmcItemBean> copiedItemList = itemService.getDao().selectListByIds(copiedItemIdList);
        List<EmcOrderItemGroupBean> copiedOrderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(copiedOrderIdList, "ORDERID");

        List<EmcOrderBean> newOrderList = new ArrayList<>();
        List<EmcFolderBean> newFolderList = new ArrayList<>();
        List<EmcOrderItemGroupBean> newOrderItemGroupList = new ArrayList<>();
        List<EmcOrderTaskBean> newOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> newOrderTaskResultList = new ArrayList<>();

        List<EmcOrderTaskBean> updateTaskList = new ArrayList<>();

        Long projectId = copiedOrderList.get(0).getProjId();
        List<EmcQcCategoryBean> addFolderQcCategoryList = copiedQcCategoryList.stream().filter(qc -> StringUtils.equals(qc.getAddFolder(), Constant.YES)).collect(Collectors.toList());

        if (!addFolderQcCategoryList.isEmpty()) {
            long totalNum = addFolderQcCategoryList.stream().mapToLong(qc -> Optional.ofNullable(qc.getAddNum()).orElse(1L)).sum();
            List<String> newFolderNoList = folderService.generateFolderNosByProjId(projectId, projectService.selectDetail(projectId).getProjNo(), copiedOrderList.get(0).getMonitorTmplId(), (int) totalNum);

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
                    String newOrderNo = newFolderNo + "01";
                    if(newFolderNo.contains("-")){
                        newOrderNo = newFolderNo + "-01";
                    }
                    copyOrder(copiedOrder, copiedQc, newFolderId, newOrder, newOrderId, newOrderNo, newOrderList);

                    // 复制分析项组
                    copyOrderItemGroup(copiedOrderItemGroupList, copiedOrder, newOrderId, newOrderItemGroupList);

                    // 复制点位
                    copyFolder(copiedFolderList, copiedOrder, copiedQc, newFolderId, newFolderNo, newFolderList);

                    // 复制样品测试和样品测试结果
                    currentOrderTaskList.forEach(copiedOrderTask -> {
                        Long newOrderTaskId = ApplicationContextHelper.getNextIdentity();
                        List<EmcOrderTaskResultBean> currentResultList = copiedOrderTaskResultList.stream().filter(result -> ObjectUtils.equals(result.getOrderTaskId(), copiedOrderTask.getId())).collect(Collectors.toList());
                        currentResultList.forEach(copiedOrderTaskResult -> {
                            EmcItemBean currentItem = copiedItemList.stream().filter(item -> ObjectUtils.equals(item.getId(), copiedOrderTaskResult.getItemId())).findFirst().orElse(null);
                            if (currentItem == null) {
                                return;
                            }

                            if (!StringUtils.isEmpty(currentItem.getQcCategoryId()) && Constant.YES.equals(currentItem.getActivatedFlag()) && ArrayUtils.contains(StringUtils.split(currentItem.getQcCategoryId(), ","), "" + copiedQc.getId())) { {
                                copyOrderTaskResult(copiedOrderTaskResult, newFolderId, newOrderTaskId, newOrder, copiedOrder, copiedQc, newOrderTaskResultList);
                                if (newOrderTaskList.stream().noneMatch(c -> ObjectUtils.equals(c.getId(), newOrderTaskId))) {
                                    copyOrderTask(copiedOrderTask, copiedQc, newOrderTaskId, newFolderId, newOrder, newOrderTaskList);
                                }
                            }}
                        });
                    });
                });
            }));
        }

        List<EmcOrderBean> existedOrderList = orderService.getDao().selectListByOneColumnValues(copiedFolderIdList, "FOLDERID", Arrays.asList("ID", "FOLDERID", "ORDERNO"));
        existedOrderList.forEach(o ->{
            if(o.getOrderNo().contains("-H")){
             o.setOrderNo(o.getOrderNo().replace("-H", ""));
            }
           });
        Map<Long, EmcOrderBean> existedFolderOrderMap = existedOrderList.stream().collect(Collectors.groupingBy(EmcOrderBean::getFolderId, Collectors.collectingAndThen(Collectors.reducing(BinaryOperator.maxBy(Comparator.comparing(EmcOrderBean::getOrderNo))), Optional::get)));
        Map<Long, Integer> existedFolderMaxOrderNoMap = existedFolderOrderMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> NumberUtils.parseInt(e.getValue().getOrderNo().substring(e.getValue().getOrderNo().length() - 3))));

        List<EmcQcCategoryBean> notAddFolderQcCategoryList = copiedQcCategoryList.stream().filter(qc -> !StringUtils.equals(qc.getAddFolder(), Constant.YES)).collect(Collectors.toList());
        copiedOrderList.forEach(copiedOrder -> notAddFolderQcCategoryList.forEach(copiedQc -> {
            Long addNum = Optional.ofNullable(copiedQc.getAddNum()).orElse(1L);
            List<EmcOrderTaskBean> currentOrderTaskList = copiedOrderTaskList.stream().filter(task -> ObjectUtils.equals(task.getOrderId(), copiedOrder.getId())).collect(Collectors.toList());
            LongStream.range(0, addNum).forEach(index -> {
                Integer currentMaxOrderNum = existedFolderMaxOrderNoMap.get(copiedOrder.getFolderId());
                if (currentMaxOrderNum == null) {
                    return;
                }

                // 复制样品
                EmcOrderBean newOrder = new EmcOrderBean();
                BeanUtils.copyProperties(copiedOrder, newOrder, EmcConstant.CREATED_COLUMNS);
                Long newOrderId = ApplicationContextHelper.getNextIdentity();
                int nextOrderNum = currentMaxOrderNum + 1;
                String newOrderNo = copiedOrder.getOrderNo().substring(0, copiedOrder.getOrderNo().length() - 3) + StringUtils.leftPad(nextOrderNum + "", 3);

                // 判断是否为后管
                boolean isBlabQc = PfConstant.QC_L_BLAB.equals(copiedQc.getQcCategoryId());
                // 判断是否为尘氟
                boolean isCfQc = PfConstant.QC_P_CF.equals(copiedQc.getQcCategoryId());
                if (isBlabQc || isCfQc) {
                    newOrderNo = copiedOrder.getOrderNo() + "-" + (isBlabQc ? PfConstant.QC_L_BLAB_ORDER_NO_SUFFIX : PfConstant.QC_P_CF_ORDER_NO_SUFFIX);
                }

                copyOrder(copiedOrder, copiedQc, copiedOrder.getFolderId(), newOrder, newOrderId, newOrderNo, newOrderList);
                existedFolderMaxOrderNoMap.put(copiedOrder.getFolderId(), nextOrderNum);

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

                        if (!StringUtils.isEmpty(currentItem.getQcCategoryId()) && Constant.YES.equals(currentItem.getActivatedFlag()) && ArrayUtils.contains(StringUtils.split(currentItem.getQcCategoryId(), ","), "" + copiedQc.getId())) { {
                            copyOrderTaskResult(copiedOrderTaskResult, copiedOrder.getFolderId(), newOrderTaskId, newOrder, copiedOrder, copiedQc, newOrderTaskResultList);
                            if (newOrderTaskList.stream().noneMatch(c -> ObjectUtils.equals(c.getId(), newOrderTaskId))) {
                                copyOrderTask(copiedOrderTask, copiedQc, newOrderTaskId, copiedOrder.getFolderId(), newOrder, newOrderTaskList);
                            }
                        }}
                    });
                });

                if (isBlabQc || isCfQc) {
                    String acqNoSuffix = "-" + PfConstant.QC_L_ALAB_ORDER_NO_SUFFIX;
                    List<Long> newTestMethodIdList = newOrderTaskList.stream().filter(task -> StringUtils.equals(copiedQc.getQcCategoryId(), task.getQcCategoryCode()) && ObjectUtils.equals(newOrderId, task.getOrderId())).map(EmcOrderTaskBean::getTestMethodId).distinct().collect(Collectors.toList());
                    List<EmcOrderTaskBean> needUpdateTaskList = currentOrderTaskList.stream().filter(task -> newTestMethodIdList.contains(task.getTestMethodId()) && !StringUtils.endsWith(task.getEquiptAcquNo(), acqNoSuffix)).collect(Collectors.toList());
                    needUpdateTaskList.forEach(task -> task.setEquiptAcquNo(copiedOrder.getOrderNo() + acqNoSuffix));
                    updateTaskList.addAll(needUpdateTaskList);
                }

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
}
