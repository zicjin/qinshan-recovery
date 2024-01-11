package com.sunwayworld.emc.module.item.managereview.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.managereview.bean.EmcManageReviewBean;
import com.sunwayworld.emc.module.item.managereview.dao.EmcManageReviewDao;
import com.sunwayworld.emc.module.item.managereview.mapper.EmcManageReviewMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
public class EmcManageReviewDaoImpl extends MybatisDaoSupport<EmcManageReviewBean, Long>implements EmcManageReviewDao {

    @Autowired
    private EmcManageReviewMapper emcManageReviewMapper;

    @Override
    public EmcManageReviewMapper getMapper() {
        return emcManageReviewMapper;
    }

}
