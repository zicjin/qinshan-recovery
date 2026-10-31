// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.item.admarea.resource;

import com.sunwayworld.emc.module.item.admarea.bean.EmcAdmAreaBean;
import com.sunwayworld.emc.module.item.admarea.service.EmcAdmAreaService;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/emc/module/item/adm-areas"})
public interface EmcAdmAreaResource
extends GenericResource<EmcAdmAreaService, EmcAdmAreaBean, String>,
GenericSelectableResource<EmcAdmAreaService, EmcAdmAreaBean, String> {
}
