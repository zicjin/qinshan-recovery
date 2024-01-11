package com.sunwayworld.emc.module.bp.result.pretreatment.dao.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.result.pretreatment.bean.EmcPurifyMethodBean;
import com.sunwayworld.emc.module.bp.result.pretreatment.dao.EmcPurifyMethodDao;
import com.sunwayworld.emc.module.bp.result.pretreatment.mapper.EmcPurifyMethodMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2023年08月14日
 */
@Repository
@GikamBean
public class EmcPurifyMethodDaoImpl extends MybatisDaoSupport<EmcPurifyMethodBean, Long> implements EmcPurifyMethodDao {

    @Autowired
    private EmcPurifyMethodMapper EmcPurifyMethodMapper;

    @Override
    public EmcPurifyMethodMapper getMapper() {
        return EmcPurifyMethodMapper;
    }

}
