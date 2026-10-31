package com.sunwayworld.emc.module.mdm.basemdm.monitorability.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.monitorability.bean.EmcMonitorAbilityEquiptBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.bean.EmcMonitorAbilityTestBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.dao.EmcMonitorAbilityEquiptDao;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.service.EmcMonitorAbilityEquiptService;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.service.EmcMonitorAbilityTestService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptTestMethodService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author magt@sunwayworld.com
 * @date 2019-10-09
 */

@Repository
public class EmcMonitorAbilityEquiptServiceImpl implements EmcMonitorAbilityEquiptService {
    @Autowired
    private EmcMonitorAbilityEquiptDao emcMonitorAbilityEquiptDao;

    @Autowired
    private EmcMonitorAbilityTestService monitorAbilityTestService;
    
    @Autowired
    private EmcEquiptTestMethodService equiptTestMethodService;
    
    
    

    @Override
    @SuppressWarnings("unchecked")
    public EmcMonitorAbilityEquiptDao getDao() {
        return emcMonitorAbilityEquiptDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long moniAbilId, RestJsonWrapperBean jsonWrapper) {
        List<EmcMonitorAbilityEquiptBean> monitorAbilityEquiptList = jsonWrapper
                .parse(EmcMonitorAbilityEquiptBean.class);
        monitorAbilityEquiptList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
        });
        getDao().insert(monitorAbilityEquiptList);
        return moniAbilId;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void syncEquipt(RestJsonWrapperBean wrapper) {
        List<EmcMonitorAbilityEquiptBean> monitorAbilityEquiptList = wrapper.parse(EmcMonitorAbilityEquiptBean.class);

        List<EmcMonitorAbilityTestBean> monitorAbilityTestList = monitorAbilityTestService.getDao()
                .selectListByOneColumnValue(monitorAbilityEquiptList.get(0).getMonitorAbilityId(), "MONITORABILITYID");

        List<EmcEquiptTestMethodBean> preInsertTestMethodEquiptList = new ArrayList<>();
        
        monitorAbilityEquiptList.forEach(e -> {

            monitorAbilityTestList.forEach(t -> {
                EmcEquiptTestMethodBean testMethodEquipt = new EmcEquiptTestMethodBean();

                testMethodEquipt.setEquiptId(e.getEquiptId());
                testMethodEquipt.setTestMethodId(t.getTestMethodId());
                testMethodEquipt.setDefaultFlag(e.getDefaultFlag());
                
                preInsertTestMethodEquiptList.add(testMethodEquipt);
                
            });

        });

        List<String> searchColNames = new ArrayList<>();
        searchColNames.add("EQUIPTID");
        searchColNames.add("TESTMETHODID");
        
        List<String> selectColNames = new ArrayList<>();
        selectColNames.add("EQUIPTID");
        selectColNames.add("TESTMETHODID");
        
        List<EmcEquiptTestMethodBean> selectTestMethodEquiptList = equiptTestMethodService.getDao()
                .selectList(preInsertTestMethodEquiptList, searchColNames, selectColNames);
        
        List<EmcEquiptTestMethodBean> tempTestMethodEquiptList = new ArrayList<>();
        
        preInsertTestMethodEquiptList.forEach(e -> {
            selectTestMethodEquiptList.forEach(t -> {
                if (e.getTestMethodId().equals(t.getTestMethodId())  && e.getEquiptId().equals(t.getEquiptId())) {
                    tempTestMethodEquiptList.add(e);
                }
            });
        });
        
        preInsertTestMethodEquiptList.removeAll(tempTestMethodEquiptList);
          
        preInsertTestMethodEquiptList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity()); });
         
        equiptTestMethodService.getDao().insert(preInsertTestMethodEquiptList);
        
    }

}
