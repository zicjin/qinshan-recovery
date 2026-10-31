package com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.mapper;

import java.util.List;
import java.util.Map;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

public interface EmcStdSetCategoryMapper extends GenericMapper<Long> {

    List<Map<String, Object>> selectFieldInfoPagination(MapperParameter mapperParameter);

}
