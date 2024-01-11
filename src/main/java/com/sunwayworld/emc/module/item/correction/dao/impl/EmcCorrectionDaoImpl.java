package com.sunwayworld.emc.module.item.correction.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.correction.bean.EmcCorrectionBean;
import com.sunwayworld.emc.module.item.correction.dao.EmcCorrectionDao;
import com.sunwayworld.emc.module.item.correction.mapper.EmcCorrectionMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-25
 */
@Repository
@GikamBean
public class EmcCorrectionDaoImpl extends MybatisDaoSupport<EmcCorrectionBean, Long> implements EmcCorrectionDao {

    @Autowired
    private EmcCorrectionMapper correctionMapper;

    @Override
    public EmcCorrectionMapper getMapper() {
        return correctionMapper;
    }

}
