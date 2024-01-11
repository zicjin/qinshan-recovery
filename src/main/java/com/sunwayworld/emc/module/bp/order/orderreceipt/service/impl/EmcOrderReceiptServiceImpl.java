package com.sunwayworld.emc.module.bp.order.orderreceipt.service.impl;

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
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.order.orderlabel.bean.EmcOrderLabelBean;
import com.sunwayworld.emc.module.bp.order.orderlabel.service.EmcOrderLabelService;
import com.sunwayworld.emc.module.bp.order.orderreceipt.service.EmcOrderReceiptService;
import com.sunwayworld.emc.module.bp.order.orderreceipt.validator.EmcDeleteOrderValidator;
import com.sunwayworld.emc.module.bp.order.orderretain.service.EmcOrderRetainService;
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
import com.sunwayworld.framework.data.page.Page;
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
 * @author zhengxy@sunwayworld.com 2019年10月14日
 */
@Repository
@GikamBean
public class EmcOrderReceiptServiceImpl implements EmcOrderReceiptService {

    @Autowired
    private EmcQcCategoryService qcCategoryService;

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
    private EmcOrderRetainService orderRetainService;

    @Autowired
    private EmcOrderService orderService;

    @Autowired
    private EmcOrderLabelService orderLabelService;

    @Autowired
    @Lazy
    private EmcOrderItemGroupService orderItemGroupService;

    @Override
    public Page<EmcQcCategoryBean> selectQcCategoryList(String category, RestJsonWrapperBean wrapper) {
        return qcCategoryService.selectPaginationByFilter(SearchFilter.instance().match("USECATEGORY", category).filter(MatchPattern.EQ), wrapper);
    }

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

        Long projectId = copiedOrderList.get(0).getProjId();
        List<EmcQcCategoryBean> addFolderQcCategoryList = copiedQcCategoryList.stream().filter(qc -> StringUtils.equals(qc.getAddFolder(), Constant.YES)).collect(Collectors.toList());

