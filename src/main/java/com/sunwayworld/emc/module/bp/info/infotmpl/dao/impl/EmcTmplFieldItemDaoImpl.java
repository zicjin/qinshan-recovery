package com.sunwayworld.emc.module.bp.info.infotmpl.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcTmplFieldItemBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.dao.EmcTmplFieldItemDao;
import com.sunwayworld.emc.module.bp.info.infotmpl.mapper.EmcTmplFieldItemMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-19
 */
@Repository
@GikamBean
public class EmcTmplFieldItemDaoImpl extends MybatisDaoSupport<EmcTmplFieldItemBean, Long> implements EmcTmplFieldItemDao {

    @Autowired
    private EmcTmplFieldItemMapper tmplFieldItemMapper;

    @Override
    public EmcTmplFieldItemMapper getMapper() {
        return tmplFieldItemMapper;
    }

}
