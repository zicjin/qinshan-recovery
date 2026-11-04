/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filemanage.resource.impl;

import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManageAuthBean;
import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManageBean;
import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManagePrintRecorBean;
import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManageReadRecordBean;
import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManageVersionBean;
import com.sunwayworld.cloud.module.qms.filemanage.resource.CoreFileManageResource;
import com.sunwayworld.cloud.module.qms.filemanage.service.CoreFileManageService;
import com.sunwayworld.cloud.module.qms.filemanage.validator.CoreFileManageDeleteValidator;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnTaskStatusDTO;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u6587\u4ef6\u7ba1\u7406")
@RestController
@GikamBean
public class CoreFileManageResourceImpl
implements CoreFileManageResource,
AbstractGenericResource<CoreFileManageService, CoreFileManageBean, Long> {
    @Autowired
    private CoreFileManageService coreFileManageService;

    @Override
    public CoreFileManageService getService() {
        return this.coreFileManageService;
    }

    @Override
    @Log(value="\u6587\u4ef6\u7ba1\u7406\u5ba1\u6838\u9875\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/queries/tasks"}, method={RequestMethod.POST})
    public Page<CoreFileManageBean> selectAuditablePagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectAuditablePagination(wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u6587\u4ef6\u7ba1\u7406", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u6587\u4ef6\u7ba1\u7406", type=LogType.DELETE)
    @RequestMapping(method={RequestMethod.DELETE})
    @ValidateDataWith(value=CoreFileManageDeleteValidator.class)
    public void delete(RestJsonWrapperBean wrapper) {
        this.getService().delete(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u6587\u4ef6\u7c7b\u522bID\u83b7\u53d6\u6587\u4ef6\u7ba1\u7406\u7f16\u7801", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/fileno"}, method={RequestMethod.GET})
    public String getFileNo(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().getFileNo(id, wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u6587\u4ef6\u7ba1\u7406ID\u67e5\u8be2\u6587\u4ef6\u7ba1\u7406-\u7248\u672c", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/manage-versions/queries"}, method={RequestMethod.POST})
    public Page<CoreFileManageVersionBean> selectManageVersionPaginationByFileManageId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectManageVersionPaginationByFileManageId(id, wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u6587\u4ef6\u7ba1\u7406-\u7248\u672cID\u83b7\u53d6\u8be6\u60c5\u4fe1\u606f", type=LogType.SELECT)
    @RequestMapping(value={"/versions/{id}"}, method={RequestMethod.GET})
    public CoreFileManageVersionBean selectVersionDetail(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectVersionDetail(id, wrapper);
    }

    @Override
    @Log(value="\u9a8c\u8bc1\u6570\u636e\u552f\u4e00\u6027", type=LogType.VALIDATE)
    @RequestMapping(value={"/action/validate-unique"}, method={RequestMethod.POST})
    public RestValidationResultBean validateUnique(RestJsonWrapperBean wrapper) {
        return this.getService().validateUnique(wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u6587\u4ef6\u7ba1\u7406-\u7248\u672c", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/manage-versions"}, method={RequestMethod.POST})
    public Long insertManageVersion(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertManageVersion(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u6587\u4ef6\u7ba1\u7406-\u7248\u672c", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/manage-versions"}, method={RequestMethod.DELETE})
    public void deleteManageVersion(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteManageVersion(id, wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u6587\u4ef6\u7ba1\u7406ID\u67e5\u8be2\u6587\u4ef6\u7ba1\u7406-\u6388\u6743", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/manage-auths/queries"}, method={RequestMethod.POST})
    public Page<CoreFileManageAuthBean> selectManageAuthPaginationByFileManageId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectManageAuthPaginationByFileManageId(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u6587\u4ef6\u7ba1\u7406-\u6388\u6743", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/manage-auths"}, method={RequestMethod.POST})
    public List<Long> insertManageAuth(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertManageAuth(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u6587\u4ef6\u7ba1\u7406-\u6388\u6743", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/manage-auths"}, method={RequestMethod.DELETE})
    public void deleteManageAuth(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteManageAuth(id, wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u6587\u4ef6\u7ba1\u7406ID\u67e5\u8be2\u6587\u4ef6\u7ba1\u7406-\u67e5\u9605\u8bb0\u5f55", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/manage-read-records/queries"}, method={RequestMethod.POST})
    public Page<CoreFileManageReadRecordBean> selectManageReadRecordPaginationByFileManageId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectManageReadRecordPaginationByFileManageId(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u6587\u4ef6\u7ba1\u7406-\u67e5\u9605\u8bb0\u5f55", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/manage-read-records"}, method={RequestMethod.POST})
    public Long insertManageReadRecord(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertManageReadRecord(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u6587\u4ef6\u7ba1\u7406-\u67e5\u9605\u8bb0\u5f55", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/manage-read-records"}, method={RequestMethod.DELETE})
    public void deleteManageReadRecord(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteManageReadRecord(id, wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u6587\u4ef6\u7ba1\u7406ID\u67e5\u8be2\u6587\u4ef6\u7ba1\u7406-\u6253\u5370\u8bb0\u5f55", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/manage-print-recors/queries"}, method={RequestMethod.POST})
    public Page<CoreFileManagePrintRecorBean> selectManagePrintRecorPaginationByFileManageId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectManagePrintRecorPaginationByFileManageId(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u6587\u4ef6\u7ba1\u7406-\u6253\u5370\u8bb0\u5f55", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/manage-print-recors"}, method={RequestMethod.POST})
    public Long insertManagePrintRecor(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertManagePrintRecor(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u6587\u4ef6\u7ba1\u7406-\u6253\u5370\u8bb0\u5f55", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/manage-print-recors"}, method={RequestMethod.DELETE})
    public void deleteManagePrintRecor(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteManagePrintRecor(id, wrapper);
    }

    @Override
    @Log(value="\u6279\u91cf\u6587\u4ef6\u94fe\u63a5", type=LogType.UPDATE)
    @RequestMapping(value={"/action/batch-file-link"}, method={RequestMethod.PUT})
    public void batchFileLink(RestJsonWrapperBean wrapper) {
        this.getService().batchFileLink(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u542f\u52a8", type=LogType.PROCESS)
    @RequestMapping(value={"/action/start-process"}, method={RequestMethod.PUT})
    public void startProcess(RestJsonWrapperBean wrapper) {
        this.getService().startProcess(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u64a4\u56de", type=LogType.PROCESS)
    @RequestMapping(value={"/action/withdraw-process"}, method={RequestMethod.PUT})
    public void withdrawProcess(RestJsonWrapperBean wrapper) {
        this.getService().withdrawProcess(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u5ba1\u6838\u901a\u8fc7", type=LogType.PROCESS)
    @RequestMapping(value={"/action/complete-task"}, method={RequestMethod.PUT})
    public void completeTask(RestJsonWrapperBean wrapper) {
        this.getService().completeTask(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u7ed3\u675f", type=LogType.PROCESS)
    @RequestMapping(value={"/action/end-task"}, method={RequestMethod.PUT})
    public void endTask(RestJsonWrapperBean wrapper) {
        this.getService().endTask(wrapper);
    }

    @Override
    @Log(value="\u83b7\u53d6\u5de5\u4f5c\u6d41\u4efb\u52a1\u8282\u70b9\u7684\u72b6\u6001", type=LogType.PROCESS)
    @RequestMapping(value={"/bpmn-task-status"}, method={RequestMethod.POST})
    public CoreBpmnTaskStatusDTO selectBpmnTaskStatus(RestJsonWrapperBean wrapper) {
        return this.getService().selectBpmnTaskStatus(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u5ba1\u6838\u62d2\u7edd", type=LogType.PROCESS)
    @RequestMapping(value={"/action/reject-task"}, method={RequestMethod.PUT})
    public void rejectTask(RestJsonWrapperBean wrapper) {
        this.getService().rejectTask(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u5f02\u5e38\u62d2\u7edd", type=LogType.PROCESS)
    @RequestMapping(value={"/action/odd-reject-task"}, method={RequestMethod.PUT})
    public void oddRejectTask(RestJsonWrapperBean wrapper) {
        this.getService().oddRejectTask(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u56de\u9000", type=LogType.PROCESS)
    @RequestMapping(value={"/action/undo"}, method={RequestMethod.PUT})
    public void undo(RestJsonWrapperBean wrapper) {
        this.getService().undo(wrapper);
    }
}
