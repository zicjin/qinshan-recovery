package com.sunwayworld.emc.module.mdm.bizmdm.test.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcRelatedTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcRelatedTestMethodDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.mapper.EmcRelatedTestMethodMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@Repository
public class EmcRelatedTestMethodDaoImpl extends MybatisDaoSupport<EmcRelatedTestMethodBean, Long> implements EmcRelatedTestMethodDao {

    @Autowired
    private EmcRelatedTestMethodMapper emcRelatedTestMethodMapper;

    @Override
    public EmcRelatedTestMethodMapper getMapper() {
        return emcRelatedTestMethodMapper;
    }
}
