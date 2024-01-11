package com.sunwayworld.emc.module.bp.project.project.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemBean;
import com.sunwayworld.emc.module.bp.project.project.dao.EmcOrderItemDao;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
public class EmcOrderItemServiceImpl implements EmcOrderItemService {
    @Autowired
    private EmcOrderItemDao orderItemDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcOrderItemDao getDao() {
        return orderItemDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void editItem(Long orderItemGroupId, RestJsonWrapperBean wrapper) {

        List<EmcOrderItemBean> newOrderItemList = wrapper.parse(EmcOrderItemBean.class);

        List<Long> oldItemIdList = getDao().selectListByOneColumnValue(orderItemGroupId, "ORDERITEMGROUPID").stream().map(e -> e.getItemId()).collect(Collectors.toList());

        List<Long> newItemIdList = newOrderItemList.stream().map(e -> e.getItemId()).collect(Collectors.toList());

        List<EmcOrderItemBean> addOrderItemList = newOrderItemList.stream().filter(e -> !oldItemIdList.contains(e.getItemId())).collect(Collectors.toList());

        List<EmcOrderItemBean> deleteOrderItemList = oldItemIdList.stream().filter(e -> !newItemIdList.contains(e)).map(e -> {
            EmcOrderItemBean orderItem = new EmcOrderItemBean();

            orderItem.setItemId(e);
            orderItem.setOrderItemGroupId(orderItemGroupId);

            return orderItem;
        }).collect(Collectors.toList());

        if (!addOrderItemList.isEmpty()) {

            addOrderItemList.forEach(e -> {
                e.setId(ApplicationContextHelper.getNextIdentity());
                e.setOrderItemGroupId(orderItemGroupId);
            });

            getDao().insert(addOrderItemList);
        }

        if (!deleteOrderItemList.isEmpty()) {
            getDao().deleteBy(deleteOrderItemList, "ITEMID", "ORDERITEMGROUPID");
        }
    }

}
