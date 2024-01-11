package com.sunwayworld.emc.module.bp.sample.sample.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleRemindBean;
import com.sunwayworld.emc.module.bp.sample.sample.dao.EmcSampleRemindDao;
import com.sunwayworld.emc.module.bp.sample.sample.mapper.EmcSampleRemindMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcSampleRemindDaoImpl extends MybatisDaoSupport<EmcSampleRemindBean, Long> implements EmcSampleRemindDao {

    @Autowired
    private EmcSampleRemindMapper emcSampleRemindMapper;

    @Override
    public EmcSampleRemindMapper getMapper() {
        return emcSampleRemindMapper;
    }

}
