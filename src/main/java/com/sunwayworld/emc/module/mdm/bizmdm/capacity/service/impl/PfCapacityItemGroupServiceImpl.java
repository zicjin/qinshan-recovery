package com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.dao.EmcCapacityItemGroupDao;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.PfCapacityItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.validator.PfDeleteCapacityItemGroupValidator;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;

/**
 * @author zhengxy@sunwayworld.com 2023年07月19日
 */
@GikamBean
@Repository
public class PfCapacityItemGroupServiceImpl extends EmcCapacityItemGroupServiceImpl implements PfCapacityItemGroupService {

    @Autowired
    private EmcCapacityItemGroupDao capacityItemGroupDao;

    @Autowired
    private EmcGroupItemService groupItemService;

    @Autowired
    private EmcItemService itemService;

    @Autowired
    private EmcCapacityItemService capacityItemService;

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long batchInsert(Long id, RestJsonWrapperBean jsonWrapper) {
        List<EmcCapacityItemGroupBean> itemGroupList = jsonWrapper.parse(EmcCapacityItemGroupBean.class);
        itemGroupList.forEach(e->{
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setTestCapacityId(id);
        });

        List<Long> itemGroupIdList = itemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId).collect(Collectors.toList());
        List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "itemGroupId");
        List<Long> itemIdList = groupItemList.stream().map(EmcGroupItemBean::getItemId).distinct().collect(Collectors.toList());

        List<EmcItemBean> itemList = itemService.getDao().selectListByIds(itemIdList);

        List<EmcCapacityItemBean> capacityItemList = new ArrayList<>();
        itemGroupList.forEach(cig -> {
            List<Long> curItemIdList = groupItemList.stream().filter(gi -> ObjectUtils.equals(cig.getItemGroupId(), gi.getItemGroupId())).map(EmcGroupItemBean::getItemId).collect(Collectors.toList());
            List<EmcItemBean> curItemList = itemList.stream().filter(i -> curItemIdList.contains(i.getId())).collect(Collectors.toList());
            curItemList.forEach(item -> {
                EmcCapacityItemBean capacityItem = new EmcCapacityItemBean();
                BeanUtils.copyProperties(item, capacityItem, EmcConstant.CREATED_COLUMNS);
                capacityItem.setCapItemGroupId(cig.getId());
                capacityItem.setTestCapacityId(cig.getTestCapacityId());
                capacityItem.setId(ApplicationContextHelper.getNextIdentity());
                capacityItem.setItemId(item.getId());
                capacityItemList.add(capacityItem);
            });
        });

        capacityItemGroupDao.insert(itemGroupList);
        capacityItemService.getDao().insert(capacityItemList);

        return itemGroupList.get(0).getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    @ValidateDataWith(PfDeleteCapacityItemGroupValidator.class)
    public void delete(RestJsonWrapperBean wrapper) {
        List<EmcCapacityItemGroupBean> capacityItemGroupList = wrapper.parse(EmcCapacityItemGroupBean.class);
        List<Long> capacityItemGroupIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getId).collect(Collectors.toList());

        getDao().deleteByIdList(capacityItemGroupIdList);

        List<EmcCapacityItemBean> deleteItemList = new ArrayList<>(capacityItemGroupIdList.size());
        capacityItemGroupIdList.forEach(cigId -> {
            EmcCapacityItemBean deleteItem = new EmcCapacityItemBean();
            deleteItem.setCapItemGroupId(cigId);
            deleteItemList.add(deleteItem);
        });

        capacityItemService.getDao().deleteBy(deleteItemList, "capItemGroupId");
    }
}
