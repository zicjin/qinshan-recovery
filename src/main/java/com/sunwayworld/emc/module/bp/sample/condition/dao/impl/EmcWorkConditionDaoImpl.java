package com.sunwayworld.emc.module.bp.sample.condition.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.sample.condition.bean.EmcWorkConditionBean;
import com.sunwayworld.emc.module.bp.sample.condition.dao.EmcWorkConditionDao;
import com.sunwayworld.emc.module.bp.sample.condition.mapper.EmcWorkConditionMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcWorkConditionDaoImpl extends MybatisDaoSupport<EmcWorkConditionBean, Long> implements EmcWorkConditionDao {

    @Autowired
    private EmcWorkConditionMapper emcWorkConditionMapper;

    @Override
    public EmcWorkConditionMapper getMapper() {
        return emcWorkConditionMapper;
    }

}
