package com.sunwayworld.emc.module.mdm.basemdm.filecategory.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.filecategory.bean.EmcFileCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.filecategory.dao.EmcFileCategoryDao;
import com.sunwayworld.emc.module.mdm.basemdm.filecategory.mapper.EmcFileCategoryMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

/**
 * @author zhengxy@sunwayworld.com 2019年10月9日
 */
@Repository
public class EmcFileCategoryDaoImpl extends MybatisDaoSupport<EmcFileCategoryBean, Long> implements EmcFileCategoryDao {

    @Autowired
    private EmcFileCategoryMapper emcFileCategoryMapper;

    @Override
    public GenericMapper<Long> getMapper() {
        return emcFileCategoryMapper;
    }
}
