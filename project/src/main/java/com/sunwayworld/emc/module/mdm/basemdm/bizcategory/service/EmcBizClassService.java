// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.mdm.basemdm.bizcategory.service;

import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean.EmcBizClassBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

public interface EmcBizClassService
extends GenericService<EmcBizClassBean, Long>,
GenericSelectableService<EmcBizClassBean, Long>,
GenericChoosableService<EmcBizClassBean, Long> {
}
