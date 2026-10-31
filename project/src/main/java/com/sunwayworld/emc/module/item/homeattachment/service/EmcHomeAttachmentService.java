package com.sunwayworld.emc.module.item.homeattachment.service;

import com.sunwayworld.emc.module.item.homeattachment.bean.EmcHomeAttachmentBean;
import com.sunwayworld.emc.module.item.homeattachment.bean.EmcHomeAttachmentPermissionBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcHomeAttachmentService extends GenericService<EmcHomeAttachmentBean, Long>, GenericActivatableService<EmcHomeAttachmentBean, Long> {

    void insertAttachmentPermission(RestJsonWrapperBean wrapper);

    void deleteAttachmentPermission(RestJsonWrapperBean wrapper);

    Page<EmcHomeAttachmentPermissionBean> selectPermissionPagination(Long id, RestJsonWrapperBean wrapper);

    String getDownloadUrl(Long id, RestJsonWrapperBean wrapper);

    void batchEmpower(RestJsonWrapperBean wrapper);
}