        if (!addFolderQcCategoryList.isEmpty()) {
            long totalNum = addFolderQcCategoryList.stream().mapToLong(qc -> Optional.ofNullable(qc.getAddNum()).orElse(1L)).sum();
            List<String> newFolderNoList = folderService.generateFolderNosByProjId(projectId, projectService.selectDetail(projectId).getProjNo(), (int) totalNum);

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
                    if(newFolderNo.contains("-")){
                        newOrderNo = newFolderNo + "-001";
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
            });
        }));

        // 新增
        orderService.getDao().insert(newOrderList);
        folderService.getDao().insert(newFolderList);
        orderTaskService.getDao().insert(newOrderTaskList);
        orderTaskResultService.getDao().insert(newOrderTaskResultList);
        orderItemGroupService.getDao().insert(newOrderItemGroupList);
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
    }

    @Override
    @Transactional
    public Integer matchBarcode(RestJsonWrapperBean wrapper) {
        List<EmcSampleBean> emcSampleBeanList = wrapper.parse(EmcSampleBean.class);
        List<Long> sampleIdList = emcSampleBeanList.stream().map(EmcSampleBean::getId).collect(Collectors.toList());
        List<EmcOrderBean> emcOrderBeanList = orderService.selectListByFilter(SearchFilter.instance().match("SAMPLEID", sampleIdList).filter(MatchPattern.OR));
        List<String> labelNoList = emcOrderBeanList.stream().map(EmcOrderBean::getLabelNo).filter(s -> !StringUtils.isEmpty(s)).collect(Collectors.toList());
        if (!labelNoList.isEmpty()) {
            List<EmcOrderLabelBean> labelBeanList = orderLabelService.selectListByFilter(SearchFilter.instance().match("LABELCODE", labelNoList).filter(MatchPattern.OR));
            if (!labelBeanList.isEmpty()) {
                labelBeanList.forEach(bean -> bean.setMatch("1"));
                orderLabelService.updateIfChanged(labelBeanList);
                return labelBeanList.size();
            }
        }
        return 0;
    }

    @Override
    @Transactional
    public void retainOrder(RestJsonWrapperBean wrapper) {
        orderRetainService.retainOrder(wrapper);
    }

    @Override
    @Transactional
    @ValidateDataWith(EmcDeleteOrderValidator.class)
    public void deleteOrder(RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskBeanList = new ArrayList<>();
        List<EmcOrderTaskResultBean> orderTaskResultBeanList = new ArrayList<>();
        orderIdList.forEach(t -> {
            EmcOrderTaskBean taskBean = new EmcOrderTaskBean();
            taskBean.setOrderId(t);
            orderTaskBeanList.add(taskBean);
            EmcOrderTaskResultBean resultBean = new EmcOrderTaskResultBean();
            resultBean.setOrderId(t);
            orderTaskResultBeanList.add(resultBean);
        });
        orderList = orderService.selectListByIds(orderIdList);
        orderService.getDao().deleteByIdList(orderIdList);
        orderTaskService.getDao().deleteBy(orderTaskBeanList, "ORDERID");
        orderTaskResultService.getDao().deleteBy(orderTaskResultBeanList, "ORDERID");

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
    }

    protected void copyFolder(List<EmcFolderBean> copiedFolderList, EmcOrderBean copiedOrder, EmcQcCategoryBean copiedQc, Long newFolderId, String newFolderNo,  List<EmcFolderBean> newFolderList) {
        copiedFolderList.stream().filter(folder -> ObjectUtils.equals(folder.getId(), copiedOrder.getFolderId())).forEach(folder -> {
            EmcFolderBean newFolder = new EmcFolderBean();
            BeanUtils.copyProperties(folder, newFolder, EmcConstant.CREATED_COLUMNS);
            newFolder.setId(newFolderId);
            newFolder.setFolderNo(newFolderNo);
            newFolder.setMonitorPointName(copiedQc.getQcName());

            // 现场空白不复制监测点名称和编号
            if (EmcConstant.QCCATEGORY_QCCATEGORYID_P_BLANK.equals(copiedQc.getQcCategoryId())) {
                newFolder.setMonitorPointNo("");
                newFolder.setMonitorPointName(copiedQc.getQcName());
            } else if (EmcConstant.QCCATEGORY_QCCATEGORYID_P_DUP.equals(copiedQc.getQcCategoryId())) {
                newFolder.setMonitorPointName((StringUtils.isEmpty(folder.getMonitorPointName()) ? "" : folder.getMonitorPointName()) + copiedQc.getQcName());
            }

            newFolderList.add(newFolder);
        });
    }

    protected void copyOrder(EmcOrderBean copiedOrder, EmcQcCategoryBean copiedQc, Long folderId, EmcOrderBean newOrder, Long newOrderId, String newOrderNo, List<EmcOrderBean> newOrderList) {
        newOrder.setId(newOrderId);
        newOrder.setFolderId(folderId);
        newOrder.setOrderNo(newOrderNo);
        newOrder.setLabelNo(newOrderNo);
        newOrder.setQcCategoryId(copiedQc.getId());
        newOrder.setQcCategoryCode(copiedQc.getQcCategoryId());
        newOrder.setQcName(copiedQc.getQcName());
        if (StringUtils.equals(Constant.YES, copiedQc.getUpdateParNo())) {
            newOrder.setParentOrderNo(copiedOrder.getOrderNo());
        }
        newOrderList.add(newOrder);
    }

    protected void copyOrderTask(EmcOrderTaskBean copiedOrderTask, EmcQcCategoryBean copiedQc, Long newOrderTaskId, Long folderId, EmcOrderBean newOrder, List<EmcOrderTaskBean> newOrderTaskList) {
        EmcOrderTaskBean newOrderTask = new EmcOrderTaskBean();
        BeanUtils.copyProperties(copiedOrderTask, newOrderTask, EmcConstant.CREATED_COLUMNS);
        newOrderTask.setId(newOrderTaskId);
        newOrderTask.setFolderId(folderId);
        newOrderTask.setOrderId(newOrder.getId());
        newOrderTask.setQcCategoryId(copiedQc.getId());
        newOrderTask.setQcCategoryCode(copiedQc.getQcCategoryId());
        newOrderTask.setQcName(copiedQc.getQcName());
        newOrderTask.setEquiptAcquNo(newOrder.getOrderNo());
        newOrderTask.setParentId(copiedOrderTask.getId());
        newOrderTaskList.add(newOrderTask);
    }

    protected void copyOrderTaskResult(EmcOrderTaskResultBean copiedOrderTaskResult, Long folderId, Long newOrderTaskId, EmcOrderBean newOrder, EmcOrderBean copiedOrder, EmcQcCategoryBean copiedQc, List<EmcOrderTaskResultBean> newOrderTaskResultList) {
        EmcOrderTaskResultBean newOrderTaskResult = new EmcOrderTaskResultBean();
        BeanUtils.copyProperties(copiedOrderTaskResult, newOrderTaskResult, EmcConstant.CREATED_COLUMNS);
        newOrderTaskResult.setId(ApplicationContextHelper.getNextIdentity());
        newOrderTaskResult.setOrderTaskId(newOrderTaskId);
        newOrderTaskResult.setFolderId(folderId);
        newOrderTaskResult.setOrderId(newOrder.getId());
        newOrderTaskResult.setOrderNo(newOrder.getOrderNo());
        newOrderTaskResult.setParentOrderNo(copiedOrder.getParentOrderNo());
        newOrderTaskResult.setQcCategoryId(copiedQc.getId());
        newOrderTaskResult.setQcCategoryCode(copiedQc.getQcCategoryId());
        newOrderTaskResult.setQcName(copiedQc.getQcName());
        newOrderTaskResult.setReportResult(null);
        newOrderTaskResult.setOriginalResult(null);
        newOrderTaskResult.setResultStatus(null);
        newOrderTaskResultList.add(newOrderTaskResult);
    }

    protected void copyOrderItemGroup(List<EmcOrderItemGroupBean> copiedOrderItemGroupList, EmcOrderBean copiedOrder, Long newOrderId, List<EmcOrderItemGroupBean> newOrderItemGroupList) {
        List<EmcOrderItemGroupBean> currentOrderItemGroupList = copiedOrderItemGroupList.stream().filter(e -> ObjectUtils.equals(copiedOrder.getId(), e.getOrderId())).collect(Collectors.toList());
        currentOrderItemGroupList.forEach(copiedOrderItemGroup -> {
            EmcOrderItemGroupBean newOrderItemGroup = new EmcOrderItemGroupBean();
            BeanUtils.copyProperties(copiedOrderItemGroup, newOrderItemGroup, EmcConstant.CREATED_COLUMNS);
            newOrderItemGroup.setId(ApplicationContextHelper.getNextIdentity());
            newOrderItemGroup.setOrderId(newOrderId);
            newOrderItemGroupList.add(newOrderItemGroup);
        });
    }
}
