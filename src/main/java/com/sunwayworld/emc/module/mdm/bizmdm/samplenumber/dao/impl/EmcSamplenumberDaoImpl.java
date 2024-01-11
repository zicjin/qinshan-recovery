package com.sunwayworld.emc.module.mdm.bizmdm.samplenumber.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.samplenumber.bean.EmcSamplenumberBean;
import com.sunwayworld.emc.module.mdm.bizmdm.samplenumber.dao.EmcSamplenumberDao;
import com.sunwayworld.emc.module.mdm.bizmdm.samplenumber.mapper.EmcSamplenumberMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcSamplenumberDaoImpl extends MybatisDaoSupport<EmcSamplenumberBean, Long> implements EmcSamplenumberDao {

    @Autowired
    private EmcSamplenumberMapper emcSamplenumberMapper;

    @Override
    public EmcSamplenumberMapper getMapper() {
        return emcSamplenumberMapper;
    }

}
