/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.usertrain.resource;

import com.sunwayworld.cloud.module.qms.usertrain.bean.CoreUserTrainBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/qms/user-trains"})
public interface CoreUserTrainResource
extends GenericCloudResource<CoreUserTrainBean, Long>,
GenericSelectableCloudResource<CoreUserTrainBean, Long> {
}
