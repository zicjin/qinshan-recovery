package com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.dao.EmcEquiptCategoryDao;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.mapper.EmcEquiptCategoryMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author magh@sunwayworld.com 2019年9月4日
 */
@Repository
public class EmcEquiptCategoryDaoImpl extends MybatisDaoSupport<EmcEquiptCategoryBean, Long> implements EmcEquiptCategoryDao {

    @Autowired
    private EmcEquiptCategoryMapper emcEquiptCategoryMapper;

    @Override
    public EmcEquiptCategoryMapper getMapper() {
        return emcEquiptCategoryMapper;
    }

}
