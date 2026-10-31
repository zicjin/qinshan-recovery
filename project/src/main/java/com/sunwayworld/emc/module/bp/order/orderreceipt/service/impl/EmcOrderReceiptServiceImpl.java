// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）
package com.sunwayworld.emc.module.bp.order.orderreceipt.service.impl;

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
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.lang.Long;
import java.lang.String;

@Repository
@GikamBean
public class EmcOrderReceiptServiceImpl
implements EmcOrderReceiptService {
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
        return this.qcCategoryService.selectPaginationByFilter(SearchFilter.instance().match("USECATEGORY", category).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    public void copyQcOrder(RestJsonWrapperBean wrapper) {
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
            taskBean.setOrderId((Long)t);
            orderTaskBeanList.add(taskBean);
            EmcOrderTaskResultBean resultBean = new EmcOrderTaskResultBean();
            resultBean.setOrderId((Long)t);
            orderTaskResultBeanList.add(resultBean);
            EmcOrderItemGroupBean orderItemGroup = new EmcOrderItemGroupBean();
            orderItemGroup.setOrderId((Long)t);
            orderItemGroupList.add(orderItemGroup);
        });
        orderList = this.orderService.selectListByIds(orderIdList);
        List<Long> qcIdList = orderList.stream().map(EmcOrderBean::getQcCategoryId).collect(Collectors.toList());
        List<EmcQcCategoryBean> qcList = this.qcCategoryService.selectListByIds(qcIdList);
        List<EmcOrderBean> neworderList = new ArrayList<EmcOrderBean>();
        for (EmcQcCategoryBean qcBean : qcList) {
            for (EmcOrderBean orderBean : orderList) {
                if (orderBean.getQcCategoryId().equals(qcBean.getId()) && "1".equals(qcBean.getAddFolder())) {
                    neworderList.add(orderBean);
                }
            }
        }
        List<Long> folderIdList = neworderList.stream().map(EmcOrderBean::getFolderId).collect(Collectors.toList());
        this.orderService.getDao().deleteByIdList(orderIdList);
        this.folderService.getDao().deleteByIdList(folderIdList);
        this.orderTaskService.getDao().deleteBy(orderTaskBeanList, new String[]{"ORDERID"});
        this.orderTaskResultService.getDao().deleteBy(orderTaskResultBeanList, new String[]{"ORDERID"});
        this.orderItemGroupService.getDao().deleteBy(orderItemGroupList, new String[]{"ORDERID"});
        List<Long> folderIds = orderList.stream().map(EmcOrderBean::getFolderId).collect(Collectors.toList());
        List<Long> deleteFolderIds = new ArrayList<Long>();
        for (Long folderId : folderIds) {
            List<EmcOrderBean> list = this.orderService.getDao().selectListByOneColumnValue((Long)folderId, "FOLDERID", new Order[0]);
            if (list == null || list.size() == 0) {
                deleteFolderIds.add(folderId);
            }
        }
        this.folderService.getDao().deleteByIdList(deleteFolderIds);
    }

    @Override
    @Transactional
    public Integer matchBarcode(RestJsonWrapperBean wrapper) {
        List<EmcSampleBean> emcSampleBeanList = wrapper.parse(EmcSampleBean.class);
        List<Long> sampleIdList = emcSampleBeanList.stream().map(EmcSampleBean::getId).collect(Collectors.toList());
        List<EmcOrderBean> emcOrderBeanList = this.orderService.selectListByFilter(SearchFilter.instance().match("SAMPLEID", sampleIdList).filter(MatchPattern.OR), new Order[0]);
        List<String> labelNoList = emcOrderBeanList.stream().map(EmcOrderBean::getLabelNo).filter(s -> !StringUtils.isEmpty(s)).collect(Collectors.toList());
        if (!labelNoList.isEmpty()) {
            List<EmcOrderLabelBean> labelBeanList = this.orderLabelService.selectListByFilter(SearchFilter.instance().match("LABELCODE", labelNoList).filter(MatchPattern.OR), new Order[0]);
            if (!labelBeanList.isEmpty()) {
                labelBeanList.forEach(bean -> bean.setMatch("1"));
                this.orderLabelService.updateIfChanged(labelBeanList);
                return labelBeanList.size();
            }
        }
        return 0;
    }

    @Override
    @Transactional
    public void retainOrder(RestJsonWrapperBean wrapper) {
        this.orderRetainService.retainOrder(wrapper);
    }

    @Override
    @Transactional
    @ValidateDataWith(value=EmcDeleteOrderValidator.class)
    public void deleteOrder(RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskBeanList = new ArrayList<>();
        List<EmcOrderTaskResultBean> orderTaskResultBeanList = new ArrayList<>();
        orderIdList.forEach(t -> {
            EmcOrderTaskBean taskBean = new EmcOrderTaskBean();
            taskBean.setOrderId((Long)t);
            orderTaskBeanList.add(taskBean);
            EmcOrderTaskResultBean resultBean = new EmcOrderTaskResultBean();
            resultBean.setOrderId((Long)t);
            orderTaskResultBeanList.add(resultBean);
        });
        orderList = this.orderService.selectListByIds(orderIdList);
        this.orderService.getDao().deleteByIdList(orderIdList);
        this.orderTaskService.getDao().deleteBy(orderTaskBeanList, new String[]{"ORDERID"});
        this.orderTaskResultService.getDao().deleteBy(orderTaskResultBeanList, new String[]{"ORDERID"});
        List<Long> folderIds = orderList.stream().map(EmcOrderBean::getFolderId).collect(Collectors.toList());
        List<Long> deleteFolderIds = new ArrayList<Long>();
        for (Long folderId : folderIds) {
            List<EmcOrderBean> list = this.orderService.getDao().selectListByOneColumnValue((Long)folderId, "FOLDERID", new Order[0]);
            if (list == null || list.size() == 0) {
                deleteFolderIds.add(folderId);
            }
        }
        this.folderService.getDao().deleteByIdList(deleteFolderIds);
    }

    protected void copyFolder(List<EmcFolderBean> copiedFolderList, EmcOrderBean copiedOrder, EmcQcCategoryBean copiedQc, Long newFolderId, String newFolderNo, List<EmcFolderBean> newFolderList) {
        Long projId = copiedFolderList.get(0).getProjId();
        List<EmcFolderBean> folderBeanList = this.folderService.getDao().selectListByOneColumnValue((Long)projId, "projId", new Order[0]);
        folderBeanList.addAll(newFolderList);
        String qcName = copiedQc.getQcName();
        String qcCategoryId = copiedQc.getQcCategoryId();
        copiedFolderList.stream().filter(folder -> ObjectUtils.equals(folder.getId(), copiedOrder.getFolderId())).forEach(folder -> {
            EmcFolderBean newFolder = new EmcFolderBean();
            BeanUtils.copyProperties(folder, (Object)newFolder, EmcConstant.CREATED_COLUMNS);
            newFolder.setId(newFolderId);
            newFolder.setFolderNo(newFolderNo);
            newFolder.setSource("COPY");
            if ("P-BLANK".equals(qcCategoryId) || "PQC-STD".equals(qcCategoryId) || "P-YSKB".equals(qcCategoryId)) {
                newFolder.setMonitorPointNo(qcName);
            }
            if ("P-DUP".equals(qcCategoryId)) {
                newFolder.setMonitorPointName((StringUtils.isEmpty(folder.getMonitorPointName()) ? "" : folder.getMonitorPointName()) + qcName);
            } else {
                newFolder.setMonitorPointName(qcName);
            }
            if (ArrayUtils.asList("P-BLANK", "Q-DUP", "zcsbkb", "P-YSKB").contains(qcCategoryId) && ArrayUtils.asList("土壤", "地下水").contains(copiedOrder.getOrderCategory())) {
                int index = folderBeanList.stream().filter(e -> e.getMonitorPointName() != null && e.getMonitorPointName().contains(qcName)).map(e -> e.getMonitorPointName().replace(qcName, "")).filter(e -> NumberUtils.isNumber(e)).mapToInt(e -> NumberUtils.parseInt(e)).max().orElse(0);
                newFolder.setMonitorPointNo(qcName + (index + 1));
                newFolder.setMonitorPointName(qcName + (index + 1));
            }
            newFolderList.add(newFolder);
            folderBeanList.add(newFolder);
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
        if (StringUtils.equals("1", copiedQc.getUpdateParNo())) {
            newOrder.setParentOrderNo(copiedOrder.getOrderNo());
        }
        newOrderList.add(newOrder);
    }

    protected void copyOrderTask(EmcOrderTaskBean copiedOrderTask, EmcQcCategoryBean copiedQc, Long newOrderTaskId, Long folderId, EmcOrderBean newOrder, List<EmcOrderTaskBean> newOrderTaskList) {
        EmcOrderTaskBean newOrderTask = new EmcOrderTaskBean();
        BeanUtils.copyProperties((Object)copiedOrderTask, (Object)newOrderTask, EmcConstant.CREATED_COLUMNS);
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
        BeanUtils.copyProperties((Object)copiedOrderTaskResult, (Object)newOrderTaskResult, EmcConstant.CREATED_COLUMNS);
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
            BeanUtils.copyProperties(copiedOrderItemGroup, (Object)newOrderItemGroup, EmcConstant.CREATED_COLUMNS);
            newOrderItemGroup.setId(ApplicationContextHelper.getNextIdentity());
            newOrderItemGroup.setOrderId(newOrderId);
            newOrderItemGroupList.add(newOrderItemGroup);
        });
    }
}
