/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.design.studio.bean.CoreStudioBlockBean;
import com.sunwayworld.module.design.studio.bean.CoreStudioBlockLineBean;
import com.sunwayworld.module.design.studio.resource.CoreStudioBlockResource;
import com.sunwayworld.module.design.studio.service.CoreStudioBlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
public class CoreStudioBlockResourceImpl
implements CoreStudioBlockResource {
    @Autowired
    private CoreStudioBlockService studioBlockService;

    @Override
    public CoreStudioBlockService getService() {
        return this.studioBlockService;
    }

    @Override
    @Log(value="\u901a\u8fc7\u9875\u9762\u7684\u8bf7\u6c42\u5730\u5740\u67e5\u8be2\u5f53\u524d\u9875\u9762\u7684\u914d\u7f6e\u6a21\u5757\u8868", type=LogType.SELECT)
    public Page<CoreStudioBlockBean> selectPaginationByPageUrl(RestJsonWrapperBean wrapper) {
        return this.getService().selectPaginationByPageUrl(wrapper);
    }

    @Override
    public Page<CoreStudioBlockLineBean> selectLinePaginationByMasterId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.studioBlockService.selectLinePaginationByMasterId(id, wrapper);
    }

    @Override
    @Log(value="\u4fdd\u5b58\u5f53\u524d\u9009\u4e2d\u7684\u914d\u7f6e\u8868\u548c\u914d\u7f6e\u5b57\u6bb5", type=LogType.UPDATE)
    public Long save(RestJsonWrapperBean wrapper) {
        return this.getService().save(wrapper);
    }
}
