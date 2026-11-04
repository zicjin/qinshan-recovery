/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.helpcenter.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.admin.helpcenter.bean.CoreHelpCenterBean;
import com.sunwayworld.module.admin.helpcenter.bean.CoreHelpCenterDTO;

public interface CoreHelpCenterService
extends GenericService<CoreHelpCenterBean, String> {
    public CoreHelpCenterDTO selectHelpCenterDTOByMenuId(String var1);
}
