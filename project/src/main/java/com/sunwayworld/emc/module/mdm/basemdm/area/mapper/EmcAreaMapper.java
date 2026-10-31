package com.sunwayworld.emc.module.mdm.basemdm.area.mapper;

import java.util.List;
import java.util.Map;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

/**
 * @author yuanhh@sunwayworld.com 2019年9月16日
 */
public interface EmcAreaMapper extends GenericMapper<Long> {
    List<Map<String, Object>> selectTreeNodeList(MapperParameter parameter);
}
