package com.sunwayworld.emc.module.bp.bp.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskLogBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author magh@sunwayworld.com
 * @date 2019-11-22
 */

public interface EmcOrderTaskLogService extends GenericService<EmcOrderTaskLogBean, Long> {

    Long insert(List<EmcOrderTaskBean> allOrderTaskList, String operateCategory, String model);
}
