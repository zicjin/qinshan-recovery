package com.sunwayworld.emc.module.mdm.equipt.equipt.dao;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckPlanBean;
import com.sunwayworld.framework.support.base.dao.GenericDao;

/**
 * @author liuyz@sunwayworld.com 2020年09月25日
 */
public interface EmcEquiptPerCheckPlanDao extends GenericDao<EmcEquiptPerCheckPlanBean,Long> {
    void updatePlan(EmcEquiptPerCheckPlanBean emcEquiptPerCheckPlanBean);
}
