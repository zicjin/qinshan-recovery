/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.io.file;

import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.utils.SystemUtils;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FilePathManager {
    public static final String FILE_ROOT_DIR_NAME = "files";
    private static volatile Path applicationFileRootPath;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static final Path getApplicationRootPath() {
        if (applicationFileRootPath != null) return applicationFileRootPath;
        Class<CoreFileUtils> clazz = CoreFileUtils.class;
        synchronized (CoreFileUtils.class) {
            if (applicationFileRootPath != null) return applicationFileRootPath;
            applicationFileRootPath = SystemUtils.getWorkingPath();
            // ** MonitorExit[var0] (shouldn't be in output)
            return applicationFileRootPath;
        }
    }

    public static final Path getApplicationFilePath() {
        return FilePathManager.getApplicationRootPath().resolve(FILE_ROOT_DIR_NAME);
    }

    public static final Path getLocalPath(FileScope fileScope) {
        return FilePathManager.getApplicationFilePath().resolve(fileScope.name());
    }

    public static final Path getLocalPath(Path relativeFilePath) {
        return FilePathManager.getApplicationRootPath().resolve(relativeFilePath);
    }

    public static final Path getLocalPath(FilePathDTO filePath) {
        return FilePathManager.getLocalDirPath(filePath).resolve(filePath.getFileName());
    }

    public static final Path getLocalDirPath(FilePathDTO filePath) {
        Path path = FilePathManager.getApplicationFilePath().resolve(filePath.getScope().name());
        if (!FileScope.finereport.equals((Object)filePath.getScope()) && !StringUtils.isBlank(filePath.getTimestamp())) {
            path = path.resolve(filePath.getTimestamp());
        }
        return path.resolve(filePath.getIrregularName());
    }

    public static final String getUrl(FilePathDTO filePath) {
        StringBuilder sb = new StringBuilder("/").append(FILE_ROOT_DIR_NAME).append("/").append(filePath.getScope().name()).append("/");
        if (!FileScope.finereport.equals((Object)filePath.getScope()) && !StringUtils.isBlank(filePath.getTimestamp())) {
            sb.append(filePath.getTimestamp()).append("/");
        }
        return sb.append(filePath.getIrregularName()).append("/").append(StringUtils.encodeURL(filePath.getFileName())).toString();
    }

    public static final FilePathDTO toFilePath(String downloadUri) {
        String[] values = StringUtils.split(StringUtils.decodeURL(downloadUri), "/");
        if (values.length == 6) {
            return FilePathDTO.of(values[2], values[3], values[4], values[5]);
        }
        if (values.length == 5) {
            return FilePathDTO.of(values[2], "", values[3], values[4]);
        }
        return null;
    }

    public static final Path getRelativePath(Path fileAbsolutePath) {
        ArrayList nameList = new ArrayList();
        fileAbsolutePath.spliterator().forEachRemaining(p -> nameList.add(p.toString()));
        ArrayList<String> selectedNameList = new ArrayList<String>();
        for (int i = nameList.size() - 1; i >= 0; --i) {
            String name = (String)nameList.get(i);
            if (i < nameList.size() - 2 && FILE_ROOT_DIR_NAME.equals(name)) break;
            selectedNameList.add(0, name);
        }
        Path relativePath = Paths.get(FILE_ROOT_DIR_NAME, new String[0]);
        for (String selectedName : selectedNameList) {
            relativePath = relativePath.resolve(selectedName);
        }
        return relativePath;
    }
}
