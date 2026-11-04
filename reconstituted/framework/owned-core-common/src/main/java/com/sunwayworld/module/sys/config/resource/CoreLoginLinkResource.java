/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.config.resource;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.sys.config.bean.CoreLoginLinkBean;
import com.sunwayworld.module.sys.config.service.CoreLoginLinkService;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/core/module/sys/login-links"})
public interface CoreLoginLinkResource
extends GenericResource<CoreLoginLinkService, CoreLoginLinkBean, Long> {
}
