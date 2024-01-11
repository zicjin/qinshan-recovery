package com.sunwayworld.emc.module.bp.result.run.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.result.run.dao.EmcRunDao;
import com.sunwayworld.emc.module.bp.result.run.mapper.EmcRunMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;

@Repository
public class EmcRunDaoImpl extends MybatisDaoSupport<EmcRunBean, String>implements EmcRunDao {

    @Autowired
    private EmcRunMapper runMapper;

    @Override
    public EmcRunMapper getMapper() {
        return runMapper;
    }

    @Override
    public List<Map<String, Object>> selectCurveInfoByCondition(MapperParameter parameter) {
        return getMapper().selectCurveInfoByCondition(parameter);
    }
    
    @Override
    public List<Map<String, Object>> selectAnalysisAuditRun(MapperParameter parameter) {
    	return getMapper().selectAnalysisAuditRun(parameter);
    }
    
    @Override
    public List<Map<String, Object>> selectRunResult(MapperParameter parameter) {
    	return getMapper().selectRunResult(parameter);
    }

    @Override
    public List<Map<String, Object>> selectRunTodoList(MapperParameter parameter) {
        return getMapper().selectRunTodoList(parameter);
    }

    @Override
    public List<Map<String, Object>> selectCheckRunTodoList(MapperParameter parameter) {
        return getMapper().selectCheckRunTodoList(parameter);
    }

    @Override
    public List<Map<String, Object>> selectSubmitRunTodoList(MapperParameter parameter) {
        return getMapper().selectSubmitRunTodoList(parameter);
    }
}
