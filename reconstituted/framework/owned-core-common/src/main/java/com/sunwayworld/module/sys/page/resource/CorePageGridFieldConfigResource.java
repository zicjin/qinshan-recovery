/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.page.resource;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.sys.page.bean.CorePageGridFieldConfigBean;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(value={"/core/module/sys/page-grid-field-configs"})
public interface CorePageGridFieldConfigResource {
    public List<CorePageGridFieldConfigBean> selectCurrentGridConfigList(@RequestParam String var1, @RequestParam String var2);

    public void insertOrUpdateCurrentGridConfig(RestJsonWrapperBean var1);
}
