package com.sunwayworld.emc.module.mdm.bizmdm.test.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemQcIndexBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcItemQcIndexDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.mapper.EmcItemQcIndexMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */
@Repository
public class EmcItemQcIndexDaoImpl extends MybatisDaoSupport<EmcItemQcIndexBean, Long>implements EmcItemQcIndexDao {
    
    @Autowired
    private EmcItemQcIndexMapper emcItemQcIndexMapper;
    
    @Override
    public EmcItemQcIndexMapper getMapper() {
        return emcItemQcIndexMapper;
    }
    
}
