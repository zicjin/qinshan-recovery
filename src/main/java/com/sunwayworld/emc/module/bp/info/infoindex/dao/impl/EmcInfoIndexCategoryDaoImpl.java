package com.sunwayworld.emc.module.bp.info.infoindex.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcInfoIndexCategoryBean;
import com.sunwayworld.emc.module.bp.info.infoindex.dao.EmcInfoIndexCategoryDao;
import com.sunwayworld.emc.module.bp.info.infoindex.mapper.EmcInfoIndexCategoryMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-21
 */
@Repository
@GikamBean
public class EmcInfoIndexCategoryDaoImpl extends MybatisDaoSupport<EmcInfoIndexCategoryBean, Long> implements EmcInfoIndexCategoryDao {

    @Autowired
    private EmcInfoIndexCategoryMapper infoIndexCategoryMapper;

    @Override
    public EmcInfoIndexCategoryMapper getMapper() {
        return infoIndexCategoryMapper;
    }

}
