package com.sunwayworld.emc.module.mdm.bizmdm.test.mapper;

import java.util.List;
import java.util.Map;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */
@GikamBean
public interface EmcItemMapper extends GenericMapper<Long> {
    List<Map<String, Object>> selectTestItem(MapperParameter parameter);
}
