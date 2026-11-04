/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.page.resource;

import com.sunwayworld.module.sys.page.bean.CorePageConfigDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(value={"/core/module/sys/page-configs"})
public interface CorePageConfigResource {
    public CorePageConfigDTO selectCurrentPageConfig(@RequestParam String var1);
}
