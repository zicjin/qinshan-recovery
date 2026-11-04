/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.fileacq;

import com.sunwayworld.cloud.module.equipt.record.bean.CoreEquiptAcqRecordBean;
import com.sunwayworld.framework.utils.FileUtils;
import java.io.File;

public interface CoreEquiptFileAcqService {
    default public String extractTextFromFile(File file) {
        return FileUtils.extractTextFromFile(file);
    }

    public void fileAcq(CoreEquiptAcqRecordBean var1);

    default public void preAcqRecordInserted(CoreEquiptAcqRecordBean acqRecord, File file) {
    }
}
