package com.sunwayworld.emc.module.mdm.bizmdm.comparison.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.comparison.bean.EmcComparisonBean;
import com.sunwayworld.emc.module.mdm.bizmdm.comparison.dao.EmcComparisonDao;
import com.sunwayworld.emc.module.mdm.bizmdm.comparison.mapper.EmcComparisonMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2020年08月17日
*/
@Repository
public class EmcComparisonDaoImpl extends MybatisDaoSupport<EmcComparisonBean, Long> implements EmcComparisonDao {

    @Autowired
    private EmcComparisonMapper emcComparisonMapper;

    @Override
    public EmcComparisonMapper getMapper() {
        return emcComparisonMapper;
    }
}
