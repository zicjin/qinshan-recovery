package com.sunwayworld.emc.module.mdm.invoices.dao.impl;

import com.sunwayworld.emc.module.mdm.invoices.bean.EmcReturnedBean;
import com.sunwayworld.emc.module.mdm.invoices.dao.EmcReturnedDao;
import com.sunwayworld.emc.module.mdm.invoices.mapper.EmcReturnedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcReturnedDaoImpl extends MybatisDaoSupport<EmcReturnedBean, Long> implements EmcReturnedDao {

    @Autowired
    private EmcReturnedMapper emcReturnedMapper;

    @Override
    public EmcReturnedMapper getMapper() {
        return emcReturnedMapper;
    }

}
