package com.sunwayworld.emc.module.bp.info.infoconfig.dao.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.info.infoconfig.bean.EmcInfoConfigBean;
import com.sunwayworld.emc.module.bp.info.infoconfig.dao.EmcInfoConfigDao;
import com.sunwayworld.emc.module.bp.info.infoconfig.mapper.EmcInfoConfigMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.PersistableHelper;

@Repository
public class EmcInfoConfigDaoImpl extends MybatisDaoSupport<EmcInfoConfigBean, Long>implements EmcInfoConfigDao {

    @Autowired
    private EmcInfoConfigMapper emcInfoConfigMapper;

    @Override
    public EmcInfoConfigMapper getMapper() {
        return emcInfoConfigMapper;
    }

    @Override
    public List<EmcInfoConfigBean> selectTreeNodeList(MapperParameter parameter) {
        return getMapper().selectTreeNodeList(parameter).stream().map(e -> PersistableHelper.mapToPersistable(e, EmcInfoConfigBean.class)).collect(Collectors.toList());
    }

}
