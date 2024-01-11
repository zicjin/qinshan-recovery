package com.sunwayworld.emc.module.mdm.contract.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.contract.bean.EmcContractBean;
import com.sunwayworld.emc.module.mdm.contract.dao.EmcContractDao;
import com.sunwayworld.emc.module.mdm.contract.mapper.EmcContractMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcContractDaoImpl extends MybatisDaoSupport<EmcContractBean, Long> implements EmcContractDao {

    @Autowired
    private EmcContractMapper emcContractMapper;

    @Override
    public EmcContractMapper getMapper() {
        return emcContractMapper;
    }

}
