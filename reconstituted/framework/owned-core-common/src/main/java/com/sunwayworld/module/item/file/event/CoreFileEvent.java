/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.event;

import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.constant.CoreFileOperation;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.Nullable;

public class CoreFileEvent
extends ApplicationEvent {
    private static final long serialVersionUID = -595507526485393875L;
    private CoreFileBean coreFile;
    private String category;

    public CoreFileEvent(CoreFileOperation fileOperation, @Nullable String category, CoreFileBean coreFile) {
        super((Object)fileOperation);
        this.coreFile = coreFile;
        this.category = category;
    }

    public CoreFileBean getCoreFile() {
        return this.coreFile;
    }

    public void setCoreFile(CoreFileBean coreFile) {
        this.coreFile = coreFile;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
