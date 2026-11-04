/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customquery.resource;

import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomButtonBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/item/custom-buttons"})
public interface CoreCustomButtonResource
extends GenericCloudResource<CoreCustomButtonBean, Long>,
GenericChoosableCloudResource<CoreCustomButtonBean, Long> {
}
