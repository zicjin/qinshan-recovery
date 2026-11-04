/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filetype.service;

import com.sunwayworld.cloud.module.qms.filetype.bean.CoreFileCategoryBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

public interface CoreFileCategoryService
extends GenericService<CoreFileCategoryBean, Long>,
GenericSelectableService<CoreFileCategoryBean, Long>,
GenericActivatableService<CoreFileCategoryBean, Long> {
}
