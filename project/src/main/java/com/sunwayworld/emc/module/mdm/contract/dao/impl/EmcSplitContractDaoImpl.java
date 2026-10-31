package com.sunwayworld.emc.module.mdm.contract.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.contract.bean.EmcSplitContractBean;
import com.sunwayworld.emc.module.mdm.contract.dao.EmcSplitContractDao;
import com.sunwayworld.emc.module.mdm.contract.mapper.EmcSplitContractMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2023年11月07日
 */
@Repository
@GikamBean
public class EmcSplitContractDaoImpl extends MybatisDaoSupport<EmcSplitContractBean, Long> implements EmcSplitContractDao {

    @Autowired
    private EmcSplitContractMapper splitContractMapper;

    @Override
    public EmcSplitContractMapper getMapper() {
        return splitContractMapper;
    }
}
