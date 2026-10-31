package com.sunwayworld.emc.module.bp.info.reporttmpl.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.info.reporttmpl.bean.EmcReportTmplBean;
import com.sunwayworld.emc.module.bp.info.reporttmpl.dao.EmcReportTmplDao;
import com.sunwayworld.emc.module.bp.info.reporttmpl.mapper.EmcReportTmplMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-04
 */
@Repository
public class EmcReportTmplDaoImpl extends MybatisDaoSupport<EmcReportTmplBean, Long> implements EmcReportTmplDao {

    @Autowired
    private EmcReportTmplMapper reportTmplMapper;

    @Override
    public EmcReportTmplMapper getMapper() {
        return reportTmplMapper;
    }

}
