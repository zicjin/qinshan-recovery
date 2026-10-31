package com.sunwayworld.emc.module.mdm.basemdm.vendor.service;

import com.sunwayworld.emc.module.mdm.basemdm.vendor.bean.EmcVendorBean;
import com.sunwayworld.emc.module.mdm.basemdm.vendor.bean.EmcVendorMtlBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

public interface EmcVendorService
extends GenericService<EmcVendorBean, Long>,
GenericChoosableService<EmcVendorBean, Long>,
GenericSelectableService<EmcVendorBean, Long> {
    public Long insert(RestJsonWrapperBean var1);

    public Long insertMtl(Long var1, RestJsonWrapperBean var2);

    public Page<EmcVendorMtlBean> selectMtlPaginationById(Long var1, RestJsonWrapperBean var2);

    public void deleteMtl(RestJsonWrapperBean var1);
}
