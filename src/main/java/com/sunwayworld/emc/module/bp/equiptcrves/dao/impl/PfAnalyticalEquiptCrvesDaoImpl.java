package com.sunwayworld.emc.module.bp.equiptcrves.dao.impl;


import com.sunwayworld.emc.module.bp.equiptcrves.bean.PfAnalyticalEquiptCrvesBean;
import com.sunwayworld.emc.module.bp.equiptcrves.dao.PfAnalyticalEquiptCrvesDao;
import com.sunwayworld.emc.module.bp.equiptcrves.mapper.PfAnalyticalEquiptCrvesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
/**
 * @author licm@sunwayworld.com 2021年10月21日
 */
@Repository
@GikamBean
public class PfAnalyticalEquiptCrvesDaoImpl extends MybatisDaoSupport<PfAnalyticalEquiptCrvesBean, Long> implements PfAnalyticalEquiptCrvesDao {

    @Autowired
    private PfAnalyticalEquiptCrvesMapper pfAnalyticalEquiptCrvesMapper;

    @Override
    public PfAnalyticalEquiptCrvesMapper getMapper() {
        return pfAnalyticalEquiptCrvesMapper;
    }

}
