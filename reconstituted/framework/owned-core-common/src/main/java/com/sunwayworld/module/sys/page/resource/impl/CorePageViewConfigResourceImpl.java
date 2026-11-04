/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.page.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.page.bean.CorePageViewConfigBean;
import com.sunwayworld.module.sys.page.resource.CorePageViewConfigResource;
import com.sunwayworld.module.sys.page.service.CorePageViewConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
@LogModule(value="\u9875\u9762\u663e\u793a\u914d\u7f6e")
public class CorePageViewConfigResourceImpl
implements CorePageViewConfigResource {
    @Autowired
    private CorePageViewConfigService configService;

    @Override
    @Log(value="\u67e5\u8be2\u9875\u9762\u663e\u793a\u914d\u7f6e", type=LogType.SELECT)
    @RequestMapping(value={"/{id}"}, method={RequestMethod.GET})
    public CorePageViewConfigBean selectById(@PathVariable String id) {
        return this.configService.selectById(id);
    }
}
