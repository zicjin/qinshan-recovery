// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.mdm.oa.resource;

import com.sunwayworld.emc.module.mdm.oa.bean.EmcOaAreaBean;
import com.sunwayworld.emc.module.mdm.oa.service.EmcOaAreaService;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/emc/module/mdm/oa-areas"})
public interface EmcOaAreaResource
extends GenericResource<EmcOaAreaService, EmcOaAreaBean, Long> {
}
