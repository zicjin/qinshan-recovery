package com.sunwayworld.emc.module.item.scm.convenmtl.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.scm.convenmtl.bean.EmcConvenMtlBean;
import com.sunwayworld.emc.module.item.scm.convenmtl.dao.EmcConvenMtlDao;
import com.sunwayworld.emc.module.item.scm.convenmtl.mapper.EmcConvenMtlMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


@Repository
public class EmcConvenMtlDaoImpl extends MybatisDaoSupport<EmcConvenMtlBean, Long>implements EmcConvenMtlDao {
    
    @Autowired
    private EmcConvenMtlMapper emcConvenMtlMapper;
    
    @Override
    public EmcConvenMtlMapper getMapper() {
        return emcConvenMtlMapper;
    }
    
}
