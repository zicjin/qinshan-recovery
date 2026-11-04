/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.at.listener;

import com.sunwayworld.framework.at.handler.AuditTrailRelationTableDaoHandler;
import com.sunwayworld.framework.at.handler.RelationTableHandlerContext;
import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import com.sunwayworld.framework.mybatis.dao.support.DaoAfterDeleteContext;
import com.sunwayworld.framework.mybatis.dao.support.DaoAfterDeleteListener;
import com.sunwayworld.framework.mybatis.dao.support.DaoAfterInsertContext;
import com.sunwayworld.framework.mybatis.dao.support.DaoAfterInsertListener;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuditTrailRelationTableDaoListener
implements DaoAfterInsertListener,
DaoAfterDeleteListener {
    private final CaseInsensitiveLinkedMap<AuditTrailRelationTableDaoHandler> handlerMap = new CaseInsensitiveLinkedMap();

    @Autowired(required=false)
    public AuditTrailRelationTableDaoListener(List<AuditTrailRelationTableDaoHandler> handlers) {
        if (handlers != null) {
            for (AuditTrailRelationTableDaoHandler handler : handlers) {
                this.handlerMap.put(handler.tableName(), handler);
            }
        }
    }

    @Override
    public void afterDelete(DaoAfterDeleteContext context) {
        AuditTrailRelationTableDaoHandler auditTrailRelationTableDaoHandler = this.handlerMap.get(context.getTableName());
        if (auditTrailRelationTableDaoHandler != null) {
            RelationTableHandlerContext handlerContext = new RelationTableHandlerContext(context.getItems());
            auditTrailRelationTableDaoHandler.handleDelete(handlerContext);
        }
    }

    @Override
    public void afterInsert(DaoAfterInsertContext context) {
        AuditTrailRelationTableDaoHandler auditTrailRelationTableDaoHandler = this.handlerMap.get(context.getTableName());
        if (auditTrailRelationTableDaoHandler != null) {
            RelationTableHandlerContext handlerContext = new RelationTableHandlerContext(context.getItems());
            auditTrailRelationTableDaoHandler.handleInsert(handlerContext);
        }
    }
}
