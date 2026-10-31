package com.sunwayworld.emc.module.mdm.basemdm.cert.mapper;

import java.util.List;
import java.util.Map;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-08
 */
public interface EmcCertMapper extends GenericMapper<Long> {

    List<Map<String, Object>> selectOverdueCert(MapperParameter parameter);

    List<Map<String, Object>> selectOverdueQual(MapperParameter parameter);

}
