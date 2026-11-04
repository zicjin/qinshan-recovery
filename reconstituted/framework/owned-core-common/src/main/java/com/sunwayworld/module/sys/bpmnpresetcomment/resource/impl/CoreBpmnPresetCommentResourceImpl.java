/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmnpresetcomment.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmnpresetcomment.resource.CoreBpmnPresetCommentResource;
import com.sunwayworld.module.sys.bpmnpresetcomment.service.CoreBpmnPresetCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u6d41\u7a0b\u9884\u7f6e\u610f\u89c1")
@RestController
@GikamBean
public class CoreBpmnPresetCommentResourceImpl
implements CoreBpmnPresetCommentResource {
    @Autowired
    private CoreBpmnPresetCommentService coreBpmnPresetCommentService;

    @Override
    public CoreBpmnPresetCommentService getService() {
        return this.coreBpmnPresetCommentService;
    }

    @Override
    @Log(value="\u65b0\u589e\u6d41\u7a0b\u9884\u7f6e\u610f\u89c1", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }
}
