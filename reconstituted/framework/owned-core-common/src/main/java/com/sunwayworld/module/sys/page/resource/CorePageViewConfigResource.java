/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.page.resource;

import com.sunwayworld.module.sys.page.bean.CorePageViewConfigBean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/core/module/sys/page-view-configs"})
public interface CorePageViewConfigResource {
    public CorePageViewConfigBean selectById(@PathVariable String var1);
}
