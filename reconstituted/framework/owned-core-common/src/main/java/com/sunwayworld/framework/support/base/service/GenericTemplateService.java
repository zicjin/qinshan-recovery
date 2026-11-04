/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.base.service;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public interface GenericTemplateService<ID extends Serializable> {
    public static <ID extends Serializable> GenericTemplateService<ID> getInstance(String service) {
        return (GenericTemplateService)ApplicationContextHelper.getBean(service);
    }

    public List<Pair<String, String>> getTemplatePairList();

    default public String getTemplateExtension() {
        return "xls";
    }

    default public String getDownloadUrl(String templateCode, ID id) {
        List<Pair<String, String>> templatePairList = this.getTemplatePairList();
        Pair templatePair = Objects.requireNonNull(templatePairList.stream().filter(t -> ((String)t.getFirst()).equals(templateCode)).findFirst().orElse(null));
        FilePathDTO filePath = FilePathDTO.of(FileScope.temp.name(), "" + System.currentTimeMillis(), UUID.randomUUID().toString(), (String)templatePair.getSecond() + "." + this.getTemplateExtension());
        Path path = FilePathManager.getLocalPath(filePath);
        CoreFileManager fileManager = ApplicationContextHelper.getBean(CoreFileManager.class);
        try {
            FileUtils.makeDirs(path);
            URL resource = this.getClass().getResource("");
            if (FileUtils.isInJarURL(resource)) {
                InputStream is = this.getClass().getResourceAsStream("templates/" + templateCode + "." + this.getTemplateExtension());
                Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Path templatePath = new File(resource.getPath()).toPath().resolve("templates").resolve(templateCode + "." + this.getTemplateExtension());
                Files.copy(templatePath, path, StandardCopyOption.REPLACE_EXISTING);
            }
            this.initTemplateData(path, id);
            fileManager.upload(filePath, path);
        }
        catch (IOException ioe) {
            throw new FileException(ioe);
        }
        return fileManager.getDownloadUrl(filePath);
    }

    default public void initTemplateData(Path path, ID id) {
    }
}
