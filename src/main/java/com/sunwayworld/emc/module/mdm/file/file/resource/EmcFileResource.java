package com.sunwayworld.emc.module.mdm.file.file.resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sunwayworld.emc.module.mdm.file.file.bean.EmcFileBean;
import com.sunwayworld.emc.module.mdm.file.file.bean.EmcFileVersionBean;
import com.sunwayworld.emc.module.mdm.file.file.bean.EmcFileVersionDistBean;
import com.sunwayworld.emc.module.mdm.file.file.service.EmcFileService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

/**
 * @author zhengxy@sunwayworld.com 2019年10月09日
 */
@RequestMapping("/secure/emc/module/mdm/file/files")
public interface EmcFileResource extends GenericResource<EmcFileService, EmcFileBean, Long>, GenericChoosableResource<EmcFileService, EmcFileBean,Long> {

    @RequestMapping(value = "/{id}/versions/queries", method = RequestMethod.POST)
    Page<EmcFileVersionBean> selectFileVersionPaginationByFileId(@PathVariable Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/versions/{versionId}/dist/queries", method = RequestMethod.POST)
    Page<EmcFileVersionDistBean> selectFileVersionDistPaginationByVersionId(@PathVariable Long versionId, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{id}/versions", method = RequestMethod.POST)
    Long insertFileVersion(@PathVariable Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{id}/versions", method = RequestMethod.DELETE)
    void deleteFileVersion(@PathVariable Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/versions/{versionId}/dist", method = RequestMethod.POST)
    Long insertFileVersionDist(@PathVariable Long versionId, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/versions/{versionId}/dist", method = RequestMethod.DELETE)
    void deleteFileVersionDist(@PathVariable Long versionId, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{id}/action/get-url", method = RequestMethod.POST)
    String getDownloadUrl(Long id, RestJsonWrapperBean wrapper);
}
