package com.sunwayworld.emc.module.mdm.bizmdm.comparison.service;

import com.sunwayworld.emc.module.mdm.bizmdm.comparison.bean.EmcComparisonBean;
import com.sunwayworld.emc.module.mdm.bizmdm.comparison.bean.EmcComparisonResultBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
* @author zhengxy@sunwayworld.com 2020年08月17日
*/
public interface EmcComparisonService extends GenericService<EmcComparisonBean, Long> {

    Page<EmcComparisonResultBean> selectResultPagination(RestJsonWrapperBean wrapper);

    void insertResultByFolder(Long folderId);
}
