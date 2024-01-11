package com.sunwayworld.emc.module.mdm.equipt.equipt.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptTestMethodDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptTestMethodService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.BeanUtils;

/**
 * @author magt@sunwayworld.com
 * @date 2019-09-20
 */

@Repository
public class EmcEquiptTestMethodServiceImpl implements EmcEquiptTestMethodService {
    
    @Autowired
    private EmcEquiptTestMethodDao emcEquiptTestMethodDao;
    
    @Override
    @SuppressWarnings("unchecked")
    public EmcEquiptTestMethodDao getDao() {
        return emcEquiptTestMethodDao;
    }

    @Override
    @Transactional
    public Long insert(Long equiptId, RestJsonWrapperBean jsonWrapper) {
        List<EmcEquiptTestMethodBean> emcEquiptTestMethodList = jsonWrapper.parse(EmcEquiptTestMethodBean.class);
        emcEquiptTestMethodList.stream().forEach(e ->{
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setEquiptId(equiptId);
        });
        getDao().insert(emcEquiptTestMethodList);
        return emcEquiptTestMethodList.get(0).getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void testMethodCopyTo(RestJsonWrapperBean wrapper) {
        List<EmcEquiptBean> equiptList = wrapper.parse(EmcEquiptBean.class);
        
        List<EmcEquiptTestMethodBean> equiptTestMethodList = wrapper.parse(EmcEquiptTestMethodBean.class);
        
        List<EmcEquiptTestMethodBean> insertEquiptTestMethodList = new ArrayList<>();
        
        for (EmcEquiptBean equipt : equiptList) {
            for (EmcEquiptTestMethodBean equiptTestMethod : equiptTestMethodList) {
                EmcEquiptTestMethodBean insertMethod = new EmcEquiptTestMethodBean();
                BeanUtils.copyProperties(equiptTestMethod, insertMethod);
                insertMethod.setId(ApplicationContextHelper.getNextIdentity());
                insertMethod.setEquiptId(equipt.getId());
                
                insertEquiptTestMethodList.add(insertMethod);
            }
        }
        
        getDao().insert(insertEquiptTestMethodList);
    }
}
