package com.sunwayworld.emc.module.mdm.basemdm.vendor.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sunwayworld.emc.module.mdm.basemdm.vendor.bean.EmcVendorBean;
import com.sunwayworld.emc.module.mdm.basemdm.vendor.bean.EmcVendorMtlBean;
import com.sunwayworld.emc.module.mdm.basemdm.vendor.service.EmcVendorService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

@RequestMapping("/secure/emc/module/mdm/basemdm/vendors")
public interface EmcVendorResource extends GenericResource<EmcVendorService, EmcVendorBean, Long>, GenericChoosableResource<EmcVendorService, EmcVendorBean, Long> {

    Long insert(RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{id}/mtls", method = RequestMethod.POST)
    Long insertMtl(Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{id}/mtls/queries", method = RequestMethod.POST)
    Page<EmcVendorMtlBean> selectMtlPaginationById(Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/mtls", method = RequestMethod.DELETE)
    void deleteMtl(RestJsonWrapperBean wrapper);

}
