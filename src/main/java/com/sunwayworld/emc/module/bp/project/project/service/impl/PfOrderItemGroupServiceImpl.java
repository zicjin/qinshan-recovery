package com.sunwayworld.emc.module.bp.project.project.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemService;
import com.sunwayworld.emc.module.bp.project.project.service.PfOrderItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.UnsupportedException;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ObjectUtils;

@Repository
@GikamBean
public class PfOrderItemGroupServiceImpl extends EmcOrderItemGroupServiceImpl implements PfOrderItemGroupService {

    private static final Logger logger = LogManager.getLogger(PfOrderItemGroupServiceImpl.class);

    @Autowired
    private EmcGroupItemService groupItemService;

    @Autowired
    private EmcOrderItemService orderItemService;

    @Autowired
    @Lazy
    private EmcCapacityItemGroupService capacityItemGroupService;

    @Override
    public void qcMtlSelect(RestJsonWrapperBean wrapper) {
        List<EmcOrderItemGroupBean> orderItemGroupList = wrapper.parse(EmcOrderItemGroupBean.class);
        getDao().update(orderItemGroupList,"BATCHNO");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insert(List<EmcOrderBean> orderList, List<EmcOrderItemGroupBean> orderItemGroupList, boolean itemFlag) {
        List<Long> capacityItemGroupIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).collect(Collectors.toList());
        List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByIds(capacityItemGroupIdList);
        List<Long> itemGroupIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId).collect(Collectors.toList());
        Map<Long, Long> itemGroupIdMap = capacityItemGroupList.stream().collect(Collectors.toMap(EmcCapacityItemGroupBean::getId, EmcCapacityItemGroupBean::getItemGroupId));

        List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID");

        List<EmcOrderItemBean> insertOrderItemList = new ArrayList<>();
        List<EmcOrderItemGroupBean> insertOrderItemGroupList = new ArrayList<>();

        orderList.forEach(order -> {
            orderItemGroupList.forEach(group -> {
                // 样品分析项组
                EmcOrderItemGroupBean orderItemGroup = new EmcOrderItemGroupBean();
                BeanUtils.copyProperties(group, orderItemGroup);
                orderItemGroup.setOrderId(order.getId());

                Long orderItemGroupId = ApplicationContextHelper.getNextIdentity();
                orderItemGroup.setId(orderItemGroupId);
                insertOrderItemGroupList.add(orderItemGroup);

                if (itemFlag) {
                    // 样品分析项
                    List<Long> itemIdList = groupItemList.stream().filter(e -> ObjectUtils.equals(e.getItemGroupId(), itemGroupIdMap.get(group.getItemGroupId()))).map(EmcGroupItemBean::getItemId).collect(Collectors.toList());

                    itemIdList.forEach(itemId -> {
                        EmcOrderItemBean orderItem = new EmcOrderItemBean();

                        orderItem.setId(ApplicationContextHelper.getNextIdentity());
                        orderItem.setItemId(itemId);
                        orderItem.setOrderId(order.getId());
                        orderItem.setOrderItemGroupId(orderItemGroupId);

                        insertOrderItemList.add(orderItem);

                    });
                }
            });
        });
        if (itemFlag) {
            if (insertOrderItemList.size() > 100000) {
                logger.warn("新增样品{}出现问题，数据异常 -> {}", orderList.get(0).getProjId(), insertOrderItemList.size());
                throw new UnsupportedException("数据异常");
            }

            orderItemService.getDao().insert(insertOrderItemList);
        }

        getDao().insert(insertOrderItemGroupList);
    }
}
