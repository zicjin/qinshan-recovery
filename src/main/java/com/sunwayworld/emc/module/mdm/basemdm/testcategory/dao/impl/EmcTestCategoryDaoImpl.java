package com.sunwayworld.emc.module.mdm.basemdm.testcategory.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.testcategory.bean.EmcTestCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.testcategory.dao.EmcTestCategoryDao;
import com.sunwayworld.emc.module.mdm.basemdm.testcategory.mapper.EmcTestCategoryMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
public class EmcTestCategoryDaoImpl extends MybatisDaoSupport<EmcTestCategoryBean, Long> implements EmcTestCategoryDao {

    @Autowired
    private EmcTestCategoryMapper emcTestCategoryMapper;

    @Override
    public EmcTestCategoryMapper getMapper() {
        return emcTestCategoryMapper;
    }

}
