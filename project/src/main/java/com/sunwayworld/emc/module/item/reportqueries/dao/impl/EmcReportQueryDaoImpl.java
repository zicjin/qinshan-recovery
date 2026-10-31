package com.sunwayworld.emc.module.item.reportqueries.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.reportqueries.bean.EmcReportQueryBean;
import com.sunwayworld.emc.module.item.reportqueries.dao.EmcReportQueryDao;
import com.sunwayworld.emc.module.item.reportqueries.mapper.EmcReportQueryMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-07
 */
@Repository
@GikamBean
public class EmcReportQueryDaoImpl extends MybatisDaoSupport<EmcReportQueryBean, String> implements EmcReportQueryDao {

    @Autowired
    private EmcReportQueryMapper reportQueryMapper;

    @Override
    public EmcReportQueryMapper getMapper() {
        return reportQueryMapper;
    }

}
