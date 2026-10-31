package com.sunwayworld.emc.module.bp.info.reporttmpl.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.info.reporttmpl.bean.EmcReportTmplLineBean;
import com.sunwayworld.emc.module.bp.info.reporttmpl.dao.EmcReportTmplLineDao;
import com.sunwayworld.emc.module.bp.info.reporttmpl.mapper.EmcReportTmplLineMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
public class EmcReportTmplLineDaoImpl extends MybatisDaoSupport<EmcReportTmplLineBean, Long> implements EmcReportTmplLineDao {

    @Autowired
    private EmcReportTmplLineMapper reportTmplLineMapper;

    @Override
    public EmcReportTmplLineMapper getMapper() {
        return reportTmplLineMapper;
    }

}
