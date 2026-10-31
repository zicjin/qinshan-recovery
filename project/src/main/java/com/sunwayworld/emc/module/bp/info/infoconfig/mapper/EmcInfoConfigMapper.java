package com.sunwayworld.emc.module.bp.info.infoconfig.mapper;

import java.util.List;
import java.util.Map;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

public interface EmcInfoConfigMapper extends GenericMapper<Long> {

    List<Map<String,Object>> selectTreeNodeList(MapperParameter parameter);
    
}
