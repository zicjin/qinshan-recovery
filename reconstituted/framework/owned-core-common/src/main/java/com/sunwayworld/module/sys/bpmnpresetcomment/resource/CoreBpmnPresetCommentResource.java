/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmnpresetcomment.resource;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;
import com.sunwayworld.module.sys.bpmnpresetcomment.bean.CoreBpmnPresetCommentBean;
import com.sunwayworld.module.sys.bpmnpresetcomment.service.CoreBpmnPresetCommentService;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/bpmn/preset-comments"})
public interface CoreBpmnPresetCommentResource
extends GenericResource<CoreBpmnPresetCommentService, CoreBpmnPresetCommentBean, Long>,
GenericSelectableResource<CoreBpmnPresetCommentService, CoreBpmnPresetCommentBean, Long> {
}
