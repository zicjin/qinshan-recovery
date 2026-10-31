package com.sunwayworld.emc.module.bp.bp.dao;

import java.util.List;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderContainerBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

public interface EmcOrderContainerDao extends GenericDao<EmcOrderContainerBean, Long> {

    List<EmcOrderContainerBean> selectOrderContainerList(MapperParameter parameter);

    List<EmcOrderContainerBean> selectOrderReceiveContainerList(MapperParameter parameter);
}
