/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filevolume.service;

import com.sunwayworld.cloud.module.qms.filevolume.bean.CoreFileVolumeBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

public interface CoreFileVolumeService
extends GenericService<CoreFileVolumeBean, Long>,
GenericSelectableService<CoreFileVolumeBean, Long>,
GenericChoosableService<CoreFileVolumeBean, Long>,
GenericActivatableService<CoreFileVolumeBean, Long> {
}
