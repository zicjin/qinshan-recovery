package com.sunwayworld.emc.module.mdm.basemdm.qccategory.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean.EmcQcCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.dao.EmcQcCategoryDao;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.service.EmcQcCategoryService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.exception.core.ApplicationWarningException;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@GikamBean
@Repository
public class EmcQcCategoryServiceImpl implements EmcQcCategoryService {

    @Autowired
    private EmcQcCategoryDao qcCategoryDao;

    @SuppressWarnings("unchecked")
    @Override
    public EmcQcCategoryDao getDao() {
        return qcCategoryDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        EmcQcCategoryBean qcCategory = wrapper.parseUnique(EmcQcCategoryBean.class);
        validateCodeLength(qcCategory);
        qcCategory.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(qcCategory);

        return qcCategory.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    @Audit("AUDIT.SAVE")
    public void updateIfChanged(EmcQcCategoryBean item) {
        validateCodeLength(item);
        getDao().updateIfChanged(item);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    @Audit("AUDIT.SAVE")
    public void updateIfChanged(List<EmcQcCategoryBean> items) {
        // Validate the complete inline-save batch before the first database write.
        items.forEach(this::validateCodeLength);
        getDao().updateIfChanged(items);
    }

    private void validateCodeLength(EmcQcCategoryBean item) {
        String code = item.getQcCategoryId();
        // Original Oracle QCCATEGORYID is VARCHAR2(16 BYTE), AL32UTF8.
        // Null remains optional, including partial updates that omit this field.
        if (code != null && code.getBytes(StandardCharsets.UTF_8).length > 16) {
            throw new ApplicationWarningException("EMC.QC_CATEGORY.CODE_MAX_BYTES");
        }
    }
}
