/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filevolume.resource;

import com.sunwayworld.cloud.module.qms.filevolume.bean.CoreFileVolumeBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/qms/file-volumes"})
public interface CoreFileVolumeResource
extends GenericCloudResource<CoreFileVolumeBean, Long>,
GenericSelectableCloudResource<CoreFileVolumeBean, Long>,
GenericChoosableCloudResource<CoreFileVolumeBean, Long>,
GenericActivatableCloudResource<CoreFileVolumeBean, Long> {
}
