package com.sunwayworld.emc.module.mdm.equipt.equipt.mapper;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckPlanBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;

/**
 * @author liuyz@sunwayworld.com 2020年09月25日
 */
public interface EmcEquiptPerCheckPlanMapper extends GenericMapper<Long> {
    void updatePlan(EmcEquiptPerCheckPlanBean emcEquiptPerCheckPlanBean);

    List<Map<String, Object>> selectGlobalPerCheckPlans(MapperParameter parameter);
}
