package com.sunwayworld.emc.module.item.consume.consume.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.consume.consume.bean.EmcConsumeBean;
import com.sunwayworld.emc.module.item.consume.consume.dao.EmcConsumeDao;
import com.sunwayworld.emc.module.item.consume.consume.service.EmcConsumeService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
@GikamBean
public class EmcConsumeServiceImpl implements EmcConsumeService {

    @Autowired
    private EmcConsumeDao emcConsumeDao;
    
    @SuppressWarnings("unchecked")
    @Override
    public EmcConsumeDao getDao() {
        return emcConsumeDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT) 
    public String insert(RestJsonWrapperBean wrapper) {
        
        EmcConsumeBean emcConsume = wrapper.parseUnique(EmcConsumeBean.class);
        emcConsume.setId(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        getDao().insert(emcConsume);
        
        return emcConsume.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE) 
    public void updateStockQty(String id) {
        getDao().updateStockQty(id);
    }
}
