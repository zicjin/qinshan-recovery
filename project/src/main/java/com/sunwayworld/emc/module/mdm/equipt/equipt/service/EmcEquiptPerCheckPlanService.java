package com.sunwayworld.emc.module.mdm.equipt.equipt.service;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckPlanBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author liuyz@sunwayworld.com 2020年09月25日
 */
public interface EmcEquiptPerCheckPlanService extends GenericService<EmcEquiptPerCheckPlanBean, Long> {

    void updatePlan(EmcEquiptPerCheckPlanBean emcEquiptPerCheckPlanBean);
}
