package com.sunwayworld.emc.module.mdm.bizmdm.test.dao;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */

public interface EmcItemDao extends GenericDao<EmcItemBean, Long> {
    List<Map<String, Object>> selectTestItem(MapperParameter parameter);
}
