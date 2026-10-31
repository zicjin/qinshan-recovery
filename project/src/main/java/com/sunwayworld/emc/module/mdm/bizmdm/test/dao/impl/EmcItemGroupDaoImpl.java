package com.sunwayworld.emc.module.mdm.bizmdm.test.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcItemGroupDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.mapper.EmcItemGroupMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */
@Repository
public class EmcItemGroupDaoImpl extends MybatisDaoSupport<EmcItemGroupBean, Long>implements EmcItemGroupDao {
    
    @Autowired
    private EmcItemGroupMapper emcItemGroupMapper;
    
    @Override
    public EmcItemGroupMapper getMapper() {
        return emcItemGroupMapper;
    }
    
}
