package com.sunwayworld.emc.module.mdm.emp.emp.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpTrainBean;
import com.sunwayworld.emc.module.mdm.emp.emp.dao.EmcEmpTrainDao;
import com.sunwayworld.emc.module.mdm.emp.emp.service.EmcEmpTrainService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEmpTrainServiceImpl  implements EmcEmpTrainService {
   
    @Autowired
    private EmcEmpTrainDao emcAnEmpTrainDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEmpTrainDao getDao() {
        return emcAnEmpTrainDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertTrain(String empId, RestJsonWrapperBean wrapper) {
        List<EmcEmpTrainBean> empTrainList = wrapper.parse(EmcEmpTrainBean.class);
        empTrainList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setEmpId(empId);
        });

        getDao().insert(empTrainList);
    }

}
