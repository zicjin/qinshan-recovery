/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.io.text;

import com.sunwayworld.framework.cache.memory.MemoryCacheManager;
import com.sunwayworld.framework.io.text.FileToTextExtractor;
import com.sunwayworld.framework.io.text.impl.ExcelFileToTextExtractor;
import com.sunwayworld.framework.io.text.impl.ExcelFileToTextFullExtractor;
import com.sunwayworld.framework.utils.ClassUtils;
import java.util.List;
import java.util.stream.Collectors;

public abstract class FileToTextExtractorFactory {
    public static final FileToTextExtractor getFileToTextExtractor(String fileExtension) {
        for (FileToTextExtractor textExtractor : FileToTextExtractorFactory.getTextExtractorList()) {
            if (ExcelFileToTextFullExtractor.class.equals(textExtractor.getClass()) || !textExtractor.match(fileExtension)) continue;
            return textExtractor;
        }
        return null;
    }

    public static final FileToTextExtractor getFileToFullTextExtractor(String fileExtension) {
        for (FileToTextExtractor textExtractor : FileToTextExtractorFactory.getTextExtractorList()) {
            if (ExcelFileToTextExtractor.class.equals(textExtractor.getClass()) || !textExtractor.match(fileExtension)) continue;
            return textExtractor;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static final List<FileToTextExtractor> getTextExtractorList() {
        List textExtractorList = (List)MemoryCacheManager.get("FileToTextExtractorList");
        if (textExtractorList != null) return textExtractorList;
        Class<FileToTextExtractorFactory> clazz = FileToTextExtractorFactory.class;
        synchronized (FileToTextExtractorFactory.class) {
            textExtractorList = (List)MemoryCacheManager.get("FileToTextExtractorList");
            if (textExtractorList != null) return textExtractorList;
            textExtractorList = ClassUtils.getPredicatedClasses(FileToTextExtractorFactory.class.getPackage().getName(), c -> FileToTextExtractor.class.isAssignableFrom((Class<?>)c)).stream().filter(c -> !c.isInterface()).map(c -> (FileToTextExtractor)ClassUtils.newInstance(c)).collect(Collectors.toList());
            MemoryCacheManager.put("FileToTextExtractorList", textExtractorList);
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return textExtractorList;
        }
    }
}
