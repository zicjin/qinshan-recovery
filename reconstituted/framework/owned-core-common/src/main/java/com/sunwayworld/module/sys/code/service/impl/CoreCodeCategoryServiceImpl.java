/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.code.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.config.bean.CoreAdminSelectConfigBean;
import com.sunwayworld.module.admin.config.service.CoreAdminSelectConfigService;
import com.sunwayworld.module.sys.code.bean.CoreCodeCategoryBean;
import com.sunwayworld.module.sys.code.dao.CoreCodeCategoryDao;
import com.sunwayworld.module.sys.code.service.CoreCodeCategoryService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreCodeCategoryServiceImpl
implements CoreCodeCategoryService {
    @Autowired
    private CoreCodeCategoryDao codeCategoryDao;
    @Autowired
    private CoreAdminSelectConfigService adminSelectConfigService;

    @Override
    public CoreCodeCategoryDao getDao() {
        return this.codeCategoryDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public String insert(RestJsonWrapperBean wrapper) {
        CoreCodeCategoryBean codeCategory = wrapper.parseUnique(CoreCodeCategoryBean.class);
        codeCategory.setCodeClassId("sys");
        String id = codeCategory.getId();
        this.getDao().insert(codeCategory);
        this.adminSelectConfigService.insertByCodeCategory(codeCategory);
        return id;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    @Audit(value="AUDIT.DELETE")
    public void delete(RestJsonWrapperBean wrapper) {
        List<String> idList = wrapper.parseId(String.class);
        ArrayList itemList = new ArrayList();
        idList.stream().forEach(e -> {
            CoreAdminSelectConfigBean item = new CoreAdminSelectConfigBean();
            item.setCategory((String)e);
            itemList.add(item);
        });
        this.adminSelectConfigService.getDao().deleteBy(itemList, new String[]{"CATEGORY"});
        this.getDao().deleteByIdList(idList);
    }
}
