package com.sunwayworld.emc.module.mdm.bizmdm.test.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorSchemeService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemGroupBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;

/**
 * @author zhengxy@sunwayworld.com 2021年09月29日
 */
@GikamBean
@Repository
public class PfItemGroupServiceImpl extends EmcItemGroupServiceImpl {

    @Autowired
    @Lazy
    private EmcCapacityItemGroupService capacityItemGroupService;

    @Autowired
    private EmcMonitorSchemeService monitorSchemeService;

    @Override
    public void postUpdate(List<String> updatedColumnNameList, List<Long> idList) {
        // 修改测试管理分析项组名称后，同步修改监测能力库和监测方案中的分析项组名称
        if (CollectionUtils.containsIgnoreCase(updatedColumnNameList, "itemGroup")) {
            List<EmcItemGroupBean> itemGroupList = getDao().selectListByIds(idList);
            Map<Long, String> idItemGroupMap = itemGroupList.stream().collect(Collectors.toMap(EmcItemGroupBean::getId, EmcItemGroupBean::getItemGroup, (v1, v2) -> v1));

            List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByOneColumnValues(idList, "itemGroupId");
            capacityItemGroupList.forEach(capacityItemGroup -> capacityItemGroup.setItemGroup(idItemGroupMap.get(capacityItemGroup.getItemGroupId())));

            Map<Long, String> capacityIdItemGroupMap = capacityItemGroupList.stream().collect(Collectors.toMap(EmcCapacityItemGroupBean::getId, EmcCapacityItemGroupBean::getItemGroup, (v1, v2) -> v1));
            List<Long> capacityItemGroupIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getId).collect(Collectors.toList());
            List<EmcMonitorSchemeBean> monitorSchemeList = monitorSchemeService.getDao().selectListByOneColumnValues(capacityItemGroupIdList, "itemGroupId");
            monitorSchemeList.forEach(scheme -> scheme.setItemGroup(capacityIdItemGroupMap.get(scheme.getItemGroupId())));

            capacityItemGroupService.getDao().update(capacityItemGroupList, "itemGroup");
            monitorSchemeService.getDao().update(monitorSchemeList, "itemGroup");
        }
    }
}
