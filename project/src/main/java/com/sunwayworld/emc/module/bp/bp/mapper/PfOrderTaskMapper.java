package com.sunwayworld.emc.module.bp.bp.mapper;

import java.util.List;
import java.util.Map;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@GikamBean
public interface PfOrderTaskMapper extends EmcOrderTaskMapper {

    List<Map<String, Object>> selectUniqueOrderList(MapperParameter parameter);

    List<Map<String, Object>> selectTaskGroupByTest(MapperParameter parameter);

    List<Map<String, Object>> selectOrderContainerInfo(MapperParameter parameter);

    List<Map<String, Object>> selectOrderTaskPagination(MapperParameter parameter);
}
