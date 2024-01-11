package com.sunwayworld.emc.module.mdm.file.file.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.file.file.bean.EmcFileVersionBean;
import com.sunwayworld.emc.module.mdm.file.file.bean.EmcFileVersionDistBean;
import com.sunwayworld.emc.module.mdm.file.file.resource.EmcFileResource;
import com.sunwayworld.emc.module.mdm.file.file.service.EmcFileService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2019年10月09日
 */
@LogModule(value = "文件管理")
@RestController
@GikamBean
public class EmcFileResourceImpl implements EmcFileResource {

    @Autowired
    private EmcFileService fileService;

    @Override
    public EmcFileService getService() {
        return fileService;
    }

    @Override
    @Log(value = "查询文件版本", type = LogType.SELECT)
    public Page<EmcFileVersionBean> selectFileVersionPaginationByFileId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectFileVersionPaginationByFileId(id, wrapper);
    }

    @Override
    @Log(value = "查询文件分发回收记录", type = LogType.SELECT)
    public Page<EmcFileVersionDistBean> selectFileVersionDistPaginationByVersionId(@PathVariable Long versionId, RestJsonWrapperBean wrapper) {
        return getService().selectFileVersionDistPaginationByVersionId(versionId, wrapper);
    }

    @Override
    @Log(value = "新增文件版本", type = LogType.INSERT)
    public Long insertFileVersion(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().insertFileVersion(id, wrapper);
    }

    @Override
    @Log(value = "删除文件版本", type = LogType.DELETE)
    public void deleteFileVersion(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        getService().deleteFileVersion(id, wrapper);
    }

    @Override
    @Log(value = "新增文件分发回收记录", type = LogType.INSERT)
    public Long insertFileVersionDist(@PathVariable Long versionId, RestJsonWrapperBean wrapper) {
        return getService().insertFileVersionDist(versionId, wrapper);
    }

    @Override
    @Log(value = "删除文件分发回收记录", type = LogType.DELETE)
    public void deleteFileVersionDist(@PathVariable Long versionId, RestJsonWrapperBean wrapper) {
        getService().deleteFileVersionDist(versionId, wrapper);
    }
    
    @Override
    @Log(value = "获取文件下载路径", type = LogType.SELECT)
    public String getDownloadUrl(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().getDownloadUrl(id, wrapper);
    }
}
