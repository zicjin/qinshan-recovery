package com.sunwayworld.emc.module.sa.qualitycontrolchart.mapper;

import java.util.List;
import java.util.Map;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

public interface EmcQualityControlChartMapper extends GenericMapper<Long> {
    List<Map<String, Object>> selectOrderTaskResultPagination(MapperParameter parameter);

    List<Map<String, Object>> selectTestSelectableList(MapperParameter parameter);

    List<Map<String, Object>> selectItemSelectableList(MapperParameter parameter);

    List<Map<String, Object>> selectQcCategorySelectableList(MapperParameter parameter);

    List<Map<String, Object>> selectUserSelectableList(MapperParameter parameter);
}
