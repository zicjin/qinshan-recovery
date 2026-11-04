/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.dms.resource;

import com.sunwayworld.cloud.module.dms.bean.CoreDmsCategoryBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.tree.resource.GenericTreeCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/dms/categories"})
public interface CoreDmsCategoryResource
extends GenericCloudResource<CoreDmsCategoryBean, String>,
GenericTreeCloudResource<CoreDmsCategoryBean, String> {
}
