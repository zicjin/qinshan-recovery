package com.sunwayworld.emc.module.mdm.basemdm.vendor.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.vendor.bean.EmcVendorBean;
import com.sunwayworld.emc.module.mdm.basemdm.vendor.dao.EmcVendorDao;
import com.sunwayworld.emc.module.mdm.basemdm.vendor.mapper.EmcVendorMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
public class EmcVendorDaoImpl extends MybatisDaoSupport<EmcVendorBean, Long>implements EmcVendorDao {
    
    @Autowired
    private EmcVendorMapper emcVendorMapper;
    
    @Override
    public EmcVendorMapper getMapper() {
        return emcVendorMapper;
    }
    
}
