package com.sunwayworld.emc.module.mdm.crm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.sunwayworld.emc.module.mdm.crm.dao.CrmWriteOffDao;
import com.sunwayworld.emc.module.mdm.crm.service.CrmWriteOffService;

@Repository
public class CrmWriteOffServiceImpl implements CrmWriteOffService {
    @Autowired
    private CrmWriteOffDao crmWriteOffDao;

    @Override
    @SuppressWarnings("unchecked")
    public CrmWriteOffDao getDao() {
        return crmWriteOffDao;
    }


}