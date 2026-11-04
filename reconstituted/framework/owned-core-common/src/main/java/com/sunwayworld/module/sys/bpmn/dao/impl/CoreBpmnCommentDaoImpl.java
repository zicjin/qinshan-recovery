/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnCommentBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnCommentDTO;
import com.sunwayworld.module.sys.bpmn.dao.CoreBpmnCommentDao;
import com.sunwayworld.module.sys.bpmn.mapper.CoreBpmnCommentMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreBpmnCommentDaoImpl
extends MybatisDaoSupport<CoreBpmnCommentBean, Long>
implements CoreBpmnCommentDao {
    @Autowired
    private CoreBpmnCommentMapper commentMapper;

    public CoreBpmnCommentMapper getMapper() {
        return this.commentMapper;
    }

    @Override
    public List<CoreBpmnCommentDTO> selectCommentList(String targetId) {
        return this.getMapper().selectCommentList(targetId).stream().map(m -> PersistableHelper.mapToPersistable(m, CoreBpmnCommentDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<String> selectUserTaskAuditedByList(String targetId, String statusCode) {
        return this.getMapper().selectUserTaskAuditedByList(targetId, statusCode);
    }
}
