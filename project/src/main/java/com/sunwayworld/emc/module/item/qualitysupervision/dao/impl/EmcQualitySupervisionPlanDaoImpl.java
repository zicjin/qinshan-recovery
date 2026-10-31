package com.sunwayworld.emc.module.item.qualitysupervision.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.qualitysupervision.bean.EmcQualitySupervisionPlanBean;
import com.sunwayworld.emc.module.item.qualitysupervision.dao.EmcQualitySupervisionPlanDao;
import com.sunwayworld.emc.module.item.qualitysupervision.mapper.EmcQualitySupervisionPlanMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author zhangft@sunwayworld.com
 * @date 2019-12-10
 */
@Repository
public class EmcQualitySupervisionPlanDaoImpl extends MybatisDaoSupport<EmcQualitySupervisionPlanBean, Long>implements EmcQualitySupervisionPlanDao {
    
    @Autowired
    private EmcQualitySupervisionPlanMapper qualitySupervisionPlanMapper;
    
    @Override
    public EmcQualitySupervisionPlanMapper getMapper() {
        return qualitySupervisionPlanMapper;
    }
    
}
