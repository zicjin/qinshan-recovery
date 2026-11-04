/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filevolume.resource.impl;

import com.sunwayworld.cloud.module.qms.filevolume.bean.CoreFileVolumeBean;
import com.sunwayworld.cloud.module.qms.filevolume.resource.CoreFileVolumeResource;
import com.sunwayworld.cloud.module.qms.filevolume.service.CoreFileVolumeService;
import com.sunwayworld.cloud.module.qms.filevolume.validator.CoreFileVolumeDeleteValidator;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.activatable.resource.AbstractGenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import com.sunwayworld.framework.support.selectable.resource.AbstractGenericSelectableResource;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.framework.validator.permission.annotation.ValidatePermissionWith;
import com.sunwayworld.framework.validator.permission.support.DefaultUpdatablePermissionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u6587\u4ef6\u518c")
@RestController
@GikamBean
public class CoreFileVolumeResourceImpl
implements CoreFileVolumeResource,
AbstractGenericResource<CoreFileVolumeService, CoreFileVolumeBean, Long>,
AbstractGenericSelectableResource<CoreFileVolumeService, CoreFileVolumeBean, Long>,
AbstractGenericChoosableResource<CoreFileVolumeService, CoreFileVolumeBean, Long>,
AbstractGenericActivatableResource<CoreFileVolumeService, CoreFileVolumeBean, Long> {
    @Autowired
    private CoreFileVolumeService coreFileVolumeService;

    @Override
    public CoreFileVolumeService getService() {
        return this.coreFileVolumeService;
    }

    @Override
    @Log(value="\u65b0\u589e\u6587\u4ef6\u518c", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664", type=LogType.DELETE)
    @ValidatePermissionWith(value=DefaultUpdatablePermissionValidator.class, table="#this.getService().getDao().getTable()")
    @ValidateDataWith(value=CoreFileVolumeDeleteValidator.class)
    public void delete(RestJsonWrapperBean wrapper) {
        this.getService().delete(wrapper);
    }
}
