package com.sunwayworld.emc.module.mdm.basemdm.subcontractor.resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.bean.EmcSubcontractRecordBean;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.bean.EmcSubcontractorBean;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.service.EmcSubcontractorService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;

/**
 * @author magh@sunwayworld.com
 * @date 2019-11-05
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/subcontractors")
public interface EmcSubcontractorResource extends GenericResource<EmcSubcontractorService, EmcSubcontractorBean, Long>, GenericSelectableResource<EmcSubcontractorService, EmcSubcontractorBean, Long>,
        GenericChoosableResource<EmcSubcontractorService, EmcSubcontractorBean, Long> {

    @RequestMapping(value = "/{id}/records/queries", method = RequestMethod.POST)
    Page<EmcSubcontractRecordBean> selectRecordPaginationBySubcontractorId(@PathVariable Long id, RestJsonWrapperBean wrapper);

}
