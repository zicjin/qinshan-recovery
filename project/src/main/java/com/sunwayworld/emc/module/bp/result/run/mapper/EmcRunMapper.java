package com.sunwayworld.emc.module.bp.result.run.mapper;

import java.util.List;
import java.util.Map;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

@GikamBean
public interface EmcRunMapper extends GenericMapper<String> {
    List<Map<String, Object>> selectCurveInfoByCondition(MapperParameter parameter);
    
    List<Map<String, Object>> selectAnalysisAuditRun(MapperParameter parameter);
    
    List<Map<String, Object>> selectRunResult(MapperParameter parameter);

    List<Map<String, Object>> selectRunTodoList(MapperParameter parameter);

    List<Map<String, Object>> selectCheckRunTodoList(MapperParameter parameter);

    List<Map<String, Object>> selectSubmitRunTodoList(MapperParameter parameter);

}
