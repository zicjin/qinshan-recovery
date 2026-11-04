/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.gikam.repository.resource.impl;

import com.sunwayworld.module.gikam.repository.bean.GikamRepositoryStatusDTO;
import com.sunwayworld.module.gikam.repository.resource.GikamRepositoryResource;
import com.sunwayworld.module.gikam.repository.service.GikamRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GikamRepositoryResourceImpl
implements GikamRepositoryResource {
    @Autowired
    private GikamRepositoryService respositoryService;

    @Override
    public GikamRepositoryStatusDTO markItemEditable(@PathVariable String key) {
        return this.respositoryService.markItemEditable(key);
    }
}
