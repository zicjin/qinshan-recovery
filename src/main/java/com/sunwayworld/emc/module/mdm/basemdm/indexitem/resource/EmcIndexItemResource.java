package com.sunwayworld.emc.module.mdm.basemdm.indexitem.resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sunwayworld.emc.module.mdm.basemdm.indexitem.bean.EmcIndexItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.bean.EmcIndexTestItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.service.EmcIndexItemService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-17
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/index-items")
public interface EmcIndexItemResource extends GenericResource<EmcIndexItemService, EmcIndexItemBean, Long>, GenericChoosableResource<EmcIndexItemService, EmcIndexItemBean, Long> {
    @RequestMapping(value = "/{id}/test-items/queries", method = RequestMethod.POST)
    Page<EmcIndexTestItemBean> selectIndexTestItemPaginationByIndexItemId(@PathVariable Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/test-items/limit/queries", method = RequestMethod.POST)
    Page<EmcIndexTestItemBean> selectIndexTestItemLimit(RestJsonWrapperBean wrapper);
    
    @RequestMapping(value = "/{id}/test-items", method = RequestMethod.POST)
    public Long insertIndexTestItem(@PathVariable Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{id}/test-items", method = RequestMethod.DELETE)
    public void deleteIndexTestItem(@PathVariable Long id, RestJsonWrapperBean wrapper);
}
