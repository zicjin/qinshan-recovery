/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.config.resource;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.sys.config.bean.CoreAboutSystemBean;
import com.sunwayworld.module.sys.config.service.CoreAboutSystemService;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/about-systems"})
public interface CoreAboutSystemResource
extends GenericResource<CoreAboutSystemService, CoreAboutSystemBean, Long> {
}
