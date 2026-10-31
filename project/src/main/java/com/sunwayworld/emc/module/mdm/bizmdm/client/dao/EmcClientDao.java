package com.sunwayworld.emc.module.mdm.bizmdm.client.dao;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcClientBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-19
 */

public interface EmcClientDao extends GenericDao<EmcClientBean, Long> {
    List<Map<String, Object>> selectClientByCondition(MapperParameter parameter);
}
