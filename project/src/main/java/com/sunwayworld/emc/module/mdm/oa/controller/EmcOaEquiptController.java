// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.mdm.oa.controller;

import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/emc/module/mdm/oa-equipts/page"})
public interface EmcOaEquiptController
extends GenericEditListPageController {
}
