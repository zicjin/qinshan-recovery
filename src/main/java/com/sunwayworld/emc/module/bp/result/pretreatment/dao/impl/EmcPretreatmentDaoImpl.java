package com.sunwayworld.emc.module.bp.result.pretreatment.dao.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.result.pretreatment.bean.EmcPretreatmentBean;
import com.sunwayworld.emc.module.bp.result.pretreatment.dao.EmcPretreatmentDao;
import com.sunwayworld.emc.module.bp.result.pretreatment.mapper.EmcPretreatmentMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2023年08月14日
 */
@Repository
@GikamBean
public class EmcPretreatmentDaoImpl extends MybatisDaoSupport<EmcPretreatmentBean, Long> implements EmcPretreatmentDao {

    @Autowired
    private EmcPretreatmentMapper pretreatmentMapper;

    @Override
    public EmcPretreatmentMapper getMapper() {
        return pretreatmentMapper;
    }

}
