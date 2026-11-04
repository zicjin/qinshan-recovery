/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.service.impl;

import com.sunwayworld.cloud.module.les.bean.CoreLesUnitBean;
import com.sunwayworld.cloud.module.les.persistent.dao.CoreLesUnitDao;
import com.sunwayworld.cloud.module.les.service.CoreLesUnitService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreLesUnitServiceImpl
implements CoreLesUnitService {
    @Autowired
    private CoreLesUnitDao coreLesUnitDao;

    @Override
    public CoreLesUnitDao getDao() {
        return this.coreLesUnitDao;
    }

    @Override
    public Page<CoreLesUnitBean> selectChoosablePagination(RestJsonWrapperBean wrapper) {
        Page<CoreLesUnitBean> coreLesUnitBeanPage = CoreLesUnitService.super.selectChoosablePagination(wrapper);
        coreLesUnitBeanPage.getRows().forEach(row -> {
            String message;
            if (!StringUtils.isBlank(row.getI18nCode()) && !StringUtils.isBlank(message = I18nHelper.getMessage(row.getI18nCode(), new String[0]))) {
                row.setUnitName(message);
            }
        });
        return coreLesUnitBeanPage;
    }
}
