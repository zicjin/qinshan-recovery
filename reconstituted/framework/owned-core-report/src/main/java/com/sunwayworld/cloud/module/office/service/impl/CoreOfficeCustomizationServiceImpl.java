/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.service.impl;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeCustomizationBean;
import com.sunwayworld.cloud.module.office.persistent.dao.CoreOfficeCustomizationDao;
import com.sunwayworld.cloud.module.office.service.CoreOfficeCustomizationService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreOfficeCustomizationServiceImpl
implements CoreOfficeCustomizationService {
    @Autowired
    private CoreOfficeCustomizationDao coreOfficeCustomizationDao;

    @Override
    public CoreOfficeCustomizationDao getDao() {
        return this.coreOfficeCustomizationDao;
    }

    @Override
    @Cacheable(value={"OFFICE-CUSTOMIZATION"}, key="'OFFICE'")
    public CoreOfficeCustomizationBean selectOfficeCustomization() {
        CoreOfficeCustomizationBean customization = (CoreOfficeCustomizationBean)this.selectById("officeCustomization");
        if (StringUtils.isBlank(customization.getLogo())) {
            customization.setLogo("/static/office/logo/officeLogo.svg");
        }
        if (StringUtils.isBlank(customization.getAboutLogo())) {
            customization.setAboutLogo("/static/office/logo/officeAboutLogo.svg");
        }
        return customization;
    }
}
