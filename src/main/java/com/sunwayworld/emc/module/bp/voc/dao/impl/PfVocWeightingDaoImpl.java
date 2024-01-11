package com.sunwayworld.emc.module.bp.voc.dao.impl;


import com.sunwayworld.emc.module.bp.voc.bean.PfVocWeightingBean;
import com.sunwayworld.emc.module.bp.voc.dao.PfVocWeightingDao;
import com.sunwayworld.emc.module.bp.voc.mapper.PfVocWeightingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
/**
 * @author licm@sunwayworld.com 2021年09月24日
 */
@Repository
@GikamBean
public class PfVocWeightingDaoImpl extends MybatisDaoSupport<PfVocWeightingBean, Long> implements PfVocWeightingDao {

    @Autowired
    private PfVocWeightingMapper pfVocWeightingMapper;

    @Override
    public PfVocWeightingMapper getMapper() {
        return pfVocWeightingMapper;
    }

}
