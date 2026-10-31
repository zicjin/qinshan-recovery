package com.sunwayworld.emc.module.bp.info.infotmpl.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcInfoTmplPointBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.dao.EmcInfoTmplPointDao;
import com.sunwayworld.emc.module.bp.info.infotmpl.mapper.EmcInfoTmplPointMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-19
 */
@Repository
@GikamBean
public class EmcInfoTmplPointDaoImpl extends MybatisDaoSupport<EmcInfoTmplPointBean, Long> implements EmcInfoTmplPointDao {

    @Autowired
    private EmcInfoTmplPointMapper infoTmplPointMapper;

    @Override
    public EmcInfoTmplPointMapper getMapper() {
        return infoTmplPointMapper;
    }

}
