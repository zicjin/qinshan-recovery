/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.ncr.resource;

import com.sunwayworld.cloud.module.qms.ncr.bean.CoreNcrBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/qms/ncrs"})
public interface CoreNcrResource
extends GenericCloudResource<CoreNcrBean, Long> {
}
