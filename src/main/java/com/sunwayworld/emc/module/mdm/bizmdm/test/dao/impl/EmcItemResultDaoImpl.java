package com.sunwayworld.emc.module.mdm.bizmdm.test.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemResultBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcItemResultDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.mapper.EmcItemResultMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */
@Repository
public class EmcItemResultDaoImpl extends MybatisDaoSupport<EmcItemResultBean, Long>implements EmcItemResultDao {
    
    @Autowired
    private EmcItemResultMapper emcItemResultMapper;
    
    @Override
    public EmcItemResultMapper getMapper() {
        return emcItemResultMapper;
    }
    
}
