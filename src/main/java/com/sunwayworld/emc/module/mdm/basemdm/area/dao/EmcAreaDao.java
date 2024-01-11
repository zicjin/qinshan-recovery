package com.sunwayworld.emc.module.mdm.basemdm.area.dao;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.mdm.basemdm.area.bean.EmcAreaBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;

/**
 * @author yuanhh@sunwayworld.com 2019年9月16日
 */

public interface EmcAreaDao extends GenericDao<EmcAreaBean, Long> {
    List<Map<String, Object>> selectTreeNodeList(MapperParameter parameter);
}
