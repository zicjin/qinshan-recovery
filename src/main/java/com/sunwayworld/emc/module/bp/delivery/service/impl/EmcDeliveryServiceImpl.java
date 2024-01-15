package com.sunwayworld.emc.module.bp.delivery.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.projectnode.ProjectNodeUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderContainerBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderContainerService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.delivery.bean.EmcDeliveryBean;
import com.sunwayworld.emc.module.bp.delivery.dao.EmcDeliveryDao;
import com.sunwayworld.emc.module.bp.delivery.service.EmcDeliveryService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

@Repository
@GikamBean
public class EmcDeliveryServiceImpl implements EmcDeliveryService {

    @Autowired
    @Lazy
    private EmcOrderService orderService;

    @Autowired
    @Lazy
    private EmcOrderTaskService orderTaskService;

    @Autowired
    private EmcOrderContainerService orderContainerService;

    @Autowired
    private EmcDeliveryDao deliveryDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcDeliveryDao getDao() {
        return deliveryDao;
    }

    @Override
    public Long insert(RestJsonWrapperBean wrapper) {
        EmcDeliveryBean delivery = wrapper.parseUnique(EmcDeliveryBean.class);
        delivery.setId(ApplicationContextHelper.getNextIdentity());
        delivery.setDeliveryNo(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        getDao().insert(delivery);
        return delivery.getId();
    }

    @Override
    @Transactional
    public void createDelivery(List<EmcSampleBean> sampleList) {
        List<Long> sampleIdList = sampleList.stream().map(EmcSampleBean::getId).collect(Collectors.toList());
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(sampleIdList, "sampleId", Arrays.asList("id", "sampleId"));
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());

        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "orderId", Arrays.asList("id", "orderId", "orderContainerId"));

        List<EmcDeliveryBean> deliveryList = new ArrayList<>(sampleIdList.size());
        List<EmcOrderContainerBean> updateContainerList = new ArrayList<>();
        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");

        sampleList.forEach(sample -> {
            EmcDeliveryBean delivery = new EmcDeliveryBean();
            delivery.setId(ApplicationContextHelper.getNextIdentity());
            delivery.setOrgId(parentOrgId);
            delivery.setDeliveryNo(ApplicationContextHelper.getNextSequence(getDao().getTable()));
            delivery.setProjId(sample.getProjId());
            delivery.setStatus("draft");
            delivery.setSource(sample.getSource());
            deliveryList.add(delivery);

            List<Long> curOrderIdList = orderList.stream().filter(order -> ObjectUtils.equals(order.getSampleId(), sample.getId())).map(EmcOrderBean::getId).collect(Collectors.toList());
            List<EmcOrderTaskBean> curTaskList = orderTaskList.stream().filter(task -> curOrderIdList.contains(task.getOrderId())).collect(Collectors.toList());
            List<Long> orderContainerIdList = curTaskList.stream().map(EmcOrderTaskBean::getOrderContainerId).collect(Collectors.toList());
            orderContainerIdList.forEach(orderContainerId -> {
                EmcOrderContainerBean updateContainer = new EmcOrderContainerBean();
                updateContainer.setId(orderContainerId);
                updateContainer.setDeliveryId(delivery.getId());
                updateContainerList.add(updateContainer);
            });
        });

        getDao().insert(deliveryList);
        orderContainerService.getDao().update(updateContainerList, "deliveryId");
    }

    @Override
    public void submit(RestJsonWrapperBean wrapper) {
        List<EmcDeliveryBean> deliveryList = wrapper.parse(EmcDeliveryBean.class);
        List<Long> deliveryIdList = deliveryList.stream().map(EmcDeliveryBean::getId).collect(Collectors.toList());

        List<EmcOrderContainerBean> containerList = orderContainerService.getDao().selectListByOneColumnValues(deliveryIdList, "deliveryId", Arrays.asList("id", "deliveryId"));
        List<Long> containerIdList = containerList.stream().map(EmcOrderContainerBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(containerIdList, "orderContainerId");

        List<EmcDeliveryBean> orderRegistrationDeliveryList = deliveryList.stream().filter(d -> EmcBizConstant.BP_SOURCE_ORDERREGISTRATION.equals(d.getSource())).collect(Collectors.toList());
        List<Long> orDeliveryIdList = orderRegistrationDeliveryList.stream().map(EmcDeliveryBean::getId).collect(Collectors.toList());
        List<Long> orContainerIdList = containerList.stream().filter(c -> orDeliveryIdList.contains(c.getDeliveryId())).map(EmcOrderContainerBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> orSamplingTaskList = orderTaskList.stream().filter(ot -> orContainerIdList.contains(ot.getOrderContainerId()) && EmcBizConstant.ANALYSISCATEGORY_SAMPLING.equals(ot.getAnalysisType())).collect(Collectors.toList());
        if (!orSamplingTaskList.isEmpty()) {
            orSamplingTaskList.forEach(task -> task.setStatus(EmcBizConstant.ORDERTASK_STATUS_DONE));
            orderTaskService.getDao().update(orSamplingTaskList, "status");
        }

        List<EmcOrderTaskBean> analysisTaskList = orderTaskList.stream().filter(ot -> EmcBizConstant.ANALYSISCATEGORY_ANALYSIS.equals(ot.getAnalysisType())).collect(Collectors.toList());
        if (!analysisTaskList.isEmpty()) {
            analysisTaskList.forEach(task -> task.setStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED));
            orderTaskService.getDao().update(analysisTaskList, "status");
        }

        deliveryList.forEach(d -> d.setStatus("done"));
        getDao().update(deliveryList, "status");

        List<Long> projectIdList = deliveryList.stream().map(EmcDeliveryBean::getProjId).distinct().collect(Collectors.toList());
        ProjectNodeUtils.updateNodes(projectIdList);
    }
}
