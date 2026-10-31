package com.sunwayworld.emc.module.bp.report.report.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.dao.EmcReportDao;
import com.sunwayworld.emc.module.bp.report.report.mapper.EmcReportMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;


/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-18
 */
@Repository
@GikamBean
public class EmcReportDaoImpl extends MybatisDaoSupport<EmcReportBean, Long>implements EmcReportDao {
    
    @Autowired
    private EmcReportMapper emcReportMapper;
    
    @Override
    public EmcReportMapper getMapper() {
        return emcReportMapper;
    }

    @Override
    public List<Map<String, Object>> selectReportOverdue(MapperParameter parameter) {
        return getMapper().selectReportOverdue(parameter);
    }
}
