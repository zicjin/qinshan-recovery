package com.sunwayworld.emc.module.mdm.basemdm.vendor.service;

import com.sunwayworld.emc.module.mdm.basemdm.vendor.bean.EmcVendorBean;
import com.sunwayworld.emc.module.mdm.basemdm.vendor.bean.EmcVendorMtlBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface EmcVendorService extends GenericService<EmcVendorBean, Long>, GenericChoosableService<EmcVendorBean, Long> {
    Long insert(RestJsonWrapperBean wrapper);

    Long insertMtl(Long id, RestJsonWrapperBean wrapper);

    Page<EmcVendorMtlBean> selectMtlPaginationById(Long id, RestJsonWrapperBean wrapper);

    void deleteMtl(RestJsonWrapperBean wrapper);
}
