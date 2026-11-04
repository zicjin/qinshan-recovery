/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.menu.service;

import com.sunwayworld.cloud.module.mobile.menu.bean.MobilePageBean;
import com.sunwayworld.cloud.module.mobile.menu.bean.MobilePageRepresentative;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface MobilePageService
extends GenericService<MobilePageBean, Long>,
GenericChoosableService<MobilePageBean, Long> {
    public void insertSameLevelPage(Long var1, RestJsonWrapperBean var2);

    public void insertLowerLevelPage(Long var1, RestJsonWrapperBean var2);

    public void deletePage(Long var1, RestJsonWrapperBean var2);

    public Page<MobilePageRepresentative> selectMobilePageTreeList(String var1, Long var2, RestJsonWrapperBean var3);

    public void switchPageColumn(Long var1, Long var2);
}
