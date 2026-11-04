/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnRejectableTaskDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnRuntimeDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnTargetBean;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(value={"/secure/core/module/sys/bpmn-exts"})
public interface CoreBpmnExtResource {
    @RequestMapping(value={"/runtime-info"}, method={RequestMethod.GET})
    public CoreBpmnRuntimeDTO selectBpmnRuntimeInfo(@RequestParam(value="table") String var1, @RequestParam(value="id") String var2);

    @RequestMapping(value={"/options/rejectable-tasks"}, method={RequestMethod.POST})
    public List<CoreBpmnRejectableTaskDTO> selectBpmnRuntimeRejectableTaskList(RestJsonWrapperBean var1);

    @RequestMapping(value={"/bpmn-diagrams/{id}/svg"}, method={RequestMethod.GET})
    public String selectBpmnDiagramSvg(@PathVariable(value="id") Long var1, @RequestParam(name="dataTable", required=false) String var2, @RequestParam(name="dataId", required=false) String var3);

    @RequestMapping(value={"/runtime-targets"}, method={RequestMethod.POST})
    public Page<CoreBpmnTargetBean> selectBpmnRuntimeTargetList(RestJsonWrapperBean var1);
}
