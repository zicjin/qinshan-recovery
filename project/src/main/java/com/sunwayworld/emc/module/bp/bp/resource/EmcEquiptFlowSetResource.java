// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.bp.resource;

import com.sunwayworld.emc.module.bp.bp.bean.EmcEquiptFlowSetBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcEquiptFlowSetService;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/emc/module/bp/equipt-flow-sets"})
public interface EmcEquiptFlowSetResource
extends GenericResource<EmcEquiptFlowSetService, EmcEquiptFlowSetBean, Long> {
}
