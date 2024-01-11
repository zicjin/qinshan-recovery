package com.sunwayworld.emc.module.mdm.bizmdm.comparison.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.comparison.bean.EmcComparisonResultBean;
import com.sunwayworld.emc.module.mdm.bizmdm.comparison.dao.EmcComparisonResultDao;
import com.sunwayworld.emc.module.mdm.bizmdm.comparison.mapper.EmcComparisonResultMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2020年10月22日
*/
@Repository
public class EmcComparisonResultDaoImpl extends MybatisDaoSupport<EmcComparisonResultBean, Long> implements EmcComparisonResultDao {

    @Autowired
    private EmcComparisonResultMapper emcComparisonResultMapper;

    @Override
    public EmcComparisonResultMapper getMapper() {
        return emcComparisonResultMapper;
    }
}
