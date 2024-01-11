package com.sunwayworld.emc.module.item.consume.consume.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.consume.consume.dao.EmcCsmStockLogDao;
import com.sunwayworld.emc.module.item.consume.consume.service.EmcCsmStockLogService;


@Repository
@GikamBean
public class EmcCsmStockLogServiceImpl implements EmcCsmStockLogService {

    @Autowired
    private EmcCsmStockLogDao emcCsmStockLogDao;
    
    @SuppressWarnings("unchecked")
    @Override
    public EmcCsmStockLogDao getDao() {
        return emcCsmStockLogDao;
    }
}
