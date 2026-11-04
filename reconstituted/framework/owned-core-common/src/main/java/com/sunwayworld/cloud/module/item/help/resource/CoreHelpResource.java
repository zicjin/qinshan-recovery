/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.help.resource;

import com.sunwayworld.cloud.module.item.help.bean.CoreHelpBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/core/module/item/helps"})
public interface CoreHelpResource
extends GenericCloudResource<CoreHelpBean, String> {
}
