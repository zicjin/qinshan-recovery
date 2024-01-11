package com.sunwayworld.emc.module.bp.edit.projectchange.dao;

import java.util.List;

import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjOrderChangeBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjDTO;
import com.sunwayworld.framework.support.base.dao.GenericDao;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-08
 */

public interface EmcProjOrderChangeDao extends GenericDao<EmcProjOrderChangeBean, Long> {

    List<EmcProjDTO> selectByProjId(Long projId);
}
