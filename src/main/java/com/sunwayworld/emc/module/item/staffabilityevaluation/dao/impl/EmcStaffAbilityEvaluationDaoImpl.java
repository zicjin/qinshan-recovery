package com.sunwayworld.emc.module.item.staffabilityevaluation.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.staffabilityevaluation.bean.EmcStaffAbilityEvaluationBean;
import com.sunwayworld.emc.module.item.staffabilityevaluation.dao.EmcStaffAbilityEvaluationDao;
import com.sunwayworld.emc.module.item.staffabilityevaluation.mapper.EmcStaffAbilityEvaluationMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcStaffAbilityEvaluationDaoImpl extends MybatisDaoSupport<EmcStaffAbilityEvaluationBean, Long> implements EmcStaffAbilityEvaluationDao {

    @Autowired
    private EmcStaffAbilityEvaluationMapper emcStaffAbilityEvaluationMapper;

    @Override
    public EmcStaffAbilityEvaluationMapper getMapper() {
        return emcStaffAbilityEvaluationMapper;
    }

}
