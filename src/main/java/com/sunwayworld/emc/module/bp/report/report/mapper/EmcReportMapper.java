package com.sunwayworld.emc.module.bp.report.report.mapper;

import java.util.List;
import java.util.Map;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-18
 */
@GikamBean
public interface EmcReportMapper extends GenericMapper<Long> {
    List<Map<String, Object>> selectReportOverdue(MapperParameter parameter);
}
