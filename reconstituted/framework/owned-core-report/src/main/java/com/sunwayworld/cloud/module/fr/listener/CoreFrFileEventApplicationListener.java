/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.fr.listener;

import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.module.item.file.constant.CoreFileOperation;
import com.sunwayworld.module.item.file.event.CoreFileEvent;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class CoreFrFileEventApplicationListener
implements ApplicationListener<CoreFileEvent> {
    private static final String INDEPENDENT_DEPLOY_FILE_UPLOAD_URL_SUFFIX = "/fr-files";
    @Autowired
    private CoreFileManager coreFileManager;
    @Autowired
    private Environment environment;

    @Override
    public void onApplicationEvent(CoreFileEvent event) {
        if (FileScope.finereport.name().equals(event.getCoreFile().getScope()) && CoreFileOperation.INSERT.equals(event.getSource()) && "independent".equals(this.environment.getProperty("sunway.config.fine.deploy-mode"))) {
            FilePathDTO filePath = CoreFileUtils.toFilePath(event.getCoreFile());
            this.coreFileManager.download(filePath);
            HttpClientManager.getInstance().upload(this.environment.getProperty("sunway.config.fine.independent-deploy-server.url") + INDEPENDENT_DEPLOY_FILE_UPLOAD_URL_SUFFIX, FilePathManager.getLocalPath(filePath).toFile(), filePath);
        }
    }
}
