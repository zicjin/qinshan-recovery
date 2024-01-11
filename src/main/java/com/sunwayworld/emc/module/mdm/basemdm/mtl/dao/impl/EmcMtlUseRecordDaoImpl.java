package com.sunwayworld.emc.module.mdm.basemdm.mtl.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlUseRecordBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.dao.EmcMtlUseRecordDao;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.mapper.EmcMtlUseRecordMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcMtlUseRecordDaoImpl extends MybatisDaoSupport<EmcMtlUseRecordBean, Long> implements EmcMtlUseRecordDao {

    @Autowired
    private EmcMtlUseRecordMapper emcMtlUseRecordMapper;

    @Override
    public EmcMtlUseRecordMapper getMapper() {
        return emcMtlUseRecordMapper;
    }

	@Override
	public List<Map<String, Object>> selectStockLogInApply(MapperParameter parameter) {
		return getMapper().selectStockLogInApply(parameter); 
	}


}
