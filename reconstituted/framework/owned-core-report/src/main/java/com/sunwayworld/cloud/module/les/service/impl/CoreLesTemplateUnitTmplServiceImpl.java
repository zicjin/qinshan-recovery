/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.service.impl;

import com.sunwayworld.cloud.module.les.bean.CoreLesInstanceUnitBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateUnitBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateUnitTmplBean;
import com.sunwayworld.cloud.module.les.persistent.dao.CoreLesTemplateUnitTmplDao;
import com.sunwayworld.cloud.module.les.service.CoreLesInstanceUnitService;
import com.sunwayworld.cloud.module.les.service.CoreLesTemplateUnitService;
import com.sunwayworld.cloud.module.les.service.CoreLesTemplateUnitTmplService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreLesTemplateUnitTmplServiceImpl
implements CoreLesTemplateUnitTmplService {
    @Autowired
    private CoreLesTemplateUnitTmplDao unitTmplDao;
    @Autowired
    private CoreLesTemplateUnitService templateUnitService;
    @Autowired
    private CoreLesInstanceUnitService instanceUnitService;

    @Override
    public CoreLesTemplateUnitTmplDao getDao() {
        return this.unitTmplDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public void insertByTemplateUnit(Long unitId, RestJsonWrapperBean wrapper) {
        CoreLesTemplateUnitBean unit = (CoreLesTemplateUnitBean)this.templateUnitService.selectById(unitId);
        CoreLesTemplateUnitBean newUnit = wrapper.parseUnique(CoreLesTemplateUnitBean.class);
        CoreLesTemplateUnitTmplBean unitTmpl = new CoreLesTemplateUnitTmplBean();
        unitTmpl.setId(ApplicationContextHelper.getNextIdentity());
        unitTmpl.setUnitCategory(unit.getUnitCategory());
        unitTmpl.setUnitTitle(newUnit.getUnitTitle());
        unitTmpl.setUnitBody(unit.getUnitBody());
        unitTmpl.setConfig(unit.getConfig());
        this.getDao().insert(unitTmpl);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public void insertByInstanceUnit(Long unitId, RestJsonWrapperBean wrapper) {
        CoreLesInstanceUnitBean unit = (CoreLesInstanceUnitBean)this.instanceUnitService.selectById(unitId);
        CoreLesInstanceUnitBean newUnit = wrapper.parseUnique(CoreLesInstanceUnitBean.class);
        CoreLesTemplateUnitTmplBean unitTmpl = new CoreLesTemplateUnitTmplBean();
        unitTmpl.setId(ApplicationContextHelper.getNextIdentity());
        unitTmpl.setUnitCategory(unit.getUnitCategory());
        unitTmpl.setUnitTitle(newUnit.getUnitTitle());
        unitTmpl.setUnitBody(unit.getUnitBody());
        unitTmpl.setConfig(unit.getConfig());
        this.getDao().insert(unitTmpl);
    }
}
