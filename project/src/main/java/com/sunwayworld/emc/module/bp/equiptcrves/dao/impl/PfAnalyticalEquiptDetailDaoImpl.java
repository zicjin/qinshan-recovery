package com.sunwayworld.emc.module.bp.equiptcrves.dao.impl;



import com.sunwayworld.emc.module.bp.equiptcrves.bean.PfAnalyticalEquiptDetailBean;
import com.sunwayworld.emc.module.bp.equiptcrves.dao.PfAnalyticalEquiptDetailDao;
import com.sunwayworld.emc.module.bp.equiptcrves.mapper.PfAnalyticalEquiptDetailMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
/**
 * @author licm@sunwayworld.com 2021年10月21日
 */
@Repository
@GikamBean
public class PfAnalyticalEquiptDetailDaoImpl extends MybatisDaoSupport<PfAnalyticalEquiptDetailBean, Long> implements PfAnalyticalEquiptDetailDao {

    @Autowired
    private PfAnalyticalEquiptDetailMapper pfAnalyticalEquiptDetailMapper;

    @Override
    public PfAnalyticalEquiptDetailMapper getMapper() {
        return pfAnalyticalEquiptDetailMapper;
    }

}
