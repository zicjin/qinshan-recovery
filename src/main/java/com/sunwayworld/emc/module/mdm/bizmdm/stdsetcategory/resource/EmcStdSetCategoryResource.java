package com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.resource;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.bean.EmcFieldSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.bean.EmcStandardSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.bean.EmcStdSetCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.service.EmcStdSetCategoryService;
import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/mdm/bizmdm/std-set-categorys")
public interface EmcStdSetCategoryResource extends GenericResource<EmcStdSetCategoryService, EmcStdSetCategoryBean, Long> {
    @RequestMapping(value = "/{id}/standard-sets", method = RequestMethod.POST)
    Long insertStandardSet(Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{id}/standard-sets/queries", method = RequestMethod.POST)
    Page<EmcStandardSetBean> selectStandardSetPaginationById(Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/standard-sets", method = RequestMethod.DELETE)
    void deleteStandardSet(RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{standardSetId}/field-sets", method = RequestMethod.POST)
    Long insertFieldSet(Long standardSetId, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{standardSetId}/field-sets/queries", method = RequestMethod.POST)
    Page<EmcFieldSetBean> selectFieldSetPaginationByStandardSetId(Long standardSetId, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/field-sets", method = RequestMethod.DELETE)
    void deleteFieldSet(RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/result-fields/queries/choosable", method = RequestMethod.POST)
    Page<BeanPropertyDescriptor> selectFieldInfoPagination(RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/standard-sets/queries/selectable", method = RequestMethod.GET)
    List<EmcStandardSetBean> selectStandardSetCategorySelectableList();

    @RequestMapping(value = "/stand-sets/{standSetId}", method = RequestMethod.GET)
    EmcStandardSetBean selectStandardSetDetail(Long standSetId);

    @RequestMapping(value = "/field-sets/queries", method = RequestMethod.POST)
    List<EmcFieldSetBean> selectFieldSetList(RestJsonWrapperBean wrapper);
}
