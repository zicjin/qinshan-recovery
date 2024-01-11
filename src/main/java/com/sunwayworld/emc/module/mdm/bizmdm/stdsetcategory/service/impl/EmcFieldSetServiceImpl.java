package com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.dao.EmcFieldSetDao;
import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.service.EmcFieldSetService;

@Repository
public class EmcFieldSetServiceImpl implements EmcFieldSetService {
    @Autowired
    private EmcFieldSetDao emcFieldSetDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcFieldSetDao getDao() {
        return emcFieldSetDao;
    }

}
