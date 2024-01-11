package com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.service;

import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.bean.EmcMonitorTmplGroupBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.bean.EmcOrderDescBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

/**
 * @author magh@sunwayworld.com 2019年9月6日
 */

public interface EmcMonitorTmplGroupService extends GenericService<EmcMonitorTmplGroupBean, Long>,
                                                    GenericSelectableService<EmcMonitorTmplGroupBean, Long>,
                                                    GenericActivatableService<EmcMonitorTmplGroupBean, Long>,
                                                    GenericChoosableService<EmcMonitorTmplGroupBean, Long> {
    Page<EmcOrderDescBean> selectOrderDescPaginationByMonitorTmplGroupId(Long id, RestJsonWrapperBean wrapper);

    public Long insertOrderDesc(Long id, RestJsonWrapperBean wrapper);

    public void deleteOrderDesc(Long id, RestJsonWrapperBean wrapper);

    void activateOrderDesc(RestJsonWrapperBean wrapper);

    void deactivateOrderDesc(RestJsonWrapperBean wrapper);
}
