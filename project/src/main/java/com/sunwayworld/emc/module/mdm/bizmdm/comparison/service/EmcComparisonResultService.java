package com.sunwayworld.emc.module.mdm.bizmdm.comparison.service;

import com.sunwayworld.emc.module.mdm.bizmdm.comparison.bean.EmcComparisonResultBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
* @author zhengxy@sunwayworld.com 2020年10月22日
*/
public interface EmcComparisonResultService extends GenericService<EmcComparisonResultBean, Long> {

    void insertResultByFolder(Long folderId);
}
