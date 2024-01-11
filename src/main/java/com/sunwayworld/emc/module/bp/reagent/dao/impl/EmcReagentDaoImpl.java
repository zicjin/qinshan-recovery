package com.sunwayworld.emc.module.bp.reagent.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.reagent.bean.EmcReagentBean;
import com.sunwayworld.emc.module.bp.reagent.dao.EmcReagentDao;
import com.sunwayworld.emc.module.bp.reagent.mapper.EmcReagentMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
public class EmcReagentDaoImpl extends MybatisDaoSupport<EmcReagentBean, Long>implements EmcReagentDao {

    @Autowired
    private EmcReagentMapper reagentMapper;

    @Override
    public EmcReagentMapper getMapper() {
        return reagentMapper;
    }

}
