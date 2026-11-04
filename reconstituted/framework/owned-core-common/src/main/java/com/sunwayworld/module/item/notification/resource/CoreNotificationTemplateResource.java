/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.resource;

import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.module.item.notification.bean.CoreNotificationTemplateBean;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/item/notification/notification-templates"})
public interface CoreNotificationTemplateResource
extends GenericCloudResource<CoreNotificationTemplateBean, String> {
}
