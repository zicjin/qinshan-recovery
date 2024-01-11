package com.sunwayworld.emc.module.mdm.basemdm.biology.dao.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.biology.bean.EmcBiologyBean;
import com.sunwayworld.emc.module.mdm.basemdm.biology.dao.EmcBiologyDao;
import com.sunwayworld.emc.module.mdm.basemdm.biology.mapper.EmcBiologyMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.PersistableHelper;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-28
 */
@Repository
public class EmcBiologyDaoImpl extends MybatisDaoSupport<EmcBiologyBean, Long> implements EmcBiologyDao {

    @Autowired
    private EmcBiologyMapper emcBiologyMapper;

    @Override
    public EmcBiologyMapper getMapper() {
        return emcBiologyMapper;
    }

    @Override
    public List<EmcBiologyBean> selectTreeNodeList(MapperParameter parameter) {
        return getMapper().selectTreeNodeList(parameter).stream().map(e -> PersistableHelper.mapToPersistable(e, EmcBiologyBean.class)).collect(Collectors.toList());
    }
}
