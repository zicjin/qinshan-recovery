/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.audit.listener;

import com.sunwayworld.framework.audit.AuditDatabaseOperation;
import com.sunwayworld.framework.audit.AuditLogHelper;
import com.sunwayworld.framework.audit.handler.AuditV2RelationTableDaoHandler;
import com.sunwayworld.framework.audit.handler.AuditV2RelationTableHandlerContext;
import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import com.sunwayworld.framework.mybatis.dao.support.DaoAfterDeleteContext;
import com.sunwayworld.framework.mybatis.dao.support.DaoAfterDeleteListener;
import com.sunwayworld.framework.mybatis.dao.support.DaoAfterInsertContext;
import com.sunwayworld.framework.mybatis.dao.support.DaoAfterInsertListener;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuditV2RelationTableDaoListener
implements DaoAfterInsertListener,
DaoAfterDeleteListener {
    private final CaseInsensitiveLinkedMap<AuditV2RelationTableDaoHandler> handlerMap = new CaseInsensitiveLinkedMap();

    @Autowired(required=false)
    public AuditV2RelationTableDaoListener(List<AuditV2RelationTableDaoHandler> handlers) {
        if (handlers != null) {
            for (AuditV2RelationTableDaoHandler handler : handlers) {
                this.handlerMap.put(handler.tableName(), handler);
            }
        }
    }

    @Override
    public void afterDelete(DaoAfterDeleteContext context) {
        if (!AuditLogHelper.auditable(AuditDatabaseOperation.UPDATE)) {
            return;
        }
        AuditV2RelationTableDaoHandler auditV2RelationTableDaoHandler = this.handlerMap.get(context.getTableName());
        if (auditV2RelationTableDaoHandler != null) {
            AuditV2RelationTableHandlerContext handlerContext = new AuditV2RelationTableHandlerContext(context.getItems());
            auditV2RelationTableDaoHandler.handleDelete(handlerContext);
        }
    }

    @Override
    public void afterInsert(DaoAfterInsertContext context) {
        if (!AuditLogHelper.auditable(AuditDatabaseOperation.UPDATE)) {
            return;
        }
        AuditV2RelationTableDaoHandler auditV2RelationTableDaoHandler = this.handlerMap.get(context.getTableName());
        if (auditV2RelationTableDaoHandler != null) {
            AuditV2RelationTableHandlerContext handlerContext = new AuditV2RelationTableHandlerContext(context.getItems());
            auditV2RelationTableDaoHandler.handleInsert(handlerContext);
        }
    }
}
