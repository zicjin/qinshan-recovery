package com.sunwayworld.emc.module.mdm.bizmdm.test.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcQcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcQcItemDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.mapper.EmcQcItemMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * 
 * @author changsp@sunwayworld.com
 * @date 2021-07-16
 */
@Repository
public class EmcQcItemDaoImpl extends MybatisDaoSupport<EmcQcItemBean, Long>implements EmcQcItemDao {
    
    @Autowired
    private EmcQcItemMapper emcQcItemMapper;
    
    @Override
    public EmcQcItemMapper getMapper() {
        return emcQcItemMapper;
    }
    
}
