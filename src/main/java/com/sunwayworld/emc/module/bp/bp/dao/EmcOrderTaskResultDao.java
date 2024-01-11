package com.sunwayworld.emc.module.bp.bp.dao;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

public interface EmcOrderTaskResultDao extends GenericDao<EmcOrderTaskResultBean, Long> {

    List<Map<String, Object>> selectScriptByCondition(MapperParameter parameter);
    
    List<Map<String, Object>> selectUpdateIndexOrderTaskResult(MapperParameter parameter);

    List<Map<String, Object>> selectAnalysisProjectTaskResultValidation(MapperParameter parameter);

}
