package com.sunwayworld.emc.module.bp.sample.condition.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.sample.condition.bean.EmcWorkConditionLineBean;
import com.sunwayworld.emc.module.bp.sample.condition.dao.EmcWorkConditionLineDao;
import com.sunwayworld.emc.module.bp.sample.condition.mapper.EmcWorkConditionLineMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcWorkConditionLineDaoImpl extends MybatisDaoSupport<EmcWorkConditionLineBean, Long> implements EmcWorkConditionLineDao {

    @Autowired
    private EmcWorkConditionLineMapper workConditionLineMapper;

    @Override
    public EmcWorkConditionLineMapper getMapper() {
        return workConditionLineMapper;
    }

}
