package com.sunwayworld.emc.module.bp.report.advisory.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.report.advisory.bean.EmcAdvisoryReportBean;
import com.sunwayworld.emc.module.bp.report.advisory.dao.EmcAdvisoryReportDao;
import com.sunwayworld.emc.module.bp.report.advisory.mapper.EmcAdvisoryReportMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2022年08月29日
*/
@Repository
public class EmcAdvisoryReportDaoImpl extends MybatisDaoSupport<EmcAdvisoryReportBean, Long> implements EmcAdvisoryReportDao {

    @Autowired
    private EmcAdvisoryReportMapper emcAdvisoryReportMapper;

    @Override
    public EmcAdvisoryReportMapper getMapper() {
        return emcAdvisoryReportMapper;
    }
}
