package com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.bean.EmcMonitorTmplGroupBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.bean.EmcOrderDescBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.service.EmcMonitorTmplGroupService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;

/**
 * @author magh@sunwayworld.com 2019年9月6日
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/monitor-tmpl-groups")
public interface EmcMonitorTmplGroupResource extends GenericResource<EmcMonitorTmplGroupService, EmcMonitorTmplGroupBean, Long>,
                                                     GenericActivatableResource<EmcMonitorTmplGroupService, EmcMonitorTmplGroupBean, Long>,
                                                     GenericSelectableResource<EmcMonitorTmplGroupService, EmcMonitorTmplGroupBean, Long>,
                                                     GenericChoosableResource<EmcMonitorTmplGroupService, EmcMonitorTmplGroupBean, Long> {
    @RequestMapping(value = "/{id}/order-descs/queries", method = RequestMethod.POST)
    Page<EmcOrderDescBean> selectOrderDescPaginationByMonitorTmplGroupId(@PathVariable Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{id}/order-descs", method = RequestMethod.POST)
    public Long insertOrderDesc(@PathVariable Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{id}/order-descs", method = RequestMethod.DELETE)
    public void deleteOrderDesc(@PathVariable Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/order-descs/action/activate", method = RequestMethod.PUT)
    public void activateOrderDesc(RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/order-descs/action/deactivate", method = RequestMethod.PUT)
    public void deactivateOrderDesc(RestJsonWrapperBean wrapper);

}
