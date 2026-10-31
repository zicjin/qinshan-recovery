package com.sunwayworld.emc.module.mdm.equipt.equipt.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckPlanBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptPerCheckPlanDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.mapper.EmcEquiptPerCheckPlanMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author liuyz@sunwayworld.com 2020年09月25日
 */
@Repository
public class EmcEquiptPerCheckPlanDaoImpl extends MybatisDaoSupport<EmcEquiptPerCheckPlanBean,Long> implements EmcEquiptPerCheckPlanDao {
    @Autowired
    private EmcEquiptPerCheckPlanMapper emcEquiptPerCheckPlanMapper;

    @Override
    public EmcEquiptPerCheckPlanMapper getMapper() {
        return emcEquiptPerCheckPlanMapper;
    }

    @Override
    public void updatePlan(EmcEquiptPerCheckPlanBean emcEquiptPerCheckPlanBean) {
        getMapper().updatePlan(emcEquiptPerCheckPlanBean);
    }
}
