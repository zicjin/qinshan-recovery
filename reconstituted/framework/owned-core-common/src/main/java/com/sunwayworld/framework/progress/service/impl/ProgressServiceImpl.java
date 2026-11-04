/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.progress.service.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.executor.ExecutorHelper;
import com.sunwayworld.framework.progress.bean.ProgressDTO;
import com.sunwayworld.framework.progress.core.ProgressProperties;
import com.sunwayworld.framework.progress.core.ProgressRunnable;
import com.sunwayworld.framework.progress.core.ProgressTask;
import com.sunwayworld.framework.progress.service.ProgressService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class ProgressServiceImpl
implements ProgressService {
    @Autowired
    private ProgressProperties progressProperties;

    @Override
    public void start(String progressId, String taskName, List<ProgressTask> taskList) {
        ProgressDTO progress = new ProgressDTO();
        progress.setId(progressId);
        progress.setTaskName(taskName);
        progress.setTotal(0L + (long)taskList.size());
        progress.setSuccess(0L);
        progress.setFailure(0L);
        progress.setStartTime(LocalDateTime.now());
        RedisHelper.put(ProgressRunnable.getCacheName(this.progressProperties.getCache().getName()), progressId, progress, this.progressProperties.getCache().getTimeout());
        List<ProgressRunnable> prList = taskList.stream().map(t -> new ProgressRunnable(progressId, (ProgressTask)t)).collect(Collectors.toList());
        if (this.progressProperties.getMode().getAsync().booleanValue()) {
            prList.forEach(ExecutorHelper::submit);
        } else {
            ExecutorHelper.submit(() -> prList.forEach(r -> r.run()));
        }
    }

    @Override
    public ProgressDTO selectById(String progressId) {
        return (ProgressDTO)RedisHelper.get(this.progressProperties.getCache().getName(), progressId);
    }

    @Override
    public List<ProgressDTO> selectMyProgressList() {
        RedisTemplate<String, Object> redisTemplate = RedisHelper.getRedisTemplate();
        Set<String> set = redisTemplate.keys(ProgressRunnable.getCacheName(this.progressProperties.getCache().getName()) + "::*");
        if (CollectionUtils.isEmpty(set)) {
            return CollectionUtils.emptyList();
        }
        List<Object> progressList = redisTemplate.opsForValue().multiGet(set);
        if (CollectionUtils.isEmpty(progressList)) {
            return CollectionUtils.emptyList();
        }
        return progressList.stream().map(p -> (ProgressDTO)p).sorted((p1, p2) -> p2.getStartTime().compareTo(p1.getStartTime())).collect(Collectors.toList());
    }
}
