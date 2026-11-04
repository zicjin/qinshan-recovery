/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnRejectableTaskDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnRuntimeDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnTargetBean;
import com.sunwayworld.module.sys.bpmn.resource.CoreBpmnExtResource;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnExtService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogModule(value="\u5de5\u4f5c\u6d41\u7a0b\u4fe1\u606f")
@GikamBean
public class CoreBpmnExtResourceImpl
implements CoreBpmnExtResource {
    @Autowired
    private CoreBpmnExtService extService;

    @Override
    @Log(value="\u83b7\u53d6\u5de5\u4f5c\u6d41\u8fd0\u884c\u4e2d\u7684\u4fe1\u606f", type=LogType.SELECT)
    public CoreBpmnRuntimeDTO selectBpmnRuntimeInfo(@RequestParam(value="table") String table, @RequestParam(value="id") String id) {
        return this.extService.selectBpmnRuntimeInfo(table, id);
    }

    @Override
    @Log(value="\u83b7\u53d6\u5de5\u4f5c\u6d41\u8fd0\u884c\u4e2d\u53ef\u56de\u9000\u8282\u70b9\u7684\u4fe1\u606f", type=LogType.SELECT)
    public List<CoreBpmnRejectableTaskDTO> selectBpmnRuntimeRejectableTaskList(RestJsonWrapperBean wrapper) {
        return this.extService.selectBpmnRuntimeRejectableTaskList(wrapper);
    }

    @Override
    @Log(value="\u83b7\u53d6\u6d41\u7a0b\u56fe\u7684SVG", type=LogType.SELECT)
    public String selectBpmnDiagramSvg(@PathVariable(value="id") Long id, @RequestParam(name="dataTable", required=false) String dataTable, @RequestParam(name="dataId", required=false) String dataId) {
        return this.extService.selectBpmnDiagramSvg(id, dataTable, dataId);
    }

    @Override
    @Log(value="\u83b7\u53d6\u5de5\u4f5c\u6d41\u8fd0\u884c\u4e2d\u7684\u4e1a\u52a1\u4fe1\u606f", type=LogType.SELECT)
    public Page<CoreBpmnTargetBean> selectBpmnRuntimeTargetList(RestJsonWrapperBean wrapper) {
        return null;
    }
}
