/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.i18n.resource;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nConfigBean;
import com.sunwayworld.module.sys.i18n.service.CoreI18nConfigService;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/i18n-configs"})
public interface CoreI18nConfigResource
extends GenericSelectableResource<CoreI18nConfigService, CoreI18nConfigBean, String>,
GenericResource<CoreI18nConfigService, CoreI18nConfigBean, String> {
}
