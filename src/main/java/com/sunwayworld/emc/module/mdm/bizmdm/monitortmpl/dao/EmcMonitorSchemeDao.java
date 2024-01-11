package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.dao;

import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;

import java.util.List;
import java.util.Map;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-19
 */

public interface EmcMonitorSchemeDao extends GenericDao<EmcMonitorSchemeBean, Long> {
    List<Map<String, Object>> selectDetectionChoosablePagination(MapperParameter parameter);
    List<Map<String, Object>> selectDetectionChoosablePaginationDeWeight(MapperParameter parameter);
}
