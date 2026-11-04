/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.request.resource;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.admin.request.bean.CoreRequestUrlBean;
import com.sunwayworld.module.admin.request.service.CoreRequestUrlService;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/module/admin/request-urls"})
public interface CoreRequestUrlResource
extends GenericResource<CoreRequestUrlService, CoreRequestUrlBean, Long> {
}
