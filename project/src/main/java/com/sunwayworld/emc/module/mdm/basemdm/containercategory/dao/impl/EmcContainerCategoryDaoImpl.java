package com.sunwayworld.emc.module.mdm.basemdm.containercategory.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.containercategory.bean.EmcContainerCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.containercategory.dao.EmcContainerCategoryDao;
import com.sunwayworld.emc.module.mdm.basemdm.containercategory.mapper.EmcContainerCategoryMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
public class EmcContainerCategoryDaoImpl extends MybatisDaoSupport<EmcContainerCategoryBean, Long> implements EmcContainerCategoryDao{


    @Autowired
    private EmcContainerCategoryMapper emcContainerCategoryMapper;

    @Override
    public EmcContainerCategoryMapper getMapper() {
        return emcContainerCategoryMapper;
    }
}
