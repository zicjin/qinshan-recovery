package com.sunwayworld.emc.module.mdm.basemdm.mtl.service.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.PfMtlService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class PfMtlServiceImpl extends EmcMtlServiceImpl implements PfMtlService {
	
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE) 
    public void updateStockQty(String id) {
        getDao().updateStockQty(id);
    }
	
}

