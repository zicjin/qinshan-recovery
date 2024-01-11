package com.sunwayworld.emc.module.bp.bp.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.dao.EmcOrderTaskResultDao;
import com.sunwayworld.emc.module.bp.bp.mapper.EmcOrderTaskResultMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableMetadataHelper;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */
@Repository
@GikamBean
public class EmcOrderTaskResultDaoImpl extends MybatisDaoSupport<EmcOrderTaskResultBean, Long>implements EmcOrderTaskResultDao {

    @Autowired
    private EmcOrderTaskResultMapper emcOrderTaskResultMapper;

    @Override
    public EmcOrderTaskResultMapper getMapper() {
        return emcOrderTaskResultMapper;
    }

    @Override
    public List<Map<String, Object>> selectScriptByCondition(MapperParameter parameter) {
        return getMapper().selectScriptByCondition(parameter);
    }

    public List<Map<String, Object>> selectUpdateIndexOrderTaskResult(MapperParameter parameter) {
        parameter.setTableName(PersistableMetadataHelper.getTableName(this.getType()));
        return getMapper().selectUpdateIndexOrderTaskResult(parameter);
    }

    public List<Map<String, Object>> selectAnalysisProjectTaskResultValidation(MapperParameter parameter) {
        return emcOrderTaskResultMapper.selectAnalysisProjectTaskResultValidation(parameter);
    }
}
