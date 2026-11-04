/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.auditable.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnTaskDTO;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnTaskStatusDTO;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.domain.Auditable;
import java.io.Serializable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface GenericAuditableResource<S extends GenericAuditableService<T, ID>, T extends Auditable<ID>, ID extends Serializable> {
    public S getService();

    @Log(value="\u5ba1\u6838\u9875\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/queries/tasks"}, method={RequestMethod.POST})
    default public Page<T> selectAuditablePagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectAuditablePagination(wrapper);
    }

    @Log(value="\u64a4\u56de\u9875\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/queries/withdrawable"}, method={RequestMethod.POST})
    default public Page<T> selectWithdrawablePagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectWithdrawablePagination(wrapper);
    }

    @Log(value="\u56de\u9000\u9875\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/queries/undoable"}, method={RequestMethod.POST})
    default public Page<T> selectUndoablePagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectUndoablePagination(wrapper);
    }

    @Log(value="\u5de5\u4f5c\u6d41\u542f\u52a8", type=LogType.PROCESS)
    @RequestMapping(value={"/action/start-process"}, method={RequestMethod.PUT})
    default public void startProcess(RestJsonWrapperBean wrapper) {
        this.getService().startProcess(wrapper);
    }

    @Log(value="\u5de5\u4f5c\u6d41\u542f\u52a8\uff08\u8fdb\u5ea6\u76d1\u63a7\uff09", type=LogType.PROCESS)
    @RequestMapping(value={"/action/start-process-with-progress"}, method={RequestMethod.PUT})
    default public String startProcessWithProgress(RestJsonWrapperBean wrapper) {
        return this.getService().startProcessWithProgress(wrapper);
    }

    @Log(value="\u5de5\u4f5c\u6d41\u64a4\u56de", type=LogType.PROCESS)
    @RequestMapping(value={"/action/withdraw-process"}, method={RequestMethod.PUT})
    default public void withdrawProcess(RestJsonWrapperBean wrapper) {
        this.getService().withdrawProcess(wrapper);
    }

    @Log(value="\u5de5\u4f5c\u6d41\u64a4\u56de\uff08\u8fdb\u5ea6\u76d1\u63a7\uff09", type=LogType.PROCESS)
    @RequestMapping(value={"/action/withdraw-process-with-progress"}, method={RequestMethod.PUT})
    default public String withdrawProcessWithProgress(RestJsonWrapperBean wrapper) {
        return this.getService().withdrawProcessWithProgress(wrapper);
    }

    @Log(value="\u5de5\u4f5c\u6d41\u5ba1\u6838\u901a\u8fc7", type=LogType.PROCESS)
    @RequestMapping(value={"/action/complete-task"}, method={RequestMethod.PUT})
    default public void completeTask(RestJsonWrapperBean wrapper) {
        this.getService().completeTask(wrapper);
    }

    @Log(value="\u5de5\u4f5c\u6d41\u5ba1\u6838\u901a\u8fc7\uff08\u8fdb\u5ea6\u76d1\u63a7\uff09", type=LogType.PROCESS)
    @RequestMapping(value={"/action/complete-task-with-progress"}, method={RequestMethod.PUT})
    default public String completeTaskWithProgress(RestJsonWrapperBean wrapper) {
        return this.getService().completeTaskWithProgress(wrapper);
    }

    @Log(value="\u5de5\u4f5c\u6d41\u8f6c\u529e", type=LogType.PROCESS)
    @RequestMapping(value={"/action/transfer-task"}, method={RequestMethod.PUT})
    default public void transferTask(RestJsonWrapperBean wrapper) {
        this.getService().transferTask(wrapper);
    }

    @Log(value="\u5de5\u4f5c\u6d41\u8f6c\u529e\uff08\u8fdb\u5ea6\u76d1\u63a7", type=LogType.PROCESS)
    @RequestMapping(value={"/action/transfer-task-with-progress"}, method={RequestMethod.PUT})
    default public String transferTaskWithProgress(RestJsonWrapperBean wrapper) {
        return this.getService().transferTaskWithProgress(wrapper);
    }

    @Log(value="\u5de5\u4f5c\u6d41\u7ed3\u675f", type=LogType.PROCESS)
    @RequestMapping(value={"/action/end-task"}, method={RequestMethod.PUT})
    default public void endTask(RestJsonWrapperBean wrapper) {
        this.getService().endTask(wrapper);
    }

    @Log(value="\u5de5\u4f5c\u6d41\u7ed3\u675f\uff08\u8fdb\u5ea6\u76d1\u63a7\uff09", type=LogType.PROCESS)
    @RequestMapping(value={"/action/end-task-with-progress"}, method={RequestMethod.PUT})
    default public String endTaskWithProgress(RestJsonWrapperBean wrapper) {
        return this.getService().endTaskWithProgress(wrapper);
    }

    @Log(value="\u83b7\u53d6\u5de5\u4f5c\u6d41\u4efb\u52a1\u8282\u70b9\u7684\u72b6\u6001", type=LogType.PROCESS)
    @RequestMapping(value={"/bpmn-task-status"}, method={RequestMethod.POST})
    default public CoreBpmnTaskStatusDTO selectBpmnTaskStatus(RestJsonWrapperBean wrapper) {
        return this.getService().selectBpmnTaskStatus(wrapper);
    }

    @Log(value="\u5de5\u4f5c\u6d41\u5ba1\u6838\u62d2\u7edd", type=LogType.PROCESS)
    @RequestMapping(value={"/action/reject-task"}, method={RequestMethod.PUT})
    default public void rejectTask(RestJsonWrapperBean wrapper) {
        this.getService().rejectTask(wrapper);
    }

    @Log(value="\u5de5\u4f5c\u6d41\u5ba1\u6838\u62d2\u7edd\uff08\u8fdb\u5ea6\u76d1\u63a7\uff09", type=LogType.PROCESS)
    @RequestMapping(value={"/action/reject-task-with-progress"}, method={RequestMethod.PUT})
    default public String rejectTaskWithProgress(RestJsonWrapperBean wrapper) {
        return this.getService().rejectTaskWithProgress(wrapper);
    }

    @Log(value="\u5de5\u4f5c\u6d41\u5f02\u5e38\u62d2\u7edd", type=LogType.PROCESS)
    @RequestMapping(value={"/action/odd-reject-task"}, method={RequestMethod.PUT})
    default public void oddRejectTask(RestJsonWrapperBean wrapper) {
        this.getService().oddRejectTask(wrapper);
    }

    @Log(value="\u5de5\u4f5c\u6d41\u5f02\u5e38\u62d2\u7edd\uff08\u8fdb\u5ea6\u76d1\u63a7\uff09", type=LogType.PROCESS)
    @RequestMapping(value={"/action/odd-reject-task-with-progress"}, method={RequestMethod.PUT})
    default public String oddRejectTaskWithProgress(RestJsonWrapperBean wrapper) {
        return this.getService().oddRejectTaskWithProgress(wrapper);
    }

    @Log(value="\u5de5\u4f5c\u6d41\u56de\u9000", type=LogType.PROCESS)
    @RequestMapping(value={"/action/undo"}, method={RequestMethod.PUT})
    default public void undo(RestJsonWrapperBean wrapper) {
        this.getService().undo(wrapper);
    }

    @Log(value="\u5de5\u4f5c\u6d41\u56de\u9000\uff08\u8fdb\u5ea6\u76d1\u63a7\uff09", type=LogType.PROCESS)
    @RequestMapping(value={"/action/undo-with-progress"}, method={RequestMethod.PUT})
    default public String undoWithProgress(RestJsonWrapperBean wrapper) {
        return this.getService().undoWithProgress(wrapper);
    }

    @Log(value="\u83b7\u53d6\u5de5\u4f5c\u6d41\u524d\u4e00\u4e2a\u8282\u70b9\u4efb\u52a1", type=LogType.PROCESS)
    @RequestMapping(value={"/{id}/previous-tasks"}, method={RequestMethod.GET})
    default public CoreBpmnTaskDTO getPreviousTask(@PathVariable(value="id") ID id) {
        return this.getService().getPreviousTask(id);
    }
}
