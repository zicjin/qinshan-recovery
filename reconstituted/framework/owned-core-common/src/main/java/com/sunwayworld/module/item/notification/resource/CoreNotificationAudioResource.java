/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.resource;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;
import com.sunwayworld.module.item.notification.bean.CoreNotificationAudioBean;
import com.sunwayworld.module.item.notification.service.CoreNotificationAudioService;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/item/notification/audios"})
public interface CoreNotificationAudioResource
extends GenericResource<CoreNotificationAudioService, CoreNotificationAudioBean, Long>,
GenericSelectableResource<CoreNotificationAudioService, CoreNotificationAudioBean, Long> {
}
