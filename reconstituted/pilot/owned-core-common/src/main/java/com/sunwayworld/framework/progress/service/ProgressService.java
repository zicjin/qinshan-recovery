/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.progress.service;

import com.sunwayworld.framework.progress.bean.ProgressDTO;
import com.sunwayworld.framework.progress.core.ProgressTask;
import java.util.List;

public interface ProgressService {
    public static final String PROGRESS_REDIS_NAME = "PROGRESS";

    public void start(String var1, String var2, List<ProgressTask> var3);

    public ProgressDTO selectById(String var1);

    public List<ProgressDTO> selectMyProgressList();
}
