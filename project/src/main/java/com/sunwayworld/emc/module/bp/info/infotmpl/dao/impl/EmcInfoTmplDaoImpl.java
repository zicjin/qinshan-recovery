package com.sunwayworld.emc.module.bp.info.infotmpl.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcInfoTmplBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.dao.EmcInfoTmplDao;
import com.sunwayworld.emc.module.bp.info.infotmpl.mapper.EmcInfoTmplMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-19
 */
@Repository
@GikamBean
public class EmcInfoTmplDaoImpl extends MybatisDaoSupport<EmcInfoTmplBean, Long> implements EmcInfoTmplDao {

    @Autowired
    private EmcInfoTmplMapper infoTmplMapper;

    @Override
    public EmcInfoTmplMapper getMapper() {
        return infoTmplMapper;
    }

}
