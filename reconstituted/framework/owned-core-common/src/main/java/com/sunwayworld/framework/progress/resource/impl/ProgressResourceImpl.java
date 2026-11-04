/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.progress.resource.impl;

import com.sunwayworld.framework.progress.bean.ProgressDTO;
import com.sunwayworld.framework.progress.resource.ProgressResource;
import com.sunwayworld.framework.progress.service.ProgressService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
public class ProgressResourceImpl
implements ProgressResource {
    @Autowired
    private ProgressService progressService;

    @Override
    public ProgressDTO selectById(String progressId) {
        return this.progressService.selectById(progressId);
    }

    @Override
    public List<ProgressDTO> selectMyProgressList() {
        return this.progressService.selectMyProgressList();
    }
}
