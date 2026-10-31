package com.sunwayworld.emc.module.mdm.basemdm.mtl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.dao.EmcStockLogDao;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockLogService;

/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月20日 下午3:46:03
 */
@Repository
public class EmcStockLogServiceImpl implements EmcStockLogService {

    @Autowired
    private EmcStockLogDao emcStockLogDao;
    
    @SuppressWarnings("unchecked")
    @Override
    public EmcStockLogDao getDao() {
        return emcStockLogDao;
    }
}
