/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.constant.resource;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.sys.constant.bean.CoreConstantBean;
import com.sunwayworld.module.sys.constant.service.CoreConstantService;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/constants"})
public interface CoreConstantResource
extends GenericResource<CoreConstantService, CoreConstantBean, String> {
}
