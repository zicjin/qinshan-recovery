/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnRejectableTaskDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnRuntimeDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnTargetBean;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Nullable;

public interface CoreBpmnExtService {
    public <T extends Persistable<ID>, ID extends Serializable> CoreBpmnRuntimeDTO selectBpmnRuntimeInfo(String var1, String var2);

    public List<CoreBpmnRejectableTaskDTO> selectBpmnRuntimeRejectableTaskList(RestJsonWrapperBean var1);

    public String selectBpmnDiagramSvg(Long var1, @Nullable String var2, @Nullable String var3);

    public Page<CoreBpmnTargetBean> selectBpmnRuntimeTargetList(RestJsonWrapperBean var1);
}
