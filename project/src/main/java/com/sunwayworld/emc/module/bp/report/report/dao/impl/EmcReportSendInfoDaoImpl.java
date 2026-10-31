package com.sunwayworld.emc.module.bp.report.report.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportSendInfoBean;
import com.sunwayworld.emc.module.bp.report.report.dao.EmcReportSendInfoDao;
import com.sunwayworld.emc.module.bp.report.report.mapper.EmcReportSendInfoMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2022年01月14日
*/
@Repository
public class EmcReportSendInfoDaoImpl extends MybatisDaoSupport<EmcReportSendInfoBean, Long> implements EmcReportSendInfoDao {

    @Autowired
    private EmcReportSendInfoMapper reportSendInfoMapper;

    @Override
    public EmcReportSendInfoMapper getMapper() {
        return reportSendInfoMapper;
    }
}
