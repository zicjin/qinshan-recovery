package com.sunwayworld.emc.module.item.scm.standardmtl.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.scm.standardmtl.bean.EmcStandardMtlBean;
import com.sunwayworld.emc.module.item.scm.standardmtl.dao.EmcStandardMtlDao;
import com.sunwayworld.emc.module.item.scm.standardmtl.mapper.EmcStandardMtlMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
public class EmcStandardMtlDaoImpl extends MybatisDaoSupport<EmcStandardMtlBean, Long>implements EmcStandardMtlDao {
    
    @Autowired
    private EmcStandardMtlMapper emcStandardMtlMapper;
    
    @Override
    public EmcStandardMtlMapper getMapper() {
        return emcStandardMtlMapper;
    }
    
}
