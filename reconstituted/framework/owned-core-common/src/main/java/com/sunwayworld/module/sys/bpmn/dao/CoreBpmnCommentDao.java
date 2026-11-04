/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.dao;

import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnCommentBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnCommentDTO;
import java.util.List;

public interface CoreBpmnCommentDao
extends GenericDao<CoreBpmnCommentBean, Long> {
    public List<CoreBpmnCommentDTO> selectCommentList(String var1);

    public List<String> selectUserTaskAuditedByList(String var1, String var2);
}
