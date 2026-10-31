package com.sunwayworld.emc.module.mdm.bizmdm.client.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcClientContactBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.dao.EmcClientContactDao;
import com.sunwayworld.emc.module.mdm.bizmdm.client.mapper.EmcClientContactMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author magh@sunwayworld.com
 * @date 2019-10-19
 */
@Repository
public class EmcClientContactDaoImpl extends MybatisDaoSupport<EmcClientContactBean, Long>implements EmcClientContactDao {
    
    @Autowired
    private EmcClientContactMapper emcClientContactMapper;
    
    @Override
    public EmcClientContactMapper getMapper() {
        return emcClientContactMapper;
    }
    
}
