/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.auditable.resource;

import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnTaskStatusDTO;
import com.sunwayworld.framework.support.domain.Auditable;
import java.io.Serializable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface GenericAuditableCloudResource<T extends Auditable<ID>, ID extends Serializable> {
    @RequestMapping(value={"/queries/tasks"}, method={RequestMethod.POST})
    public Pagination<T> selectAuditablePagination(RestJsonWrapperBean var1);

    @RequestMapping(value={"/queries/undoable"}, method={RequestMethod.POST})
    public Pagination<T> selectUndoablePagination(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/start-process"}, method={RequestMethod.PUT})
    public void startProcess(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/start-process-with-progress"}, method={RequestMethod.PUT})
    public String startProcessWithProgress(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/withdraw-process"}, method={RequestMethod.PUT})
    public void withdrawProcess(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/withdraw-process-with-progress"}, method={RequestMethod.PUT})
    public String withdrawProcessWithProgress(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/complete-task"}, method={RequestMethod.PUT})
    public void completeTask(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/complete-task-with-progress"}, method={RequestMethod.PUT})
    public String completeTaskWithProgress(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/transfer-task"}, method={RequestMethod.PUT})
    public void transferTask(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/transfer-task-with-progress"}, method={RequestMethod.PUT})
    public String transferTaskWithProgress(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/end-task"}, method={RequestMethod.PUT})
    public void endTask(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/end-task-with-progress"}, method={RequestMethod.PUT})
    public String endTaskWithProgress(RestJsonWrapperBean var1);

    @RequestMapping(value={"/bpmn-task-status"}, method={RequestMethod.POST})
    public CoreBpmnTaskStatusDTO selectBpmnTaskStatus(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/reject-task"}, method={RequestMethod.PUT})
    public void rejectTask(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/reject-task-with-progress"}, method={RequestMethod.PUT})
    public String rejectTaskWithProgress(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/odd-reject-task"}, method={RequestMethod.PUT})
    public void oddRejectTask(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/odd-reject-task-with-progress"}, method={RequestMethod.PUT})
    public String oddRejectTaskWithProgress(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/undo"}, method={RequestMethod.PUT})
    public void undo(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/undo-with-progress"}, method={RequestMethod.PUT})
    public String undoWithProgress(RestJsonWrapperBean var1);
}
