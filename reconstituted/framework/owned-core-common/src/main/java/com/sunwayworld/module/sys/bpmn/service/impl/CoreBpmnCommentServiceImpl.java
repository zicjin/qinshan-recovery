/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.service.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnCommentDTO;
import com.sunwayworld.module.sys.bpmn.dao.CoreBpmnCommentDao;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnCommentService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreBpmnCommentServiceImpl
implements CoreBpmnCommentService {
    @Autowired
    private CoreBpmnCommentDao commentDao;

    @Override
    public CoreBpmnCommentDao getDao() {
        return this.commentDao;
    }

    @Override
    public Page<CoreBpmnCommentDTO> selectCommentList(String table, String id) {
        return new Pagination<CoreBpmnCommentDTO>(this.getDao().selectCommentList(table + "$" + ObjectUtils.toString(id)));
    }

    @Override
    public List<String> selectUserTaskAuditedByList(String targetId, String statusCode) {
        return this.getDao().selectUserTaskAuditedByList(targetId, statusCode);
    }
}
