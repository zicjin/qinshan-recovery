package com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.dao.EmcIndexCategoryDao;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.mapper.EmcIndexCategoryMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
public class EmcIndexCategoryDaoImpl extends MybatisDaoSupport<EmcIndexCategoryBean, Long> implements EmcIndexCategoryDao {

    @Autowired
    private EmcIndexCategoryMapper emcIndexCategoryMapper;

    @Override
    public EmcIndexCategoryMapper getMapper() {
        return emcIndexCategoryMapper;
    }

}
