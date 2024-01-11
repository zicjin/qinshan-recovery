package com.sunwayworld.emc.module.item.homeattachment.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.homeattachment.bean.EmcHomeAttachmentBean;
import com.sunwayworld.emc.module.item.homeattachment.bean.EmcHomeAttachmentPermissionBean;
import com.sunwayworld.emc.module.item.homeattachment.service.EmcHomeAttachmentService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/item/home-attachments")
public interface EmcHomeAttachmentResource extends GenericResource<EmcHomeAttachmentService, EmcHomeAttachmentBean, Long>, GenericActivatableResource<EmcHomeAttachmentService, EmcHomeAttachmentBean, Long> {

    void insertAttachmentPermission(RestJsonWrapperBean wrapper);

    void deleteAttachmentPermission(RestJsonWrapperBean wrapper);

    Page<EmcHomeAttachmentPermissionBean> selectPermissionPagination(Long id, RestJsonWrapperBean wrapper);

    String getDownloadUrl(Long id, RestJsonWrapperBean wrapper);

    void batchEmpower(RestJsonWrapperBean wrapper);
}
