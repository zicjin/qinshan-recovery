package com.sunwayworld.emc.module.bp.bp.dao;

import java.util.List;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;

/**
 * @author zhengxy@sunwayworld.com 2022年03月24日
 */
public interface PfOrderTaskDao extends EmcOrderTaskDao {

    List<EmcOrderTaskBean> selectUniqueOrderList(MapperParameter parameter);

    List<EmcOrderTaskBean> selectTaskGroupByTest(MapperParameter parameter);

    List<EmcOrderTaskBean> selectOrderContainerInfo(MapperParameter parameter);

    List<EmcOrderTaskBean> selectOrderTaskPagination(MapperParameter parameter);
}
