package com.sunwayworld.emc.module.mdm.basemdm.vendor.resource;

import com.sunwayworld.emc.module.mdm.basemdm.vendor.bean.EmcVendorBean;
import com.sunwayworld.emc.module.mdm.basemdm.vendor.bean.EmcVendorMtlBean;
import com.sunwayworld.emc.module.mdm.basemdm.vendor.service.EmcVendorService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/emc/module/mdm/basemdm/vendors"})
public interface EmcVendorResource
extends GenericResource<EmcVendorService, EmcVendorBean, Long>,
GenericChoosableResource<EmcVendorService, EmcVendorBean, Long>,
GenericSelectableResource<EmcVendorService, EmcVendorBean, Long> {
    public Long insert(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}/mtls"}, method={RequestMethod.POST})
    public Long insertMtl(Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/mtls/queries"}, method={RequestMethod.POST})
    public Page<EmcVendorMtlBean> selectMtlPaginationById(Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/mtls"}, method={RequestMethod.DELETE})
    public void deleteMtl(RestJsonWrapperBean var1);
}
