package com.sunwayworld.emc.module.mdm.basemdm.bizcategory.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean.EmcBizCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.dao.EmcBizCategoryDao;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.mapper.EmcBizCategoryMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
public class EmcBizCategoryDaoImpl extends MybatisDaoSupport<EmcBizCategoryBean, Long> implements EmcBizCategoryDao{


    @Autowired
    private EmcBizCategoryMapper emcBizCategoryMapper;

    @Override
    public EmcBizCategoryMapper getMapper() {
        return emcBizCategoryMapper;
    }
}
