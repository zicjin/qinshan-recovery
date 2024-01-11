package com.sunwayworld.emc.module.bp.info.infotmpl.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcInfoTmplFieldBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.dao.EmcInfoTmplFieldDao;
import com.sunwayworld.emc.module.bp.info.infotmpl.mapper.EmcInfoTmplFieldMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-19
 */
@Repository
@GikamBean
public class EmcInfoTmplFieldDaoImpl extends MybatisDaoSupport<EmcInfoTmplFieldBean, Long> implements EmcInfoTmplFieldDao {

    @Autowired
    private EmcInfoTmplFieldMapper infoTmplFieldMapper;

    @Override
    public EmcInfoTmplFieldMapper getMapper() {
        return infoTmplFieldMapper;
    }

}
