package com.sunwayworld.emc.module.bp.report.report.dao;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-18
 */

public interface EmcReportDao extends GenericDao<EmcReportBean, Long> {
    List<Map<String, Object>> selectReportOverdue(MapperParameter parameter);
}
