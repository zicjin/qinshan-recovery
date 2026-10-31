package com.sunwayworld.emc.module.bp.bp.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.resource.EmcFolderResource;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

@LogModule("点位")
@RestController
@GikamBean
public class EmcFolderResourceImpl implements EmcFolderResource {

    @Autowired
    private EmcFolderService folderService;

    @Override
    public EmcFolderService getService() {
        return folderService;
    }

    @Override
    @Log(value = "修改项目状态为取消", type = LogType.UPDATE)
    @RequestMapping(value = "/action/update-project-status", method = RequestMethod.PUT)
    public void updatProjectStatus(RestJsonWrapperBean jsonWrapper) {
        getService().updatProjectStatus(jsonWrapper);
    }

    @Log(value = "终止/取消终止点位", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/action/terminate-folder", method = RequestMethod.PUT)
    public void terminateFolder(RestJsonWrapperBean jsonWrapper) {
        folderService.terminateFolder(jsonWrapper);
    }

    @Override
    @Log(value = "回退到样品接收", type = LogType.UPDATE)
    @RequestMapping(value = "/action/undo-to-accept", method = RequestMethod.PUT)
    public void undoToAccept(RestJsonWrapperBean jsonWrapper) {
        getService().undoToAccept(jsonWrapper);
    }

    @Override
    @Log(value = "复制点位", type = LogType.INSERT)
    @RequestMapping(value = "/{folderNo}/action/copy-folders", method = RequestMethod.POST)
    public void copyFolders(@PathVariable String folderNo, RestJsonWrapperBean wrapper) {
        getService().copyFolders(folderNo, wrapper);
    }

    @Override
    @Log(value = "数据共享", type = LogType.UPDATE)
    @RequestMapping(value = "/action/data-sharing", method = RequestMethod.POST)
    public void dataSharing(RestJsonWrapperBean wrapper) {
        getService().dataSharing(wrapper);
    }

    @Override
    @Log(value = "查询采样安排", type = LogType.SELECT)
    @RequestMapping(value = "/action/query-sample-plans", method = RequestMethod.POST)
    public Page<EmcFolderBean> querySamplePlans(RestJsonWrapperBean wrapper) {
        return getService().querySamplePlans(wrapper);
    }
}
