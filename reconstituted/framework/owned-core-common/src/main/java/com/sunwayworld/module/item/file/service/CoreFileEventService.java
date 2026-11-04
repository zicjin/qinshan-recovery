/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.service;

import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.constant.CoreFileOperation;

public interface CoreFileEventService {
    public void publishFileEvent(CoreFileOperation var1, CoreFileBean var2);
}
