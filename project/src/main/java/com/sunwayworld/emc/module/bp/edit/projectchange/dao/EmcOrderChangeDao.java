package com.sunwayworld.emc.module.bp.edit.projectchange.dao;

import java.util.List;

import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderChangeBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

public interface EmcOrderChangeDao extends GenericDao<EmcOrderChangeBean, Long> {

    List<EmcOrderChangeBean> selectOrderList(MapperParameter parameter);
}
