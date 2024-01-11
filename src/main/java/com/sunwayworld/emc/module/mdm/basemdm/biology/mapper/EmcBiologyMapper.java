package com.sunwayworld.emc.module.mdm.basemdm.biology.mapper;

import java.util.List;
import java.util.Map;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-28
 */
public interface EmcBiologyMapper extends GenericMapper<Long> {
    
    List<Map<String, Object>> selectTreeNodeList(MapperParameter parameter);

    
}
