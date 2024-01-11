package com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcTestCapacityBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.dao.EmcTestCapacityDao;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcTestCapacityService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorSchemeService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemGroupService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;

@Repository
@GikamBean
public class EmcTestCapacityServiceImpl implements EmcTestCapacityService {

    @Autowired
    private EmcTestCapacityDao emcTestCapacityDao;

    @Autowired
    private EmcItemGroupService itemGroupService;

    @Autowired
    private EmcMonitorSchemeService monitorSchemeService;

    @Autowired
    private EmcCapacityItemGroupServiceImpl capacityItemGroupService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcTestCapacityDao getDao() {
        return emcTestCapacityDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcTestCapacityBean emcTestCapacity = jsonWrapper.parseUnique(EmcTestCapacityBean.class);
        emcTestCapacity.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcTestCapacity);
        return emcTestCapacity.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long batchInsert(RestJsonWrapperBean jsonWrapper) {
        List<EmcTestCapacityBean> testCapacityList = jsonWrapper.parse(EmcTestCapacityBean.class);
        List<Long> testMethodIdList = testCapacityList.stream().map(a -> a.getId()).collect(Collectors.toList());
        List<EmcItemGroupBean> itemGroupList = itemGroupService.getDao().selectListByOneColumnValues(testMethodIdList,"TESTMETHODID");
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        List<EmcTestCapacityBean> insertList = new ArrayList<>();
        List<EmcCapacityItemGroupBean> insertGroupList = new ArrayList<>();
        testCapacityList.forEach(e->{
            EmcTestCapacityBean insertRow = new EmcTestCapacityBean();
            insertRow.setId(ApplicationContextHelper.getNextIdentity());
            insertRow.setTestMethodId(e.getId());
            insertRow.setOrgId(parentOrgId);
            insertList.add(insertRow);
        });
        Map<Long,Long> insertMap = insertList.stream().collect(Collectors.toMap(EmcTestCapacityBean::getTestMethodId,EmcTestCapacityBean::getId));
        itemGroupList.forEach(e->{
            EmcCapacityItemGroupBean itemGroup = new EmcCapacityItemGroupBean();
            itemGroup.setId(ApplicationContextHelper.getNextIdentity());
            itemGroup.setTestCapacityId(insertMap.get(e.getTestMethodId()));
            itemGroup.setTestId(e.getTestId());
            itemGroup.setItemGroup(e.getItemGroup());
            itemGroup.setTestMethodId(e.getTestMethodId());
            insertGroupList.add(itemGroup);
        });
        getDao().insert(insertList);
        capacityItemGroupService.getDao().insert(insertGroupList);
        return testCapacityList.get(0).getId();
    }

    @Override
    public Page<EmcMonitorSchemeBean> selectSchemeChoosablePagination(RestJsonWrapperBean wrapper) {
        return monitorSchemeService.selectChoosablePagination(wrapper);
    }
}
