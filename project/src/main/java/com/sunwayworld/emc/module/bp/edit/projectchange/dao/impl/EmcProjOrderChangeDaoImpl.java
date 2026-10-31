package com.sunwayworld.emc.module.bp.edit.projectchange.dao.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjOrderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.dao.EmcProjOrderChangeDao;
import com.sunwayworld.emc.module.bp.edit.projectchange.mapper.EmcProjOrderChangeMapper;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjDTO;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-08
 */
@Repository
@GikamBean
public class EmcProjOrderChangeDaoImpl extends MybatisDaoSupport<EmcProjOrderChangeBean, Long> implements EmcProjOrderChangeDao {

    @Autowired
    private EmcProjOrderChangeMapper emcProjOrderMapper;

    @Override
    public EmcProjOrderChangeMapper getMapper() {
        return emcProjOrderMapper;
    }

    @Override
    public List<EmcProjDTO> selectByProjId(Long projId) {
        return getMapper().selectByProjId(projId).stream().map(p -> PersistableHelper.mapToPersistable(p, EmcProjDTO.class)).collect(Collectors.toList());
    }

}
