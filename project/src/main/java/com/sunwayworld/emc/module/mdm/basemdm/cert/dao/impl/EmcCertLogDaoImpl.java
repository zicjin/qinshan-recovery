package com.sunwayworld.emc.module.mdm.basemdm.cert.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertLogBean;
import com.sunwayworld.emc.module.mdm.basemdm.cert.dao.EmcCertLogDao;
import com.sunwayworld.emc.module.mdm.basemdm.cert.mapper.EmcCertLogMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-08
 */
@Repository
public class EmcCertLogDaoImpl extends MybatisDaoSupport<EmcCertLogBean, Long>implements EmcCertLogDao {
    
    @Autowired
    private EmcCertLogMapper emcCertLogMapper;
    
    @Override
    public EmcCertLogMapper getMapper() {
        return emcCertLogMapper;
    }
    
}
