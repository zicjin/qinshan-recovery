package com.sunwayworld.emc.module.bp.info.infoindex.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcCategoryIndexBean;
import com.sunwayworld.emc.module.bp.info.infoindex.dao.EmcCategoryIndexDao;
import com.sunwayworld.emc.module.bp.info.infoindex.mapper.EmcCategoryIndexMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-21
 */
@Repository
@GikamBean
public class EmcCategoryIndexDaoImpl extends MybatisDaoSupport<EmcCategoryIndexBean, Long> implements EmcCategoryIndexDao {

    @Autowired
    private EmcCategoryIndexMapper categoryIndexMapper;

    @Override
    public EmcCategoryIndexMapper getMapper() {
        return categoryIndexMapper;
    }

}
