package com.sunwayworld.emc.module.bp.order.orderreceive.service.impl;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderContainerBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderContainerService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.order.orderlabel.bean.EmcOrderLabelBean;
import com.sunwayworld.emc.module.bp.order.orderreceive.service.PfOrderReceiveService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PfOrderReceiveServiceImpl implements PfOrderReceiveService {

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Autowired
    private EmcOrderContainerService orderContainerService;

    //扫码领样
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public Long match(RestJsonWrapperBean wrapper) {
        EmcOrderLabelBean emcOrderLabelBean = wrapper.parseUnique(EmcOrderLabelBean.class);

        String labelCode = emcOrderLabelBean.getLabelCode().toUpperCase();

        List<EmcOrderContainerBean> orderContainerList = orderContainerService.getDao().selectListByOneColumnValue(labelCode, "orderContainerNo");
        if (!orderContainerList.isEmpty()) {
            orderContainerList.forEach(oc -> {
                oc.setSampleReceiverDate(LocalDateTime.now());
//                oc.setSampleReceiverId(LocalContextHelper.getLoginUserId());
//                oc.setSampleReceiverName(LocalContextHelper.getLoginUserName());
                oc.setSampleReceiverTime(DateTimeUtils.formatLocalDateTime(LocalDateTime.now(), "HH:mm"));
                oc.setIsReceiveScan(Constant.YES);
            });
            orderContainerService.getDao().fastUpdate(orderContainerList, "isReceiveScan","sampleReceiverDate", "sampleReceiverTime");
        }

        return 1L;
    }

    //样品领用
    @Override
    public Long receive(RestJsonWrapperBean wrapper) {
        List<EmcOrderContainerBean> orderContainerList = wrapper.parse(EmcOrderContainerBean.class);

        List<Long> orderContainerIdList = orderContainerList.stream().map(EmcOrderContainerBean :: getId)
                .filter(id -> !ObjectUtils.isEmpty(id)).distinct().collect(Collectors.toList());

        if(!CollectionUtils.isEmpty(orderContainerIdList)){
            orderContainerList = orderContainerService.getDao().selectListByIds(orderContainerIdList);

            List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderContainerIdList,"orderContainerId");

            List<EmcOrderTaskBean> updateOrderTaskList = new ArrayList<>();
            orderContainerList.forEach(orderContainer -> orderTaskList.stream().filter(e -> ObjectUtils.equals(orderContainer.getId(),e.getOrderContainerId())).forEach(orderTask -> {
                orderTask.setSampleReceiverDate(orderContainer.getSampleReceiverDate());
                orderTask.setSampleReceiverId(orderContainer.getSampleReceiverId());
                orderTask.setSampleReceiverName(orderContainer.getSampleReceiverName());
                orderTask.setSampleReceiverTime(orderContainer.getSampleReceiverTime());
                orderTask.setSampleReceiverFlag(Constant.YES);
                updateOrderTaskList.add(orderTask);
            }));

            orderTaskService.getDao().fastUpdate(updateOrderTaskList,"sampleReceiverFlag","sampleReceiverTime","sampleReceiverName","sampleReceiverId","sampleReceiverDate");
        }
        return 1L;
    }
}
