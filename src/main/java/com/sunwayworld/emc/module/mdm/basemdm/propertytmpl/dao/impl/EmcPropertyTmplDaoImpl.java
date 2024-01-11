package com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyTmplBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.dao.EmcPropertyTmplDao;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.mapper.EmcPropertyTmplMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
public class EmcPropertyTmplDaoImpl extends MybatisDaoSupport<EmcPropertyTmplBean, Long> implements EmcPropertyTmplDao {

    @Autowired
    private EmcPropertyTmplMapper emcPropertyTmplMapper;

    @Override
    public EmcPropertyTmplMapper getMapper() {
        return emcPropertyTmplMapper;
    }

}
