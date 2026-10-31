package com.sunwayworld.emc.module.bp.bp.dao;

import java.util.List;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;

/**
 * @author zhangjian0705@sunwayworld.com
 * @date 2021/7/15 14:11
 */
public interface PfOrderTaskResultDao extends EmcOrderTaskResultDao {

    List<EmcOrderTaskResultBean> selectResultInputResultList(MapperParameter parameter);

    List<EmcOrderTaskResultBean> selectQuickInputResultList(MapperParameter parameter);

    List<EmcOrderTaskResultBean> selectResultRemind(MapperParameter parameter);

    List<EmcOrderTaskResultBean> selectQuickInputResultListBySampleParam(MapperParameter parameter);

    List<EmcOrderTaskResultBean> selectQuickInputResultListBySampleId(MapperParameter parameter);
}
