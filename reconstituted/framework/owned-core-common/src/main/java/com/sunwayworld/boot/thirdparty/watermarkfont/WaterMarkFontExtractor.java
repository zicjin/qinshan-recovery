/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.boot.thirdparty.watermarkfont;

import com.sunwayworld.boot.thirdparty.Extractor;
import com.sunwayworld.framework.utils.ArchiveUtils;
import com.sunwayworld.framework.utils.SystemUtils;
import java.nio.file.Path;

public class WaterMarkFontExtractor
implements Extractor {
    @Override
    public void extractForWindows() {
        Path workingPath = SystemUtils.getWorkingPath();
        ArchiveUtils.extractResourceIfNotPresent("/third-party/pdfwatermarkfont/windows/simsun.ttc", workingPath.resolve("simsun.ttc"));
    }

    @Override
    public void extractForLinux() {
        Path workingPath = SystemUtils.getWorkingPath();
        ArchiveUtils.extractResourceIfNotPresent("/third-party/pdfwatermarkfont/linux/simsun.ttc", workingPath.resolve("simsun.ttc"));
    }

    @Override
    public void extractForMac() {
        Path workingPath = SystemUtils.getWorkingPath();
        ArchiveUtils.extractResourceIfNotPresent("/third-party/pdfwatermarkfont/mac/simsun.ttc", workingPath.resolve("simsun.ttc"));
    }
}
