package com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.capacity.dao.EmcCapacityItemDao;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemService;

/**
 * @author zhengxy@sunwayworld.com 2023年07月19日
 */
@Repository
public class EmcCapacityItemServiceImpl implements EmcCapacityItemService {

    @Autowired
    private EmcCapacityItemDao capacityItemDao;

    @Override
    public EmcCapacityItemDao getDao() {
        return capacityItemDao;
    }
}
