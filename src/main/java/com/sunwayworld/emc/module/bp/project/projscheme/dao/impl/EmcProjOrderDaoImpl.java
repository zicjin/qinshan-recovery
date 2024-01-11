package com.sunwayworld.emc.module.bp.project.projscheme.dao.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjDTO;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.emc.module.bp.project.projscheme.dao.EmcProjOrderDao;
import com.sunwayworld.emc.module.bp.project.projscheme.mapper.EmcProjOrderMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-08
 */
@Repository
@GikamBean
public class EmcProjOrderDaoImpl extends MybatisDaoSupport<EmcProjOrderBean, Long> implements EmcProjOrderDao {

    @Autowired
    private EmcProjOrderMapper emcProjOrderMapper;

    @Override
    public EmcProjOrderMapper getMapper() {
        return emcProjOrderMapper;
    }

    @Override
    public List<EmcProjDTO> selectByProjId(Long projId) {
        return getMapper().selectByProjId(projId).stream().map(p -> PersistableHelper.mapToPersistable(p, EmcProjDTO.class)).collect(Collectors.toList());
    }

}
