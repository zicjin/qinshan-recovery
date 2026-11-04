/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.helpcenter.resource;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.admin.helpcenter.bean.CoreHelpCenterBean;
import com.sunwayworld.module.admin.helpcenter.bean.CoreHelpCenterDTO;
import com.sunwayworld.module.admin.helpcenter.service.CoreHelpCenterService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/module/admin/help-centers"})
public interface CoreHelpCenterResource
extends GenericResource<CoreHelpCenterService, CoreHelpCenterBean, String> {
    public CoreHelpCenterDTO selectHelpCenterDTOByMenuId(@PathVariable String var1);
}
