package com.sunwayworld.emc.module.item.precaution.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.precaution.bean.EmcPrecautionBean;
import com.sunwayworld.emc.module.item.precaution.dao.EmcPrecautionDao;
import com.sunwayworld.emc.module.item.precaution.mapper.EmcPrecautionMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-25
 */
@Repository
@GikamBean
public class EmcPrecautionDaoImpl extends MybatisDaoSupport<EmcPrecautionBean, Long> implements EmcPrecautionDao {

    @Autowired
    private EmcPrecautionMapper precautionMapper;

    @Override
    public EmcPrecautionMapper getMapper() {
        return precautionMapper;
    }

}
