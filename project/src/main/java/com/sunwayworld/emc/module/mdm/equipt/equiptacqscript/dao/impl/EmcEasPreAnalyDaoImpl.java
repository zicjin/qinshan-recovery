package com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasPreAnalyBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.dao.EmcEasPreAnalyDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.mapper.EmcEasPreAnalyMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEasPreAnalyDaoImpl extends MybatisDaoSupport<EmcEasPreAnalyBean, Long> implements EmcEasPreAnalyDao {

    @Autowired
    private EmcEasPreAnalyMapper EmcEasPreAnalyMapper;

    @Override
    public EmcEasPreAnalyMapper getMapper() {
        return EmcEasPreAnalyMapper;
    }

}
