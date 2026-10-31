package com.sunwayworld.emc.module.mdm.equipt.equipt.dao;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPlanBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-21
 */

public interface EmcEquiptPlanDao extends GenericDao<EmcEquiptPlanBean, Long> {

    List<Map<String, Object>> selectEquiptMaintPlanOverdue(MapperParameter parameter);

    void updatePlan(EmcEquiptPlanBean emcEquiptPlanBean);
}
