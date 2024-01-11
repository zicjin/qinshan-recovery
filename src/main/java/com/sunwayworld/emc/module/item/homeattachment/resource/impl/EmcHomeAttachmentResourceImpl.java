package com.sunwayworld.emc.module.item.homeattachment.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.homeattachment.bean.EmcHomeAttachmentPermissionBean;
import com.sunwayworld.emc.module.item.homeattachment.resource.EmcHomeAttachmentResource;
import com.sunwayworld.emc.module.item.homeattachment.service.EmcHomeAttachmentService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("附件信息表")
@RestController
@GikamBean
public class EmcHomeAttachmentResourceImpl implements EmcHomeAttachmentResource {

    @Autowired
    private EmcHomeAttachmentService emcHomeAttachmentService;

    @Override
    public EmcHomeAttachmentService getService() {
        return emcHomeAttachmentService;
    }

    @Log(value = "新增附件信息表", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Override
    @Log(value = "新增人员权限", type = LogType.INSERT)
    @RequestMapping(value = "/permissions", method = RequestMethod.POST)
    public void insertAttachmentPermission(RestJsonWrapperBean wrapper) {
        getService().insertAttachmentPermission(wrapper);
    }

    @Override
    @Log(value = "删除人员权限", type = LogType.DELETE)
    @RequestMapping(value = "/permissions", method = RequestMethod.DELETE)
    public void deleteAttachmentPermission(RestJsonWrapperBean wrapper) {
        getService().deleteAttachmentPermission(wrapper);
    }

    @Override
    @Log(value = "查询人员权限", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/permissions/queries", method = RequestMethod.POST)
    public Page<EmcHomeAttachmentPermissionBean> selectPermissionPagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectPermissionPagination(id, wrapper);
    }

    @Override
    @Log(value = "获取文件下载路径", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/action/get-url", method = RequestMethod.POST)
    public String getDownloadUrl(@PathVariable Long id, RestJsonWrapperBean wrapper) {

        return getService().getDownloadUrl(id, wrapper);
    }

    @Override
    @Log(value = "批量授权", type = LogType.INSERT)
    @RequestMapping(value = "/action/empower", method = RequestMethod.POST)
    public void batchEmpower(RestJsonWrapperBean wrapper) {
        getService().batchEmpower(wrapper);

    }

}
