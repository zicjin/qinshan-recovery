/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnCommentBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnCommentDTO;
import java.util.List;

public interface CoreBpmnCommentService
extends GenericService<CoreBpmnCommentBean, Long> {
    public Page<CoreBpmnCommentDTO> selectCommentList(String var1, String var2);

    public List<String> selectUserTaskAuditedByList(String var1, String var2);
}
