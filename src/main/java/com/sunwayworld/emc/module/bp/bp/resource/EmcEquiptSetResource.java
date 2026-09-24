// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.bp.resource;

import com.sunwayworld.emc.module.bp.bp.bean.EmcEquiptSetBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcEquiptSetService;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/emc/module/bp/equipt-sets"})
public interface EmcEquiptSetResource
extends GenericResource<EmcEquiptSetService, EmcEquiptSetBean, Long>,
GenericChoosableResource<EmcEquiptSetService, EmcEquiptSetBean, Long> {
}
