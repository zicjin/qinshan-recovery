/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.diagram.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.diagram.bean.CoreDiagramBean;
import com.sunwayworld.module.item.diagram.dao.CoreDiagramDao;
import com.sunwayworld.module.item.diagram.service.CoreDiagramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreDiagramServiceImpl
implements CoreDiagramService {
    @Autowired
    private CoreDiagramDao diagramDao;

    @Override
    public CoreDiagramDao getDao() {
        return this.diagramDao;
    }

    @Override
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public <ID> void insert(String table, ID itemId, String diagram) {
        CoreDiagramBean diagramBean = new CoreDiagramBean();
        diagramBean.setId(ApplicationContextHelper.getNextIdentity());
        diagramBean.setTargetId(table + "$" + itemId);
        diagramBean.setDiagram(diagram);
        this.getDao().insert(diagramBean);
    }

    @Override
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    @Audit(value="AUDIT.SAVE")
    public <ID> void save(String table, ID itemId, String diagram) {
        CoreDiagramBean searchItem = new CoreDiagramBean();
        searchItem.setTargetId(table + "$" + itemId);
        CoreDiagramBean updateItem = new CoreDiagramBean();
        updateItem.setDiagram(diagram);
        this.getDao().update(updateItem, searchItem);
    }

    @Override
    public CoreDiagramBean selectOne(Long id) {
        return (CoreDiagramBean)this.getDao().selectById(id);
    }

    @Override
    public <ID> CoreDiagramBean selectOne(String table, ID itemId) {
        CoreDiagramBean condition = new CoreDiagramBean();
        condition.setTargetId(table + "$" + itemId);
        return this.getDao().selectOne(condition, new String[0]);
    }
}
