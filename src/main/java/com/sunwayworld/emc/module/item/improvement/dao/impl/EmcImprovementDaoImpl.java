package com.sunwayworld.emc.module.item.improvement.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.improvement.bean.EmcImprovementBean;
import com.sunwayworld.emc.module.item.improvement.dao.EmcImprovementDao;
import com.sunwayworld.emc.module.item.improvement.mapper.EmcImprovementMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author guok@sunwayworld.com
 * @date 2019-12-09
 */

@Repository
@GikamBean
public class EmcImprovementDaoImpl extends MybatisDaoSupport<EmcImprovementBean, Long> implements EmcImprovementDao {
    @Autowired
    private EmcImprovementMapper improvementMapper;

    @Override
    public EmcImprovementMapper getMapper() {
        return improvementMapper;
    }
}
