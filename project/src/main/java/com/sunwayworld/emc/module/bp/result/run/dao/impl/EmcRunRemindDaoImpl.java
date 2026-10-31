package com.sunwayworld.emc.module.bp.result.run.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunRemindBean;
import com.sunwayworld.emc.module.bp.result.run.dao.EmcRunRemindDao;
import com.sunwayworld.emc.module.bp.result.run.mapper.EmcRunRemindMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcRunRemindDaoImpl extends MybatisDaoSupport<EmcRunRemindBean, Long> implements EmcRunRemindDao {

    @Autowired
    private EmcRunRemindMapper emcRunRemindMapper;

    @Override
    public EmcRunRemindMapper getMapper() {
        return emcRunRemindMapper;
    }

}
