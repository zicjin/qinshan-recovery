package com.sunwayworld.emc.module.mdm.basemdm.biology.dao;

import java.util.List;

import com.sunwayworld.emc.module.mdm.basemdm.biology.bean.EmcBiologyBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-28
 */

public interface EmcBiologyDao extends GenericDao<EmcBiologyBean, Long> {

    List<EmcBiologyBean> selectTreeNodeList(MapperParameter parameter);
}
