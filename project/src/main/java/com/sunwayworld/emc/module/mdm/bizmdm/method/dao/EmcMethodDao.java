package com.sunwayworld.emc.module.mdm.bizmdm.method.dao;

import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodBean;
import com.sunwayworld.framework.support.base.dao.GenericDao;

/**
 * @author yuanhh@sunwayworld.com 2019年9月10日
 */

public interface EmcMethodDao extends GenericDao<EmcMethodBean, Long> {

    Long selectReportConfigLineIdById(Long id);
}
