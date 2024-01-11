package com.sunwayworld.emc.module.mdm.org.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.org.bean.EmcOrgPermissionBean;
import com.sunwayworld.emc.module.mdm.org.dao.EmcOrgPermissionDao;
import com.sunwayworld.emc.module.mdm.org.mapper.EmcOrgPermissionMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

@Repository
public class EmcOrgPermissionDaoImpl extends MybatisDaoSupport<EmcOrgPermissionBean, Long> implements EmcOrgPermissionDao {

    @Autowired
    private EmcOrgPermissionMapper emcOrgPermissionMapper;

    @Override
    public GenericMapper<Long> getMapper() {
        return emcOrgPermissionMapper;
    }

}
