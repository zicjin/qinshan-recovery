package com.sunwayworld.emc.module.mdm.basemdm.reportinforeference.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.reportinforeference.bean.EmcReportInfoReferenceBean;
import com.sunwayworld.emc.module.mdm.basemdm.reportinforeference.dao.EmcReportInfoReferenceDao;
import com.sunwayworld.emc.module.mdm.basemdm.reportinforeference.mapper.EmcReportInfoReferenceMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author zhengxy@sunwayworld.com 2020年03月06日
 */
@Repository
public class EmcReportInfoReferenceDaoImpl extends MybatisDaoSupport<EmcReportInfoReferenceBean, Long> implements EmcReportInfoReferenceDao {

    @Autowired
    private EmcReportInfoReferenceMapper emcReportInfoReferenceMapper;

    @Override
    public EmcReportInfoReferenceMapper getMapper() {
        return emcReportInfoReferenceMapper;
    }
}
