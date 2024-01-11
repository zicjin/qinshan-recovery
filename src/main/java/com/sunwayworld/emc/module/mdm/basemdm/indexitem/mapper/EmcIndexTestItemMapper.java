package com.sunwayworld.emc.module.mdm.basemdm.indexitem.mapper;

import java.util.List;
import java.util.Map;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-17
 */
@GikamBean
public interface EmcIndexTestItemMapper extends GenericMapper<Long> {
    List<Map<String, Object>> selectIndexTestItemLimit(MapperParameter parameter);
}
