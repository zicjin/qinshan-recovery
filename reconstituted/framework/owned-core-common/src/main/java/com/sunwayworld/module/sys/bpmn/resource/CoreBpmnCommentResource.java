/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnCommentDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(value={"/secure/core/module/sys/bpmn-comments"})
public interface CoreBpmnCommentResource {
    @RequestMapping(value={"/queries"}, method={RequestMethod.POST})
    public Page<CoreBpmnCommentDTO> selectCommentList(RestJsonWrapperBean var1, @RequestParam String var2, @RequestParam String var3);
}
