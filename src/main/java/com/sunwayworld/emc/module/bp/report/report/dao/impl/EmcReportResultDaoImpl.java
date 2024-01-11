package com.sunwayworld.emc.module.bp.report.report.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.dao.EmcReportResultDao;
import com.sunwayworld.emc.module.bp.report.report.mapper.EmcReportResultMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-18
 */
@Repository
public class EmcReportResultDaoImpl extends MybatisDaoSupport<EmcReportResultBean, Long>implements EmcReportResultDao {
    
    @Autowired
    private EmcReportResultMapper emcReportResultMapper;
    
    @Override
    public EmcReportResultMapper getMapper() {
        return emcReportResultMapper;
    }
    
}
