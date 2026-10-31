package com.sunwayworld.emc.module.item.qualitysupervision.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.qualitysupervision.bean.EmcQualitySupervisionContBean;
import com.sunwayworld.emc.module.item.qualitysupervision.dao.EmcQualitySupervisionContDao;
import com.sunwayworld.emc.module.item.qualitysupervision.mapper.EmcQualitySupervisionContMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;


/**
 * @author zhangft@sunwayworld.com
 * @date 2019-12-10
 */
@Repository
@GikamBean
public class EmcQualitySupervisionContDaoImpl extends MybatisDaoSupport<EmcQualitySupervisionContBean, Long> implements EmcQualitySupervisionContDao {
    
    @Autowired
    private EmcQualitySupervisionContMapper qualitySupervisionContMapper;
    
    @Override
    public EmcQualitySupervisionContMapper getMapper() {
        return qualitySupervisionContMapper;
    }
    
}
