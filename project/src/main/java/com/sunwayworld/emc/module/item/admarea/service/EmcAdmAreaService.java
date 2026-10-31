// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.item.admarea.service;

import com.sunwayworld.emc.module.item.admarea.bean.EmcAdmAreaBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

public interface EmcAdmAreaService
extends GenericService<EmcAdmAreaBean, String>,
GenericSelectableService<EmcAdmAreaBean, String> {
}
