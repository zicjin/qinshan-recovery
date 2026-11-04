/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnCommentDTO;
import com.sunwayworld.module.sys.bpmn.resource.CoreBpmnCommentResource;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogModule(value="\u5de5\u4f5c\u6d41\u7a0b\u4fe1\u606f")
@GikamBean
public class CoreBpmnCommentResourceImpl
implements CoreBpmnCommentResource {
    @Autowired
    private CoreBpmnCommentService commentService;

    public CoreBpmnCommentService getService() {
        return this.commentService;
    }

    @Override
    public Page<CoreBpmnCommentDTO> selectCommentList(RestJsonWrapperBean wrapper, @RequestParam String table, @RequestParam String id) {
        return this.commentService.selectCommentList(table, id);
    }
}
