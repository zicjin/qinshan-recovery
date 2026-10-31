package com.sunwayworld.emc.module.sa.qualitycontrolchart.dao;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;

public interface EmcQualityControlChartDao extends GenericDao<EmcOrderTaskResultBean, Long> {

    List<Map<String, Object>> selectOrderTaskResultPagination(MapperParameter parameter);

    List<EmcOrderTaskBean> selectTestSelectableList(MapperParameter parameter);

    List<EmcOrderTaskResultBean> selectItemSelectableList(MapperParameter parameter);

    List<EmcOrderTaskBean> selectQcCategorySelectableList(MapperParameter parameter);

    List<EmcOrderTaskBean> selectUserSelectableList(MapperParameter parameter);
}
