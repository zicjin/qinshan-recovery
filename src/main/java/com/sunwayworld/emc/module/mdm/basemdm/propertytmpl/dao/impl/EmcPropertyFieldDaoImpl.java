package com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyFieldBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.dao.EmcPropertyFieldDao;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.mapper.EmcPropertyFieldMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
public class EmcPropertyFieldDaoImpl extends MybatisDaoSupport<EmcPropertyFieldBean, Long> implements EmcPropertyFieldDao {

    @Autowired
    private EmcPropertyFieldMapper emcPropertyFieldMapper;

    @Override
    public EmcPropertyFieldMapper getMapper() {
        return emcPropertyFieldMapper;
    }

}
