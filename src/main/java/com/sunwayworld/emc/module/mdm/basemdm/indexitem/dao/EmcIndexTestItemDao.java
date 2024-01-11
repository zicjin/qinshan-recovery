package com.sunwayworld.emc.module.mdm.basemdm.indexitem.dao;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.mdm.basemdm.indexitem.bean.EmcIndexTestItemBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-17
 */

public interface EmcIndexTestItemDao extends GenericDao<EmcIndexTestItemBean, Long> {
    List<Map<String, Object>> selectIndexTestItemLimit(MapperParameter parameter);
}
