/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.engine;

import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceNextTaskElementDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnProcBean;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import com.sunwayworld.module.sys.bpmn.engine.CoreBpmnRuntimeSource;
import com.sunwayworld.module.sys.bpmn.engine.cache.BpmnRuntimeData;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;

public interface CoreBpmnRuntimeService {
    default public <T extends Auditable<ID>, ID extends Serializable> ProcessStatus startProcess(CoreBpmnRuntimeSource<T, ID> source) {
        return this.startProcess(Arrays.asList(source)).get(0).getProcessStatus();
    }

    public <T extends Auditable<ID>, ID extends Serializable> List<CoreBpmnInstanceStatusDTO<ID>> startProcess(List<CoreBpmnRuntimeSource<T, ID>> var1);

    public <T extends Auditable<ID>, ID extends Serializable> List<CoreBpmnInstanceStatusDTO<ID>> withdrawProcess(List<CoreBpmnRuntimeSource<T, ID>> var1);

    public <T extends Auditable<ID>, ID extends Serializable> List<CoreBpmnInstanceStatusDTO<ID>> completeTask(List<CoreBpmnRuntimeSource<T, ID>> var1);

    public <T extends Auditable<ID>, ID extends Serializable> void transferTask(List<CoreBpmnRuntimeSource<T, ID>> var1);

    public <T extends Auditable<ID>, ID extends Serializable> List<CoreBpmnInstanceStatusDTO<ID>> endTask(List<CoreBpmnRuntimeSource<T, ID>> var1);

    public <T extends Auditable<ID>, ID extends Serializable> List<CoreBpmnInstanceStatusDTO<ID>> undo(List<CoreBpmnRuntimeSource<T, ID>> var1);

    public <T extends Auditable<ID>, ID extends Serializable> List<CoreBpmnInstanceStatusDTO<ID>> rejectTask(List<CoreBpmnRuntimeSource<T, ID>> var1);

    public <T extends Auditable<ID>, ID extends Serializable> List<CoreBpmnInstanceStatusDTO<ID>> oddRejectTask(List<CoreBpmnRuntimeSource<T, ID>> var1);

    public <ID extends Serializable> List<CoreBpmnInstanceTaskBean> selectAuditableInstanceTaskList(String var1, ID var2, @Nullable String var3);

    public <T extends Auditable<ID>, ID extends Serializable> List<CoreBpmnInstanceNextTaskElementDTO<ID>> getNextTaskList(List<CoreBpmnRuntimeSource<T, ID>> var1);

    public <T extends Auditable<ID>, ID extends Serializable> Map<ID, CoreBpmnProcBean> selectRuntimeBpmnProcList(List<T> var1);

    public void processData(BpmnRuntimeData.BpmnProcessData var1);

    public CoreBpmnProcBean getLatestBpmnProc(String var1);
}
