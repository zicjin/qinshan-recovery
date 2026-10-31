package com.sunwayworld.emc.module.mdm.equipt.equipt.mapper;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPlanBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-21
 */
public interface EmcEquiptPlanMapper extends GenericMapper<Long> {
    List<Map<String, Object>> selectEquiptMaintPlanOverdue(MapperParameter parameter);

    void updatePlan(EmcEquiptPlanBean emcEquiptPlanBean);
}
