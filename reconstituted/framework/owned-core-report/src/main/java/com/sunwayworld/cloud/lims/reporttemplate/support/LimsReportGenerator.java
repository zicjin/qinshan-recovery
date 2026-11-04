/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support;

import com.deepoove.poi.config.ConfigureBuilder;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataMap;
import com.sunwayworld.framework.io.file.FilePathDTO;
import java.nio.file.Path;

public interface LimsReportGenerator {
    public Path getTemplateFile();

    public LimsTemplateDataMap buildParam(ConfigureBuilder var1);

    public FilePathDTO buildReport();
}
