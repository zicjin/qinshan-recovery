// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.bp.service;

import com.sunwayworld.emc.module.bp.bp.bean.EmcEquiptSwSetBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface EmcEquiptSwSetService
extends GenericService<EmcEquiptSwSetBean, Long>,
GenericChoosableService<EmcEquiptSwSetBean, Long> {
}
