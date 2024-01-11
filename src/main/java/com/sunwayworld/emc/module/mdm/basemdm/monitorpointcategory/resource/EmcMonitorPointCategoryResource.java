package com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.resource;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.bean.EmcMonitorPointCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.bean.EmcMpcTmplBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.service.EmcMonitorPointCategoryService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-10
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/monitor-point-categories")
public interface EmcMonitorPointCategoryResource
        extends GenericResource<EmcMonitorPointCategoryService, EmcMonitorPointCategoryBean, String>, GenericChoosableResource<EmcMonitorPointCategoryService, EmcMonitorPointCategoryBean, String> {

    @RequestMapping(value = "/{id}/tmpls/queries", method = RequestMethod.POST)
    Page<EmcMpcTmplBean> selectTmplPaginationByMpcId(@PathVariable String id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{id}/tmpls", method = RequestMethod.POST)
    public Long insertTmpl(@PathVariable String id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{id}/tmpls", method = RequestMethod.DELETE)
    public void deleteTmpl(@PathVariable String id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/filter/queries/selectable", method = RequestMethod.GET)
    List<EmcMonitorPointCategoryBean> selectFilterMonitorTmplGroupSelectableList();

    @RequestMapping(value = "/tmpls/queries/selectable", method = RequestMethod.GET)
    List<EmcMpcTmplBean> selectTmplSelectableList();
}
