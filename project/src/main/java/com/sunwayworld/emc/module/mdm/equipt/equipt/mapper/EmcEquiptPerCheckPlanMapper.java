package com.sunwayworld.emc.module.mdm.equipt.equipt.mapper;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckPlanBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

/**
 * @author liuyz@sunwayworld.com 2020年09月25日
 */
public interface EmcEquiptPerCheckPlanMapper extends GenericMapper<Long> {
    void updatePlan(EmcEquiptPerCheckPlanBean emcEquiptPerCheckPlanBean);
}
