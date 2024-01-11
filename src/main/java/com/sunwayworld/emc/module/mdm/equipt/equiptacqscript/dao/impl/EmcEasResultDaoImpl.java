package com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasResultBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.dao.EmcEasResultDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.mapper.EmcEasResultMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEasResultDaoImpl extends MybatisDaoSupport<EmcEasResultBean, Long> implements EmcEasResultDao {

    @Autowired
    private EmcEasResultMapper EmcEasResultMapper;

    @Override
    public EmcEasResultMapper getMapper() {
        return EmcEasResultMapper;
    }

}
