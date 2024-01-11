package com.sunwayworld.emc.module.bp.result.run.dao;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;

public interface EmcRunDao extends GenericDao<EmcRunBean, String> {

    List<Map<String, Object>> selectCurveInfoByCondition(MapperParameter parameter);
    
    List<Map<String, Object>> selectAnalysisAuditRun(MapperParameter parameter);
    
    List<Map<String, Object>> selectRunResult(MapperParameter parameter);

    List<Map<String, Object>> selectRunTodoList(MapperParameter parameter);

    List<Map<String, Object>> selectCheckRunTodoList(MapperParameter parameter);

    List<Map<String, Object>> selectSubmitRunTodoList(MapperParameter parameter);
}
