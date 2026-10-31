package com.sunwayworld.emc.module.bp.bp.mapper;

import java.util.List;
import java.util.Map;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangjian0705@sunwayworld.com
 * @date 2021/7/15 14:15
 */
@GikamBean
public interface PfOrderTaskResultMapper extends EmcOrderTaskResultMapper {

    List<Map<String, Object>> selectResultInputResultList(MapperParameter parameter);

    List<Map<String, Object>> selectQuickInputResultList(MapperParameter parameter);

    List<Map<String, Object>> selectResultRemind(MapperParameter parameter);

    List<Map<String, Object>> selectQuickInputResultListBySampleParam(MapperParameter parameter);

    List<Map<String, Object>> selectQuickInputResultListBySampleId(MapperParameter parameter);
}
