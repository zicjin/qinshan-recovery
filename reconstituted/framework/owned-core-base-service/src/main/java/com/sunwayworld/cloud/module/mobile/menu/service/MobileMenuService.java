/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.menu.service;

import com.sunwayworld.cloud.module.mobile.menu.bean.MobileMenuBean;
import com.sunwayworld.cloud.module.mobile.menu.bean.MobileMenuIcon;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import java.util.ArrayList;

public interface MobileMenuService
extends GenericService<MobileMenuBean, Long>,
GenericActivatableService<MobileMenuBean, Long>,
GenericChoosableService<MobileMenuBean, Long> {
    public ArrayList<MobileMenuIcon> selectIconList();
}
