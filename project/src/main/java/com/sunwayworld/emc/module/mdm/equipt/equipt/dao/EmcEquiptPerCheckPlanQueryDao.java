package com.sunwayworld.emc.module.mdm.equipt.equipt.dao;

import java.util.List;
import java.util.Map;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;

/** Global read projection; the original equipment-scoped CRUD contract is unchanged. */
public interface EmcEquiptPerCheckPlanQueryDao {
    List<Map<String, Object>> selectGlobalPerCheckPlans(MapperParameter parameter);
}
