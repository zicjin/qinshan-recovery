package com.sunwayworld.emc.module.mdm.basemdm.vendor.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.vendor.dao.EmcVendorMtlDao;
import com.sunwayworld.emc.module.mdm.basemdm.vendor.service.EmcVendorMtlService;

@Repository
public class EmcVendorMtlServiceImpl implements EmcVendorMtlService {
    @Autowired
    private EmcVendorMtlDao emcVendorMtlDao;
    
    @Override
    @SuppressWarnings("unchecked")
    public EmcVendorMtlDao getDao() {
        return emcVendorMtlDao;
    }
    
}
