package com.sunwayworld.emc.module.item.qualitysupervision.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.qualitysupervision.bean.EmcQualitySupervisionBean;
import com.sunwayworld.emc.module.item.qualitysupervision.dao.EmcQualitySupervisionDao;
import com.sunwayworld.emc.module.item.qualitysupervision.mapper.EmcQualitySupervisionMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;


/**
 * @author zhangft@sunwayworld.com
 * @date 2019-12-10
 */
@Repository
@GikamBean
public class EmcQualitySupervisionDaoImpl extends MybatisDaoSupport<EmcQualitySupervisionBean, Long> implements EmcQualitySupervisionDao {
    
    @Autowired
    private EmcQualitySupervisionMapper qualitySupervisionMapper;
    
    @Override
    public EmcQualitySupervisionMapper getMapper() {
        return qualitySupervisionMapper;
    }
    
}
