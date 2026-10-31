package com.sunwayworld.emc.module.mdm.contract.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.contract.bean.EmcReviewBasicInfoBean;
import com.sunwayworld.emc.module.mdm.contract.dao.EmcReviewBasicInfoDao;
import com.sunwayworld.emc.module.mdm.contract.mapper.EmcReviewBasicInfoMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2020年11月01日
*/
@Repository
public class EmcReviewBasicInfoDaoImpl extends MybatisDaoSupport<EmcReviewBasicInfoBean, Long> implements EmcReviewBasicInfoDao {

    @Autowired
    private EmcReviewBasicInfoMapper emcReviewBasicInfoMapper;

    @Override
    public EmcReviewBasicInfoMapper getMapper() {
        return emcReviewBasicInfoMapper;
    }
}
