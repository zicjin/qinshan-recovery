package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service;

import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-19
 */

public interface EmcMonitorSchemeService extends GenericService<EmcMonitorSchemeBean, Long>, GenericSelectableService<EmcMonitorSchemeBean, Long>, GenericChoosableService<EmcMonitorSchemeBean, Long> {

    Long insert(String tmplId, RestJsonWrapperBean wrapper);

    void edit(String id, String schemeName, RestJsonWrapperBean wrapper);

    void addSchemeTest(String id, String schemeName, RestJsonWrapperBean wrapper);

    void deleteSchemeTest(String id, String schemeName, RestJsonWrapperBean wrapper);

    Page<EmcMonitorSchemeBean> selectDetectionChoosablePagination(RestJsonWrapperBean wrapper);
    
    Page<EmcMonitorSchemeBean> selectDetectionChoosablePaginationDeWeight(RestJsonWrapperBean wrapper);

    Page<EmcMonitorSchemeBean> selectPaginationGroupByTestMethod(RestJsonWrapperBean wrapper);
}
