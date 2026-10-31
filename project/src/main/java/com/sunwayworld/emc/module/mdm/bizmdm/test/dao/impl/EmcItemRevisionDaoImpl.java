package com.sunwayworld.emc.module.mdm.bizmdm.test.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemRevisionBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcItemRevisionDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.mapper.EmcItemRevisionMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */
@Repository
public class EmcItemRevisionDaoImpl extends MybatisDaoSupport<EmcItemRevisionBean, Long>implements EmcItemRevisionDao {
    
    @Autowired
    private EmcItemRevisionMapper emcItemRevisionMapper;
    
    @Override
    public EmcItemRevisionMapper getMapper() {
        return emcItemRevisionMapper;
    }
    
}
