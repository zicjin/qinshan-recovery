package com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.dao.EmcEquiptMaintDao;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.mapper.EmcEquiptMaintMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author magh@sunwayworld.com 2019年9月4日
 */
@Repository
public class EmcEquiptMaintDaoImpl extends MybatisDaoSupport<EmcEquiptMaintBean, Long> implements EmcEquiptMaintDao {

    @Autowired
    private EmcEquiptMaintMapper emcEquiptMaintMapper;

    @Override
    public EmcEquiptMaintMapper getMapper() {
        return emcEquiptMaintMapper;
    }

}
