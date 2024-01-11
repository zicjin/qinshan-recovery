package com.sunwayworld.emc.module.bp.project.projscheme.dao;

import java.util.List;

import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjDTO;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.framework.support.base.dao.GenericDao;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-08
 */

public interface EmcProjOrderDao extends GenericDao<EmcProjOrderBean, Long> {

    List<EmcProjDTO> selectByProjId(Long projId);
}
