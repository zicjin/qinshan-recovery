package com.sunwayworld.emc.module.mdm.basemdm.unit.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.unit.dao.EmcUnitConvertDao;
import com.sunwayworld.emc.module.mdm.basemdm.unit.service.EmcUnitConvertService;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author yuanhh@sunwayworld.com 2019年9月9日
 */

@Repository
@GikamBean
public class EmcUnitConvertServiceImpl implements EmcUnitConvertService {

    @Autowired
    private EmcUnitConvertDao emcUnitConvertDao;

    @SuppressWarnings("unchecked")
    @Override
    public EmcUnitConvertDao getDao() {
        return emcUnitConvertDao;
    }

}