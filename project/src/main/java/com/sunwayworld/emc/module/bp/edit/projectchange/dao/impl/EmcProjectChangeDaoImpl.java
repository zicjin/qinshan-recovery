package com.sunwayworld.emc.module.bp.edit.projectchange.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjectChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.dao.EmcProjectChangeDao;
import com.sunwayworld.emc.module.bp.edit.projectchange.mapper.EmcProjectChangeMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-27
 */
@Repository
@GikamBean
public class EmcProjectChangeDaoImpl extends MybatisDaoSupport<EmcProjectChangeBean, Long>implements EmcProjectChangeDao {

    @Autowired
    private EmcProjectChangeMapper emcProjectMapper;

    @Override
    public EmcProjectChangeMapper getMapper() {
        return emcProjectMapper;
    }

    @Override
    public List<Map<String, Object>> selectProjectStatistics(MapperParameter parameter) {
        return getMapper().selectProjectStatistics(parameter);
    }

    @Override
    public List<Map<String, Object>> selectAnnualProjectStatistics(MapperParameter parameter) {
        return getMapper().selectAnnualProjectStatistics(parameter);
    }

    @Override
    public List<Map<String, Object>> selectAnalysisAuditProject(MapperParameter parameter) {
        return getMapper().selectAnalysisAuditProject(parameter);
    }

    @Override
    public List<Map<String, Object>> selectMaxProjNoList() {
        return getMapper().selectMaxProjNoList();
    }

    @Override
    public List<EmcProjectChangeBean> selectProjectByContractId(Long contractId) {
        return getMapper().selectProjectByContractId(contractId);
    }

    @Override
    public List<Map<String, Object>> selectTodoAnalysisAuditProject(MapperParameter parameter) {
        return getMapper().selectTodoAnalysisAuditProject(parameter);
    }

    @Override
    public List<Map<String, Object>> selectTodoAnalysisPretrialProject(MapperParameter parameter) {
        return getMapper().selectTodoAnalysisPretrialProject(parameter);
    }
}
