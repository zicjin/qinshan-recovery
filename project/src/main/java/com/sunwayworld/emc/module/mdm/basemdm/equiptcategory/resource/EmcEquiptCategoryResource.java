package com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.service.EmcEquiptCategoryService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;

/**
 * @author magh@sunwayworld.com 2019年9月4日
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/equipt-categories")
public interface EmcEquiptCategoryResource
        extends GenericResource<EmcEquiptCategoryService, EmcEquiptCategoryBean, Long>, GenericSelectableResource<EmcEquiptCategoryService, EmcEquiptCategoryBean, Long> {

    @RequestMapping(value = "/{id}/maints/queries", method = RequestMethod.POST)
    Page<EmcEquiptMaintBean> selectEquiptMaintPaginationByEquiptCategoryId(@PathVariable Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{id}/maints", method = RequestMethod.POST)
    public Long insertEquiptMaint(@PathVariable Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{id}/maints", method = RequestMethod.DELETE)
    public void deleteEquiptMaint(@PathVariable Long id, RestJsonWrapperBean wrapper);

}
