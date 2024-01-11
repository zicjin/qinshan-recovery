package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service;

import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-19
 */

public interface EmcMonitorTmplService extends GenericService<EmcMonitorTmplBean, String>, GenericActivatableService<EmcMonitorTmplBean, String>, GenericChoosableService<EmcMonitorTmplBean, String>,
        GenericSelectableService<EmcMonitorTmplBean, String> {
    void copy(RestJsonWrapperBean jsonWrapper);

    Page<EmcMonitorSchemeBean> selectSchemeChoosablePagination(RestJsonWrapperBean wrapper);

    Page<EmcMonitorSchemeBean> selectSchemeDetectionChoosablePagination(RestJsonWrapperBean wrapper);

    Page<EmcMonitorSchemeBean> selectSchemeItemGroupChoosablePagination(RestJsonWrapperBean wrapper);
    
    Page<EmcMonitorSchemeBean> selectDetectionChoosablePaginationDeWeight(RestJsonWrapperBean wrapper);
}
