package com.sunwayworld.emc.module.bp.bp.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */
@RequestMapping("/secure/emc/module/bp/bp/folders")
public interface EmcFolderResource extends GenericResource<EmcFolderService, EmcFolderBean, Long>,
                                           GenericAuditableResource<EmcFolderService, EmcFolderBean, Long>,
                                           GenericSelectableResource<EmcFolderService, EmcFolderBean, Long> {

    void updatProjectStatus(RestJsonWrapperBean jsonWrapper);

    void undoToAccept(RestJsonWrapperBean jsonWrapper);

    void terminateFolder(RestJsonWrapperBean jsonWrapper);

    void copyFolders(String folderNo, RestJsonWrapperBean wrapper);

    void dataSharing(RestJsonWrapperBean wrapper);

    Page<EmcFolderBean> querySamplePlans(RestJsonWrapperBean wrapper);
}
