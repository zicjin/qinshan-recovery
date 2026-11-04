/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.controller;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/bpmn-procs/page"})
public interface CoreBpmnProcController
extends GenericSearchListPageController,
GenericDetailPageController,
GenericChoosePageController {
}
