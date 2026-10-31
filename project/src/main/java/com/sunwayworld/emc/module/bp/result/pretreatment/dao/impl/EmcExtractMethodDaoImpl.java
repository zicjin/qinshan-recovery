package com.sunwayworld.emc.module.bp.result.pretreatment.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.result.pretreatment.bean.EmcExtractMethodBean;
import com.sunwayworld.emc.module.bp.result.pretreatment.dao.EmcExtractMethodDao;
import com.sunwayworld.emc.module.bp.result.pretreatment.mapper.EmcExtractMethodMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2023年08月14日
 */
@Repository
@GikamBean
public class EmcExtractMethodDaoImpl extends MybatisDaoSupport<EmcExtractMethodBean, Long> implements EmcExtractMethodDao {

    @Autowired
    private EmcExtractMethodMapper extractMethodMapper;

    @Override
    public EmcExtractMethodMapper getMapper() {
        return extractMethodMapper;
    }

}
