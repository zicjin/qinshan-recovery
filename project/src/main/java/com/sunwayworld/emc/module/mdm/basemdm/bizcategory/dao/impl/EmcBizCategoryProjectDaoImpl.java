package com.sunwayworld.emc.module.mdm.basemdm.bizcategory.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean.EmcBizCategoryProjectBean;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.dao.EmcBizCategoryProjectDao;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.mapper.EmcBizCategoryProjectMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcBizCategoryProjectDaoImpl extends MybatisDaoSupport<EmcBizCategoryProjectBean, Long> implements EmcBizCategoryProjectDao {

    @Autowired
    private EmcBizCategoryProjectMapper emcBizCategoryProjectMapper;

    @Override
    public EmcBizCategoryProjectMapper getMapper() {
        return emcBizCategoryProjectMapper;
    }

}
