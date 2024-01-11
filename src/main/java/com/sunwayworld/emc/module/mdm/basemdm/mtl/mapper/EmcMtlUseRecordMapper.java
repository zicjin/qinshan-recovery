package com.sunwayworld.emc.module.mdm.basemdm.mtl.mapper;

import java.util.List;
import java.util.Map;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

@GikamBean
public interface EmcMtlUseRecordMapper extends GenericMapper<Long> {

	List<Map<String, Object>> selectStockLogInApply(MapperParameter param);

}
