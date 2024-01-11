package com.sunwayworld.emc.module.item.scm.officemtl.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.scm.officemtl.bean.EmcOfficeMtlBean;
import com.sunwayworld.emc.module.item.scm.officemtl.dao.EmcOfficeMtlDao;
import com.sunwayworld.emc.module.item.scm.officemtl.mapper.EmcOfficeMtlMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
public class EmcOfficeMtlDaoImpl extends MybatisDaoSupport<EmcOfficeMtlBean, Long>implements EmcOfficeMtlDao {
    
    @Autowired
    private EmcOfficeMtlMapper emcOfficeMtlMapper;
    
    @Override
    public EmcOfficeMtlMapper getMapper() {
        return emcOfficeMtlMapper;
    }
    
}
