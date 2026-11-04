/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.code.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.dao.CoreCodeDao;
import com.sunwayworld.module.sys.code.service.CoreCodeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreCodeServiceImpl
implements CoreCodeService {
    @Autowired
    private CoreCodeDao codeDao;

    @Override
    public CoreCodeDao getDao() {
        return this.codeDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreCodeBean code = jsonWrapper.parseUnique(CoreCodeBean.class);
        code.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(code);
        return code.getId();
    }

    @Override
    public Page<CoreCodeBean> selectChoosablePagination(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return this.selectPagination(parameter, rowBounds);
    }

    @Override
    public List<CoreCodeBean> selectSelectableList() {
        List<CoreCodeBean> codeList = CoreCodeService.super.selectSelectableList();
        codeList.forEach(e -> e.setCodeName(StringUtils.isEmpty(e.getI18nCode()) ? e.getCodeName() : e.getI18nCode()));
        return codeList;
    }
}
