/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filemanage.resource;

import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManageAuthBean;
import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManageBean;
import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManagePrintRecorBean;
import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManageReadRecordBean;
import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManageVersionBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnTaskStatusDTO;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/qms/file-manages"})
public interface CoreFileManageResource
extends GenericCloudResource<CoreFileManageBean, Long> {
    public Long insertManageVersion(Long var1, RestJsonWrapperBean var2);

    public void deleteManageVersion(Long var1, RestJsonWrapperBean var2);

    public Page<CoreFileManageAuthBean> selectManageAuthPaginationByFileManageId(Long var1, RestJsonWrapperBean var2);

    public List<Long> insertManageAuth(Long var1, RestJsonWrapperBean var2);

    public void deleteManageAuth(Long var1, RestJsonWrapperBean var2);

    public Page<CoreFileManageReadRecordBean> selectManageReadRecordPaginationByFileManageId(Long var1, RestJsonWrapperBean var2);

    public Long insertManageReadRecord(Long var1, RestJsonWrapperBean var2);

    public void deleteManageReadRecord(Long var1, RestJsonWrapperBean var2);

    public Page<CoreFileManagePrintRecorBean> selectManagePrintRecorPaginationByFileManageId(Long var1, RestJsonWrapperBean var2);

    public Long insertManagePrintRecor(Long var1, RestJsonWrapperBean var2);

    public void deleteManagePrintRecor(Long var1, RestJsonWrapperBean var2);

    public String getFileNo(Long var1, RestJsonWrapperBean var2);

    public CoreFileManageVersionBean selectVersionDetail(Long var1, RestJsonWrapperBean var2);

    public Page<CoreFileManageVersionBean> selectManageVersionPaginationByFileManageId(Long var1, RestJsonWrapperBean var2);

    @Override
    public RestValidationResultBean validateUnique(RestJsonWrapperBean var1);

    public void batchFileLink(RestJsonWrapperBean var1);

    public Page<CoreFileManageBean> selectAuditablePagination(RestJsonWrapperBean var1);

    public void startProcess(RestJsonWrapperBean var1);

    public void withdrawProcess(RestJsonWrapperBean var1);

    public void completeTask(RestJsonWrapperBean var1);

    public void endTask(RestJsonWrapperBean var1);

    public CoreBpmnTaskStatusDTO selectBpmnTaskStatus(RestJsonWrapperBean var1);

    public void rejectTask(RestJsonWrapperBean var1);

    public void oddRejectTask(RestJsonWrapperBean var1);

    public void undo(RestJsonWrapperBean var1);
}
