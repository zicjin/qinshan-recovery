package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.dao.impl;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.dao.EmcMonitorSchemeDao;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.mapper.EmcMonitorSchemeMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

import java.util.List;
import java.util.Map;


/**
 * @author magh@sunwayworld.com
 * @date 2019-09-19
 */
@Repository
public class EmcMonitorSchemeDaoImpl extends MybatisDaoSupport<EmcMonitorSchemeBean, Long>implements EmcMonitorSchemeDao {
    
    @Autowired
    private EmcMonitorSchemeMapper emcMonitorSchemeMapper;
    
    @Override
    public EmcMonitorSchemeMapper getMapper() {
        return emcMonitorSchemeMapper;
    }

    @Override
    public List<Map<String, Object>> selectDetectionChoosablePagination(MapperParameter parameter) {
        return getMapper().selectDetectionChoosablePagination(parameter);
    }

	@Override
	public List<Map<String, Object>> selectDetectionChoosablePaginationDeWeight(MapperParameter parameter) {
		return getMapper().selectDetectionChoosablePaginationDeWeight(parameter);
	}

	@Override
	public List<Map<String, Object>> selectByConditionGroupByTestMethod(MapperParameter parameter) {
		return getMapper().selectByConditionGroupByTestMethod(parameter);
	}
}
