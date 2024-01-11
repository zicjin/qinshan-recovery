package com.sunwayworld.emc.module.bp.project.project.dao;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.dao.GenericDao;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-27
 */
@GikamBean
public interface EmcProjectDao extends GenericDao<EmcProjectBean, Long> {

    List<Map<String, Object>> selectProjectStatistics(MapperParameter parameter);

    List<Map<String, Object>> selectAnnualProjectStatistics(MapperParameter parameter);

    List<Map<String, Object>> selectAnalysisAuditProject(MapperParameter parameter);

    List<Map<String, Object>> selectMaxProjNoList();

    List<EmcProjectBean> selectProjectByContractId(Long contractId);

    List<Map<String, Object>> selectTodoAnalysisAuditProject(MapperParameter parameter);

    List<Map<String, Object>> selectTodoAnalysisPretrialProject(MapperParameter parameter);
}
