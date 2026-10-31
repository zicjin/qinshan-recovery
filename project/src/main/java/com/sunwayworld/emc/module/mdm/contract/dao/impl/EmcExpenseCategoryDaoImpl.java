package com.sunwayworld.emc.module.mdm.contract.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.contract.bean.EmcExpenseCategoryBean;
import com.sunwayworld.emc.module.mdm.contract.dao.EmcExpenseCategoryDao;
import com.sunwayworld.emc.module.mdm.contract.mapper.EmcExpenseCategoryMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcExpenseCategoryDaoImpl extends MybatisDaoSupport<EmcExpenseCategoryBean, Long> implements EmcExpenseCategoryDao {

    @Autowired
    private EmcExpenseCategoryMapper emcExpenseCategoryMapper;

    @Override
    public EmcExpenseCategoryMapper getMapper() {
        return emcExpenseCategoryMapper;
    }

}
