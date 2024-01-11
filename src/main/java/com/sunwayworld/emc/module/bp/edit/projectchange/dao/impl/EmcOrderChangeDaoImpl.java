package com.sunwayworld.emc.module.bp.edit.projectchange.dao.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.dao.EmcOrderChangeDao;
import com.sunwayworld.emc.module.bp.edit.projectchange.mapper.EmcOrderChangeMapper;
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
public class EmcOrderChangeDaoImpl extends MybatisDaoSupport<EmcOrderChangeBean, Long>implements EmcOrderChangeDao {

    @Autowired
    private EmcOrderChangeMapper emcOrderMapper;

    @Override
    public EmcOrderChangeMapper getMapper() {
        return emcOrderMapper;
    }

    @Override
    public List<EmcOrderChangeBean> selectOrderList(MapperParameter parameter) {
        return getMapper().selectOrderList(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcOrderChangeBean.class)).collect(Collectors.toList());
    }
}
