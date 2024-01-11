package com.sunwayworld.emc.module.bp.info.infoindex.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcCategorySectionBean;
import com.sunwayworld.emc.module.bp.info.infoindex.dao.EmcCategorySectionDao;
import com.sunwayworld.emc.module.bp.info.infoindex.mapper.EmcCategorySectionMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-21
 */
@Repository
@GikamBean
public class EmcCategorySectionDaoImpl extends MybatisDaoSupport<EmcCategorySectionBean, Long> implements EmcCategorySectionDao {

    @Autowired
    private EmcCategorySectionMapper categorySectionMapper;

    @Override
    public EmcCategorySectionMapper getMapper() {
        return categorySectionMapper;
    }

}
