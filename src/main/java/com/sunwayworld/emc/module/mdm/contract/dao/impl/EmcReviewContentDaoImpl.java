package com.sunwayworld.emc.module.mdm.contract.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.contract.bean.EmcReviewContentBean;
import com.sunwayworld.emc.module.mdm.contract.dao.EmcReviewContentDao;
import com.sunwayworld.emc.module.mdm.contract.mapper.EmcReviewContentMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcReviewContentDaoImpl extends MybatisDaoSupport<EmcReviewContentBean, Long> implements EmcReviewContentDao {

    @Autowired
    private EmcReviewContentMapper emcReviewContentMapper;

    @Override
    public EmcReviewContentMapper getMapper() {
        return emcReviewContentMapper;
    }

}
