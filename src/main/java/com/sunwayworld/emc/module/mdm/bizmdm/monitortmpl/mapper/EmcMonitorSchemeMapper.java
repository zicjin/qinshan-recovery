package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.mapper;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

import java.util.List;
import java.util.Map;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-19
 */
@GikamBean
public interface EmcMonitorSchemeMapper extends GenericMapper<Long> {
    List<Map<String, Object>> selectDetectionChoosablePagination(MapperParameter parameter);
    List<Map<String, Object>> selectDetectionChoosablePaginationDeWeight(MapperParameter parameter);
}
