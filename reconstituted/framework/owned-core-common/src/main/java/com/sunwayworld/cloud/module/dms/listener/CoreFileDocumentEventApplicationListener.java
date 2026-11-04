/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.dms.listener;

import com.sunwayworld.cloud.module.dms.resource.CoreFileDocumentResource;
import com.sunwayworld.module.item.file.constant.CoreFileOperation;
import com.sunwayworld.module.item.file.event.CoreFileEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix="spring.elasticsearch.rest", name={"enabled"}, havingValue="true", matchIfMissing=false)
public class CoreFileDocumentEventApplicationListener
implements ApplicationListener<CoreFileEvent> {
    @Autowired
    private CoreFileDocumentResource documentResource;

    @Override
    public void onApplicationEvent(CoreFileEvent event) {
        if (CoreFileOperation.INSERT.equals(event.getSource())) {
            this.documentResource.insertByFile(event.getCoreFile().getId(), event.getCategory());
        } else if (CoreFileOperation.UPDATE.equals(event.getSource())) {
            this.documentResource.updateByFile(event.getCoreFile().getId(), event.getCategory());
        } else if (CoreFileOperation.DELETE.equals(event.getSource())) {
            this.documentResource.deleteByFile(event.getCoreFile().getId());
        }
    }
}
