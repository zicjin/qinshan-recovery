package com.sunwayworld.emc.module.mdm.basemdm.monitorability.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.monitorability.bean.EmcMonitorAbilityEmpBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.bean.EmcMonitorAbilityTestBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.dao.EmcMonitorAbilityEmpDao;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.service.EmcMonitorAbilityEmpService;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.service.EmcMonitorAbilityTestService;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpTestBean;
import com.sunwayworld.emc.module.mdm.emp.emp.service.EmcEmpTestService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author magt@sunwayworld.com
 * @date 2019-10-09
 */

@Repository
public class EmcMonitorAbilityEmpServiceImpl implements EmcMonitorAbilityEmpService {
    
    @Autowired
    private EmcEmpTestService empTestService;
    
    
    @Autowired
    private EmcMonitorAbilityTestService monitorAbilityTestService;
    
    @Autowired
    private EmcMonitorAbilityEmpDao emcMonitorAbilityEmpDao;
    
    @Override
    @SuppressWarnings("unchecked")
    public EmcMonitorAbilityEmpDao getDao() {
        return emcMonitorAbilityEmpDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT) 
    public Long insert(Long moniAbilId, RestJsonWrapperBean jsonWrapper) {
        List<EmcMonitorAbilityEmpBean> monitorAbilityEmpList = jsonWrapper.parse(EmcMonitorAbilityEmpBean.class);
        monitorAbilityEmpList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
        });
        getDao().insert(monitorAbilityEmpList);
        return moniAbilId;
    }
    
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void syncEmp(RestJsonWrapperBean wrapper) {
        List<EmcMonitorAbilityEmpBean> monitorAbilityEmpList = wrapper.parse(EmcMonitorAbilityEmpBean.class);

        List<EmcMonitorAbilityTestBean> monitorAbilityTestList = monitorAbilityTestService.getDao()
                .selectListByOneColumnValue(monitorAbilityEmpList.get(0).getMonitorAbilityId(), "MONITORABILITYID");

        List<EmcEmpTestBean> preInsertEmpTestList = new ArrayList<>();
        
        monitorAbilityEmpList.forEach(e -> {

            monitorAbilityTestList.forEach(t -> {
                EmcEmpTestBean empTestBean = new EmcEmpTestBean();

                empTestBean.setTestId(t.getTestMethodId());
                empTestBean.setEmpId(e.getEmpId());
                
                preInsertEmpTestList.add(empTestBean);
                
            });

        });

        List<String> searchColNames = new ArrayList<>();
        searchColNames.add("EMPID");
        searchColNames.add("TESTID");
        
        List<String> selectColNames = new ArrayList<>();
        selectColNames.add("EMPID");
        selectColNames.add("TESTID");
        
        List<EmcEmpTestBean> selectEmpTestList = empTestService.getDao()
                .selectList(preInsertEmpTestList, searchColNames, selectColNames);
        
        List<EmcEmpTestBean> empTestList = new ArrayList<>();
        
        preInsertEmpTestList.forEach(e -> {
            selectEmpTestList.forEach(t -> {
                if (e.getEmpId().equals(t.getEmpId())  && e.getTestId().equals(t.getTestId())) {
                    empTestList.add(e);
                }
            });
        });
        
        preInsertEmpTestList.removeAll(empTestList);
          
        preInsertEmpTestList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity()); });
         
        empTestService.getDao().insert(preInsertEmpTestList);
        
    }
}
