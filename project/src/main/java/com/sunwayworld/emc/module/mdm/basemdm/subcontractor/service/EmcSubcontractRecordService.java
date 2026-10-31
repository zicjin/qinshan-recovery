package com.sunwayworld.emc.module.mdm.basemdm.subcontractor.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.bean.EmcSubcontractRecordBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author magh@sunwayworld.com
 * @date 2019-11-05
 */

public interface EmcSubcontractRecordService extends GenericService<EmcSubcontractRecordBean, Long> {
    public void insert(List<EmcOrderTaskBean> orderTaskList);
}
