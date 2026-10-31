package com.sunwayworld.emc.module.bp.bp.dao.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.dao.EmcOrderDao;
import com.sunwayworld.emc.module.bp.bp.mapper.EmcOrderMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */
@Repository
@GikamBean
public class EmcOrderDaoImpl extends MybatisDaoSupport<EmcOrderBean, Long>implements EmcOrderDao {

    @Autowired
    private EmcOrderMapper emcOrderMapper;

    @Override
    public EmcOrderMapper getMapper() {
        return emcOrderMapper;
    }

    @Override
    public List<EmcOrderBean> selectOrderList(MapperParameter parameter) {
        return getMapper().selectOrderList(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcOrderBean.class)).collect(Collectors.toList());
    }
}
