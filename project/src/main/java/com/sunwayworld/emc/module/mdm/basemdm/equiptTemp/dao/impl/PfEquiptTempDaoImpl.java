package com.sunwayworld.emc.module.mdm.basemdm.equiptTemp.dao.impl;


import com.sunwayworld.emc.module.mdm.basemdm.equiptTemp.bean.PfEquiptTempBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptTemp.dao.PfEquiptTempDao;
import com.sunwayworld.emc.module.mdm.basemdm.equiptTemp.mapper.PfEquiptTempMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
/**
 * @author licm@sunwayworld.com 2022年01月26日
 */
@Repository
@GikamBean
public class PfEquiptTempDaoImpl extends MybatisDaoSupport<PfEquiptTempBean, Long> implements PfEquiptTempDao {

    @Autowired
    private PfEquiptTempMapper pfEquiptTempMapper;

    @Override
    public PfEquiptTempMapper getMapper() {
        return pfEquiptTempMapper;
    }

}
