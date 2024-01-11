package com.sunwayworld.emc.module.mdm.bizmdm.capacity.dao;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */

public interface EmcCapacityItemDao extends GenericDao<EmcCapacityItemBean, Long> {
    List<Map<String, Object>> selectTestItem(MapperParameter parameter);
}
