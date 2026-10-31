package com.sunwayworld.emc.module.mdm.basemdm.mtlcategory.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.mtlcategory.bean.EmcMtlCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtlcategory.dao.EmcMtlCategoryDao;
import com.sunwayworld.emc.module.mdm.basemdm.mtlcategory.mapper.EmcMtlCategoryMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author magh@sunwayworld.com
 * @date 2019-09-16
 */
@Repository
public class EmcMtlCategoryDaoImpl extends MybatisDaoSupport<EmcMtlCategoryBean, Long>implements EmcMtlCategoryDao {
    
    @Autowired
    private EmcMtlCategoryMapper emcMtlCategoryMapper;
    
    @Override
    public EmcMtlCategoryMapper getMapper() {
        return emcMtlCategoryMapper;
    }
    
}
