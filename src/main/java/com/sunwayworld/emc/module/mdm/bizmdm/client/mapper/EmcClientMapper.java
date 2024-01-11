package com.sunwayworld.emc.module.mdm.bizmdm.client.mapper;

import java.util.List;
import java.util.Map;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-19
 */
@GikamBean
public interface EmcClientMapper extends GenericMapper<Long> {
    List<Map<String, Object>> selectClientByCondition(MapperParameter parameter);
}
