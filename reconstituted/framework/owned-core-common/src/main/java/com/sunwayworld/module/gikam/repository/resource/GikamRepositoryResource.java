/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.gikam.repository.resource;

import com.sunwayworld.module.gikam.repository.bean.GikamRepositoryStatusDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/gikam/repositories"})
public interface GikamRepositoryResource {
    @RequestMapping(value={"/mark-editable/{key}"}, method={RequestMethod.POST})
    public GikamRepositoryStatusDTO markItemEditable(@PathVariable String var1);
}
