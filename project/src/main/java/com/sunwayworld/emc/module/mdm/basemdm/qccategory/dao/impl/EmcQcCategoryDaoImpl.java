package com.sunwayworld.emc.module.mdm.basemdm.qccategory.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean.EmcQcCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.dao.EmcQcCategoryDao;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.mapper.EmcQcCategoryMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcQcCategoryDaoImpl extends MybatisDaoSupport<EmcQcCategoryBean, Long>implements EmcQcCategoryDao {

    @Autowired
    private EmcQcCategoryMapper qcCategoryMapper;

    @Override
    public EmcQcCategoryMapper getMapper() {
        return qcCategoryMapper;
    }

}
