package com.sunwayworld.emc.module.mdm.basemdm.cert.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertTestMethodBean;
import com.sunwayworld.emc.module.mdm.basemdm.cert.dao.EmcCertTestMethodDao;
import com.sunwayworld.emc.module.mdm.basemdm.cert.mapper.EmcCertTestMethodMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-08
 */
@Repository
public class EmcCertTestMethodDaoImpl extends MybatisDaoSupport<EmcCertTestMethodBean, Long>implements EmcCertTestMethodDao {
    
    @Autowired
    private EmcCertTestMethodMapper emcCertTestMethodMapper;
    
    @Override
    public EmcCertTestMethodMapper getMapper() {
        return emcCertTestMethodMapper;
    }
    
}
