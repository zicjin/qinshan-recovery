package com.sunwayworld.emc.module.mdm.bizmdm.comparison.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.bizmdm.comparison.bean.EmcComparisonBean;
import com.sunwayworld.emc.module.mdm.bizmdm.comparison.bean.EmcComparisonResultBean;
import com.sunwayworld.emc.module.mdm.bizmdm.comparison.service.EmcComparisonService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
* @author zhengxy@sunwayworld.com 2020年08月17日
*/
@RequestMapping("/secure/emc/module/mdm/bizmdm/comparisons")
public interface EmcComparisonResource extends GenericResource<EmcComparisonService, EmcComparisonBean, Long> {

    Page<EmcComparisonResultBean> selectResultPagination(RestJsonWrapperBean wrapper);

    void insertResultByFolder(Long folderId);
}
