package com.sunwayworld.emc.module.bp.info.infoindex.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcInfoIndexBean;
import com.sunwayworld.emc.module.bp.info.infoindex.dao.EmcInfoIndexDao;
import com.sunwayworld.emc.module.bp.info.infoindex.mapper.EmcInfoIndexMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-21
 */
@Repository
@GikamBean
public class EmcInfoIndexDaoImpl extends MybatisDaoSupport<EmcInfoIndexBean, Long> implements EmcInfoIndexDao {

    @Autowired
    private EmcInfoIndexMapper infoIndexMapper;

    @Override
    public EmcInfoIndexMapper getMapper() {
        return infoIndexMapper;
    }

}
