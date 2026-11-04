/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.progress.core;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.progress.bean.ProgressDTO;
import com.sunwayworld.framework.progress.core.ProgressProperties;
import com.sunwayworld.framework.progress.core.ProgressTask;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProgressRunnable
implements Runnable {
    private static final Logger logger = LogManager.getLogger(ProgressRunnable.class);
    private final LoginUser loginUser = LocalContextHelper.getLoginPrincipal();
    private String progressId;
    private final ProgressTask task;
    private static ProgressProperties progressProperties;

    public ProgressRunnable(String progressId, ProgressTask task) {
        this.progressId = progressId;
        this.task = task;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void run() {
        try {
            if (this.loginUser != null) {
                LocalContextHelper.setUserLogin(this.loginUser);
            }
            Objects.requireNonNull(this.progressId);
            String cacheName = ProgressRunnable.getCacheName(ProgressRunnable.getProgressProperties().getCache().getName());
            int cacheTimeout = ProgressRunnable.getProgressProperties().getCache().getTimeout();
            try {
                this.task.run();
            }
            catch (Exception ex) {
                String message = "(" + this.task.getSpanName() + "):" + ex.getMessage();
                logger.error(message, (Throwable)ex);
                this.task.rollback(ex);
                GikamConcurrentLocker.blockRun(cacheName + ":" + this.progressId, cacheTimeout, () -> {
                    ProgressDTO progress = (ProgressDTO)RedisHelper.get(cacheName, this.progressId);
                    progress.setFailure(progress.getFailure() + 1L);
                    if (StringUtils.isBlank(progress.getFailureMessage())) {
                        progress.setFailureMessage(message);
                    } else {
                        progress.setFailureMessage(progress.getFailureMessage() + "\n" + message);
                    }
                    RedisHelper.put(cacheName, this.progressId, progress, cacheTimeout);
                });
                logger.error(message, (Throwable)ex);
                LocalContextHelper.removeUserLogin();
                return;
            }
            GikamConcurrentLocker.blockRun(cacheName + ":" + this.progressId, cacheTimeout, () -> {
                ProgressDTO progress = (ProgressDTO)RedisHelper.get(cacheName, this.progressId);
                progress.setSuccess(progress.getSuccess() + 1L);
                RedisHelper.put(cacheName, this.progressId, progress, cacheTimeout);
            });
        }
        finally {
            LocalContextHelper.removeUserLogin();
        }
    }

    public static String getCacheName(String name) {
        return name + "$" + LocalContextHelper.getLoginUserId();
    }

    private static final ProgressProperties getProgressProperties() {
        if (progressProperties == null) {
            progressProperties = ApplicationContextHelper.getBean(ProgressProperties.class);
        }
        return progressProperties;
    }
}
