/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.auditable.resource;

import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnTaskStatusDTO;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableCloudResource;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.domain.Auditable;
import java.io.Serializable;

public interface AbstractGenericAuditableResource<S extends GenericAuditableService<T, ID>, T extends Auditable<ID>, ID extends Serializable>
extends GenericAuditableCloudResource<T, ID> {
    public S getService();

    @Override
    @Log(value="\u5ba1\u6838\u9875\u67e5\u8be2", type=LogType.SELECT)
    default public Pagination<T> selectAuditablePagination(RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectAuditablePagination(wrapper);
    }

    @Override
    @Log(value="\u56de\u9000\u9875\u67e5\u8be2", type=LogType.SELECT)
    default public Pagination<T> selectUndoablePagination(RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectUndoablePagination(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u542f\u52a8", type=LogType.PROCESS)
    default public void startProcess(RestJsonWrapperBean wrapper) {
        this.getService().startProcess(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u542f\u52a8\uff08\u8fdb\u5ea6\u76d1\u63a7\uff09", type=LogType.PROCESS)
    default public String startProcessWithProgress(RestJsonWrapperBean wrapper) {
        return this.getService().startProcessWithProgress(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u64a4\u56de", type=LogType.PROCESS)
    default public void withdrawProcess(RestJsonWrapperBean wrapper) {
        this.getService().withdrawProcess(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u64a4\u56de\uff08\u8fdb\u5ea6\u76d1\u63a7\uff09", type=LogType.PROCESS)
    default public String withdrawProcessWithProgress(RestJsonWrapperBean wrapper) {
        return this.getService().withdrawProcessWithProgress(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u5ba1\u6838\u901a\u8fc7", type=LogType.PROCESS)
    default public void completeTask(RestJsonWrapperBean wrapper) {
        this.getService().completeTask(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u5ba1\u6838\u901a\u8fc7\uff08\u8fdb\u5ea6\u76d1\u63a7\uff09", type=LogType.PROCESS)
    default public String completeTaskWithProgress(RestJsonWrapperBean wrapper) {
        return this.getService().completeTaskWithProgress(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u8f6c\u529e", type=LogType.PROCESS)
    default public void transferTask(RestJsonWrapperBean wrapper) {
        this.getService().transferTask(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u8f6c\u529e\uff08\u8fdb\u5ea6\u76d1\u63a7\uff09", type=LogType.PROCESS)
    default public String transferTaskWithProgress(RestJsonWrapperBean wrapper) {
        return this.getService().transferTaskWithProgress(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u7ed3\u675f", type=LogType.PROCESS)
    default public void endTask(RestJsonWrapperBean wrapper) {
        this.getService().endTask(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u7ed3\u675f\uff08\u8fdb\u5ea6\u76d1\u63a7\uff09", type=LogType.PROCESS)
    default public String endTaskWithProgress(RestJsonWrapperBean wrapper) {
        return this.getService().endTaskWithProgress(wrapper);
    }

    @Override
    @Log(value="\u83b7\u53d6\u5de5\u4f5c\u6d41\u4efb\u52a1\u8282\u70b9\u7684\u72b6\u6001\u9700\u8981\u8ba4\u8bc1", type=LogType.PROCESS)
    default public CoreBpmnTaskStatusDTO selectBpmnTaskStatus(RestJsonWrapperBean wrapper) {
        return this.getService().selectBpmnTaskStatus(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u5ba1\u6838\u62d2\u7edd", type=LogType.PROCESS)
    default public void rejectTask(RestJsonWrapperBean wrapper) {
        this.getService().rejectTask(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u5ba1\u6838\u62d2\u7edd\uff08\u8fdb\u5ea6\u76d1\u63a7\uff09", type=LogType.PROCESS)
    default public String rejectTaskWithProgress(RestJsonWrapperBean wrapper) {
        return this.getService().rejectTaskWithProgress(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u5f02\u5e38\u62d2\u7edd", type=LogType.PROCESS)
    default public void oddRejectTask(RestJsonWrapperBean wrapper) {
        this.getService().oddRejectTask(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u5f02\u5e38\u62d2\u7edd\uff08\u8fdb\u5ea6\u76d1\u63a7\uff09", type=LogType.PROCESS)
    default public String oddRejectTaskWithProgress(RestJsonWrapperBean wrapper) {
        return this.getService().oddRejectTaskWithProgress(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u56de\u9000", type=LogType.PROCESS)
    default public void undo(RestJsonWrapperBean wrapper) {
        this.getService().undo(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u56de\u9000\uff08\u8fdb\u5ea6\u76d1\u63a7\uff09", type=LogType.PROCESS)
    default public String undoWithProgress(RestJsonWrapperBean wrapper) {
        return this.getService().undoWithProgress(wrapper);
    }
}
