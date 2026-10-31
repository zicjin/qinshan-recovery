package com.sunwayworld.emc.module.mdm.basemdm.vendor.resource.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.vendor.bean.EmcVendorMtlBean;
import com.sunwayworld.emc.module.mdm.basemdm.vendor.resource.EmcVendorResource;
import com.sunwayworld.emc.module.mdm.basemdm.vendor.service.EmcVendorService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("生产商供应商")
@RestController
@GikamBean
public class EmcVendorResourceImpl implements EmcVendorResource {
    @Autowired
    private EmcVendorService emcVendorService;

    @Override
    public EmcVendorService getService() {
        return emcVendorService;
    }

    @Log(value = "新增生产商供应商", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }


    @Log(value = "新增供应物品记录", type = LogType.INSERT)
    @Override
    public Long insertMtl(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().insertMtl(id,wrapper);
    }

    @Log(value = "查询供应物品记录", type = LogType.SELECT)
    @Override
    public Page<EmcVendorMtlBean> selectMtlPaginationById(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectMtlPaginationById(id,wrapper);
    }

    @Log(value = "删除供应物品记录", type = LogType.DELETE)
    @Override
    public void deleteMtl(RestJsonWrapperBean wrapper) {
        getService().deleteMtl(wrapper);
    }
}
