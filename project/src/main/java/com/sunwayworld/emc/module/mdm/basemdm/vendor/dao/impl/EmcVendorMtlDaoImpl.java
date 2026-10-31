package com.sunwayworld.emc.module.mdm.basemdm.vendor.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.vendor.bean.EmcVendorMtlBean;
import com.sunwayworld.emc.module.mdm.basemdm.vendor.dao.EmcVendorMtlDao;
import com.sunwayworld.emc.module.mdm.basemdm.vendor.mapper.EmcVendorMtlMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
public class EmcVendorMtlDaoImpl extends MybatisDaoSupport<EmcVendorMtlBean, Long>implements EmcVendorMtlDao {
    
    @Autowired
    private EmcVendorMtlMapper emcVendorMtlMapper;
    
    @Override
    public EmcVendorMtlMapper getMapper() {
        return emcVendorMtlMapper;
    }
    
}
