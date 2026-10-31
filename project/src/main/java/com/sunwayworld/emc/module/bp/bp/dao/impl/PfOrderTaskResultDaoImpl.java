package com.sunwayworld.emc.module.bp.bp.dao.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.dao.PfOrderTaskResultDao;
import com.sunwayworld.emc.module.bp.bp.mapper.PfOrderTaskResultMapper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;

/**
 * @author zhangjian0705@sunwayworld.com
 * @date 2021/7/15 14:12
 */
@Repository
@GikamBean
public class PfOrderTaskResultDaoImpl extends EmcOrderTaskResultDaoImpl implements PfOrderTaskResultDao {

    @Autowired
    private PfOrderTaskResultMapper emcOrderTaskResultMapper;

    @Override
    public List<EmcOrderTaskResultBean> selectResultInputResultList(MapperParameter parameter) {
        return emcOrderTaskResultMapper.selectResultInputResultList(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcOrderTaskResultBean.class)).collect(Collectors.toList());
    }

    @Override
    public List<EmcOrderTaskResultBean> selectQuickInputResultList(MapperParameter parameter) {
        return emcOrderTaskResultMapper.selectQuickInputResultList(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcOrderTaskResultBean.class)).collect(Collectors.toList());
    }

    @Override
    public List<EmcOrderTaskResultBean> selectResultRemind(MapperParameter parameter) {
        return emcOrderTaskResultMapper.selectResultRemind(parameter).stream().map(e -> PersistableHelper.mapToPersistable(e, EmcOrderTaskResultBean.class)).collect(Collectors.toList());
    }

    @Override
    public List<EmcOrderTaskResultBean> selectQuickInputResultListBySampleParam(MapperParameter parameter) {
        return emcOrderTaskResultMapper.selectQuickInputResultListBySampleParam(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcOrderTaskResultBean.class)).collect(Collectors.toList());
    }

    @Override
    public List<EmcOrderTaskResultBean> selectQuickInputResultListBySampleId(MapperParameter parameter) {
        return emcOrderTaskResultMapper.selectQuickInputResultListBySampleId(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcOrderTaskResultBean.class)).collect(Collectors.toList());
    }

}
