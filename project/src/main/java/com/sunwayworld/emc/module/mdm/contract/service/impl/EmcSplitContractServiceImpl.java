package com.sunwayworld.emc.module.mdm.contract.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.contract.dao.EmcSplitContractDao;
import com.sunwayworld.emc.module.mdm.contract.service.EmcSplitContractService;

/**
 * @author zhengxy@sunwayworld.com 2023年11月07日
 */
@Repository
public class EmcSplitContractServiceImpl implements EmcSplitContractService {

    @Autowired
    private EmcSplitContractDao splitContractDao;

    @SuppressWarnings("unchecked")
    @Override
    public EmcSplitContractDao getDao() {
        return splitContractDao;
    }

}
