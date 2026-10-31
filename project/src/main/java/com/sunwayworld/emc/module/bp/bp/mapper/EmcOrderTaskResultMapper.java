package com.sunwayworld.emc.module.bp.bp.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */
@Repository
@GikamBean
public interface EmcOrderTaskResultMapper extends GenericMapper<Long> {

    List<Map<String, Object>> selectScriptByCondition(MapperParameter parameter);

    List<Map<String, Object>> selectUpdateIndexOrderTaskResult(MapperParameter parameter);
    
    List<Map<String, Object>> selectAnalysisProjectTaskResultValidation(MapperParameter parameter);
}
