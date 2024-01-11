package com.sunwayworld.emc.module.item.yeartrainplan.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.yeartrainplan.bean.EmcYearTrainPlanBean;
import com.sunwayworld.emc.module.item.yeartrainplan.dao.EmcYearTrainPlanDao;
import com.sunwayworld.emc.module.item.yeartrainplan.mapper.EmcYearTrainPlanMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcYearTrainPlanDaoImpl extends MybatisDaoSupport<EmcYearTrainPlanBean, Long> implements EmcYearTrainPlanDao {

    @Autowired
    private EmcYearTrainPlanMapper emcYearTrainPlanMapper;

    @Override
    public EmcYearTrainPlanMapper getMapper() {
        return emcYearTrainPlanMapper;
    }

}
