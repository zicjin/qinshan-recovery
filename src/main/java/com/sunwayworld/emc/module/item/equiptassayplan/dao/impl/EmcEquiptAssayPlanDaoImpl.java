package com.sunwayworld.emc.module.item.equiptassayplan.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.equiptassayplan.bean.EmcEquiptAssayPlanBean;
import com.sunwayworld.emc.module.item.equiptassayplan.dao.EmcEquiptAssayPlanDao;
import com.sunwayworld.emc.module.item.equiptassayplan.mapper.EmcEquiptAssayPlanMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquiptAssayPlanDaoImpl extends MybatisDaoSupport<EmcEquiptAssayPlanBean, Long> implements EmcEquiptAssayPlanDao {

    @Autowired
    private EmcEquiptAssayPlanMapper emcEquiptAssayPlanMapper;

    @Override
    public EmcEquiptAssayPlanMapper getMapper() {
        return emcEquiptAssayPlanMapper;
    }

}
