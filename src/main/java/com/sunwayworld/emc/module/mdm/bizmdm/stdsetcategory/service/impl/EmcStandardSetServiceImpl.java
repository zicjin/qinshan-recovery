package com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.dao.EmcStandardSetDao;
import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.service.EmcStandardSetService;

@Repository
public class EmcStandardSetServiceImpl implements EmcStandardSetService {
    @Autowired
    private EmcStandardSetDao emcStandardSetDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcStandardSetDao getDao() {
        return emcStandardSetDao;
    }

}
