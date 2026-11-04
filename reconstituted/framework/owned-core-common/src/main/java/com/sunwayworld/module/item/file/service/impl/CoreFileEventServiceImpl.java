/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.constant.CoreFileOperation;
import com.sunwayworld.module.item.file.event.CoreFileEvent;
import com.sunwayworld.module.item.file.service.CoreFileEventService;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreFileEventServiceImpl
implements CoreFileEventService {
    @Override
    public void publishFileEvent(CoreFileOperation fileOpeartion, CoreFileBean file) {
        CoreFileEvent fileEvent = new CoreFileEvent(fileOpeartion, null, file);
        ApplicationContextHelper.getApplicationContext().publishEvent(fileEvent);
    }
}
