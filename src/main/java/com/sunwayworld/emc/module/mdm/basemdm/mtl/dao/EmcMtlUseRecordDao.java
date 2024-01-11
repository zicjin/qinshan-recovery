package com.sunwayworld.emc.module.mdm.basemdm.mtl.dao;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlUseRecordBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;


public interface EmcMtlUseRecordDao extends GenericDao<EmcMtlUseRecordBean, Long> {

	List<Map<String, Object>> selectStockLogInApply(MapperParameter parameter);
}
