package com.sunwayworld.emc.module.bp.report.report.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportLineBean;
import com.sunwayworld.emc.module.bp.report.report.dao.EmcReportLineDao;
import com.sunwayworld.emc.module.bp.report.report.mapper.EmcReportLineMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2022年01月14日
*/
@Repository
public class EmcReportLineDaoImpl extends MybatisDaoSupport<EmcReportLineBean, Long> implements EmcReportLineDao {

    @Autowired
    private EmcReportLineMapper emcReportLineMapper;

    @Override
    public EmcReportLineMapper getMapper() {
        return emcReportLineMapper;
    }
}
