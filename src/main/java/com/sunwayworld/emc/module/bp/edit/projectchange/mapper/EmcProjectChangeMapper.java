package com.sunwayworld.emc.module.bp.edit.projectchange.mapper;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjectChangeBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-27
 */
@GikamBean
public interface EmcProjectChangeMapper extends GenericMapper<Long> {

    List<Map<String, Object>> selectProjectStatistics(MapperParameter parameter);

    List<Map<String, Object>> selectAnnualProjectStatistics(MapperParameter parameter);

    List<Map<String, Object>> selectAnalysisAuditProject(MapperParameter parameter);

    List<Map<String, Object>> selectMaxProjNoList();

    List<EmcProjectChangeBean> selectProjectByContractId(Long contractId);

    List<Map<String, Object>> selectTodoAnalysisAuditProject(MapperParameter parameter);

    List<Map<String, Object>> selectTodoAnalysisPretrialProject(MapperParameter parameter);
}
