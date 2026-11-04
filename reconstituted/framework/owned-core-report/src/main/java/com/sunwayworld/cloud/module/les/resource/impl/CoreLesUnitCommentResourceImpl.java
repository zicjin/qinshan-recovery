/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.resource.impl;

import com.sunwayworld.cloud.module.les.bean.CoreLesUnitCommentBean;
import com.sunwayworld.cloud.module.les.resource.CoreLesUnitCommentResource;
import com.sunwayworld.cloud.module.les.service.CoreLesUnitCommentService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="LES\u7ec4\u4ef6\u6279\u6ce8")
@RestController
@GikamBean
@RequestMapping(value={"/secure/core/les/unit-comments"})
public class CoreLesUnitCommentResourceImpl
implements CoreLesUnitCommentResource,
AbstractGenericResource<CoreLesUnitCommentService, CoreLesUnitCommentBean, Long> {
    @Autowired
    private CoreLesUnitCommentService coreLesUnitCommentService;

    @Override
    public CoreLesUnitCommentService getService() {
        return this.coreLesUnitCommentService;
    }

    @Override
    @Log(value="\u65b0\u589eLES\u7ec4\u4ef6\u6279\u6ce8\u8868", type=LogType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u4fee\u6539\u6279\u6ce8", type=LogType.UPDATE)
    public void update(RestJsonWrapperBean wrapper) {
        this.getService().update(wrapper);
    }

    @Override
    @Log(value="\u56de\u590d\u6279\u6ce8", type=LogType.UPDATE)
    public void reply(RestJsonWrapperBean wrapper) {
        this.getService().reply(wrapper);
    }
}
